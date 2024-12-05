package com.example.decormicasa.login;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.decormicasa.AdminActivity;
import com.example.decormicasa.ClienteActivity;
import com.example.decormicasa.Interface.decorMiCasaApi;
import com.example.decormicasa.R;
import com.example.decormicasa.RegisterActivity;
import com.example.decormicasa.databinding.ActivityLoginBinding;
import com.example.decormicasa.model.AuthRequest;
import com.example.decormicasa.model.AuthResponse;
import com.example.decormicasa.model.MetodoEnc;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class LoginActivity extends AppCompatActivity {
    String token;
    private LoginViewModel loginViewModel;
    private ActivityLoginBinding binding;
    private String storedVerificationId;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        loginViewModel = new ViewModelProvider(this, new LoginViewModelFactory())
                .get(LoginViewModel.class);

        final ProgressBar loadingProgressBar = binding.loading;

        // Agregar las referencias de controles en MDC
        final TextInputLayout passwordTextInputMDC = binding.passwordTextInput;
        final TextInputLayout usernameTextInputMDC = binding.usernameTextInput;
        final TextInputEditText passwordEditTextMDC = findViewById(R.id.password_edit_text);
        final TextInputEditText usernameEditTextMDC = findViewById(R.id.username_edit_text);
        MaterialButton nextButtonMDC = binding.nextButton;
        MaterialButton cancelButtonMDC = binding.cancelButton;

        loginViewModel.getLoginResult().observe(this, new Observer<LoginResult>() {
            @Override
            public void onChanged(@Nullable LoginResult loginResult) {
                if (loginResult == null) {
                    return;
                }
                loadingProgressBar.setVisibility(View.GONE);
                if (loginResult.getError() != null) {
                    showLoginFailed(loginResult.getError());
                }
                if (loginResult.getSuccess() != null) {
                    updateUiWithUser(loginResult.getSuccess());
                }
                setResult(Activity.RESULT_OK);

                //Complete and destroy login activity once successful
                finish();
            }
        });

        binding.nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!isPasswordValid(passwordEditTextMDC.getText())) {
                    passwordTextInputMDC.setError(getString(R.string.error_password));
                }
                else{
                    binding.loading.setVisibility(View.VISIBLE);
                    String ePassword = MetodoEnc.encryptPassword(passwordEditTextMDC.getText().toString());
                    iniciarSesionPyAny(usernameEditTextMDC.getText().toString(), ePassword);
                }
            }
        });

        passwordEditTextMDC.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                if (isPasswordValid(passwordEditTextMDC.getText())) {
                    passwordTextInputMDC.setError(null); //Clear the error
                }
                return false;
            }
        });

        cancelButtonMDC.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Inicia RegisterActivity cuando se hace clic en el botón de registro
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });
    }

    private boolean isPasswordValid(@Nullable Editable text) {
        return text != null && text.length() >= 6;
    }

    private void iniciarSesionPyAny(String username, String password) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(getString(R.string.dominioservidor))
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        decorMiCasaApi decorMiCasaApi = retrofit.create(decorMiCasaApi.class);
        AuthRequest authRequest = new AuthRequest();
        authRequest.setUsername(username);
        authRequest.setPassword(password);

        Call<AuthResponse> call = decorMiCasaApi.autenticar(authRequest);
        call.enqueue(new Callback<AuthResponse>() {
            @Override
            public void onResponse(Call<AuthResponse> call, Response<AuthResponse> response) {
                binding.loading.setVisibility(View.INVISIBLE); // Ocultar cargando siempre que haya respuesta

                if (!response.isSuccessful()) {
                    Toast.makeText(LoginActivity.this, "Error en la autenticación", Toast.LENGTH_LONG).show();
                    return;
                }

                AuthResponse authResponse = response.body();
                if (authResponse == null || authResponse.getAccess_token() == null || authResponse.getRol() == null) {
                    Toast.makeText(LoginActivity.this, "Error: Respuesta no válida", Toast.LENGTH_LONG).show();
                    return;
                }

                // Asignamos token y rol
                token = authResponse.getAccess_token();
                String userRole = authResponse.getRol();
                int id = authResponse.getId();

                // Guardar token y rol en SharedPreferences
                SharedPreferences sharedPreferences = getSharedPreferences("decorMiCasa", MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("tokenJWT", token);
                editor.putString("userRole", userRole);
                editor.putInt("id_cliente", id);
                Log.e("ID", String.valueOf(id));
                editor.apply();

                // Redireccionar según el rol
                if ("cliente".equalsIgnoreCase(userRole)) {
                    //auth por telefono solo en cliente
                    mostrarDialogoTelefono();
                    //Intent intent = new Intent(LoginActivity.this, ClienteActivity.class);
                    //startActivity(intent);
                    //finish(); // Finalizar la actividad de inicio de sesión
                } else if ("administrador".equalsIgnoreCase(userRole)) {
                    Intent intent = new Intent(LoginActivity.this, AdminActivity.class);
                    startActivity(intent);
                    finish(); // Finalizar la actividad de inicio de sesión
                } else {
                    Toast.makeText(LoginActivity.this, "Rol no reconocido", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<AuthResponse> call, Throwable t) {
                binding.loading.setVisibility(View.INVISIBLE);
                Toast.makeText(LoginActivity.this, "Error de red: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }


    private void updateUiWithUser(LoggedInUserView model) {
        String welcome = getString(R.string.welcome) + model.getDisplayName();
        // TODO : initiate successful logged in experience
        Toast.makeText(getApplicationContext(), welcome, Toast.LENGTH_LONG).show();
    }

    private void showLoginFailed(@StringRes Integer errorString) {
        Toast.makeText(getApplicationContext(), errorString, Toast.LENGTH_SHORT).show();
    }

    private void mostrarDialogoTelefono() {
        // Mostrar un cuadro de diálogo para que el usuario ingrese su número de teléfono
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Ingresa tu número de teléfono");

        final EditText telefonoInput = new EditText(this);
        telefonoInput.setInputType(InputType.TYPE_CLASS_PHONE);
        telefonoInput.setHint("+51");
        builder.setView(telefonoInput);

        builder.setPositiveButton("Enviar código", (dialog, which) -> {
            String numeroTelefono = telefonoInput.getText().toString();
            if (!numeroTelefono.isEmpty()) {
                // Llamar a la función que envía el código SMS
                if (!numeroTelefono.startsWith("+")) {
                    numeroTelefono = "+51" + numeroTelefono; // Añade el código de país si no lo tiene
                }
                iniciarSesionConTelefono(numeroTelefono);
            } else {
                Toast.makeText(LoginActivity.this, "Por favor, ingrese un número de teléfono válido.", Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNegativeButton("Cancelar", (dialog, which) -> dialog.cancel());
        builder.show();
    }

    private void iniciarSesionConTelefono(String numeroTelefono) {
        FirebaseAuth mAuth = FirebaseAuth.getInstance();

        // Paso 1: Iniciar la autenticación telefónica
        PhoneAuthOptions options = PhoneAuthOptions.newBuilder(mAuth)
                .setPhoneNumber(numeroTelefono)
                .setTimeout(60L, TimeUnit.SECONDS)
                .setActivity(this)
                .setCallbacks(new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                    @Override
                    public void onVerificationCompleted(PhoneAuthCredential credential) {
                        // Verificación automática exitosa
                        signInWithPhoneAuthCredential(credential);
                    }

                    @Override
                    public void onVerificationFailed(FirebaseException e) {
                        // Manejar errores de verificación
                        Toast.makeText(LoginActivity.this, "Error en la verificación telefónica: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onCodeSent(@NonNull String verificationId, @NonNull PhoneAuthProvider.ForceResendingToken token) {
                        // El código fue enviado, guarda el verificationId
                        Toast.makeText(LoginActivity.this, "Código enviado", Toast.LENGTH_SHORT).show();
                        storedVerificationId = verificationId;
                        mostrarDialogoCodigoSMS();
                    }
                })
                .requireSmsValidation(false)
                .build();
        PhoneAuthProvider.verifyPhoneNumber(options);
    }

    private void mostrarDialogoCodigoSMS() {
        // Mostrar dialog de ingreso del código SMS
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Ingresa el código SMS");

        final EditText codigoInput = new EditText(this);
        codigoInput.setInputType(InputType.TYPE_CLASS_NUMBER);
        builder.setView(codigoInput);

        builder.setPositiveButton("Verificar", (dialog, which) -> {
            String codigoSMS = codigoInput.getText().toString();
            if (!codigoSMS.isEmpty()) {
                // Verificar el código SMS
                verificarCodigoSMS(codigoSMS);
            } else {
                Toast.makeText(LoginActivity.this, "Por favor, ingrese el código recibido.", Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNegativeButton("Cancelar", (dialog, which) -> dialog.cancel());
        builder.show();
    }

    private void verificarCodigoSMS(String codigoIngresado) {
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(storedVerificationId, codigoIngresado);
        signInWithPhoneAuthCredential(credential);
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        FirebaseAuth mAuth = FirebaseAuth.getInstance();

        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = task.getResult().getUser();
                        // Si es exitosa, redirige
                        Toast.makeText(LoginActivity.this, "Inicio de sesión exitoso", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(LoginActivity.this, ClienteActivity.class);
                        startActivity(intent);
                        finish();
                    } else {
                        Toast.makeText(LoginActivity.this, "Error en el inicio de sesión: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
    }

}