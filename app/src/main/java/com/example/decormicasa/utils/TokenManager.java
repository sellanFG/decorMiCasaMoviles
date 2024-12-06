// TokenManager.java
package com.example.decormicasa.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.CountDownTimer;
import android.util.Base64;
import android.widget.Toast;

import com.example.decormicasa.login.LoginActivity;

import org.json.JSONObject;

import java.util.Date;

public class TokenManager {
    private static final long WARNING_TIME = 5 * 60 * 1000; // 5 minutos en millisegundos
    private Context context;
    private CountDownTimer timer;
    private TokenExpirationListener listener;

    public interface TokenExpirationListener {
        void onTokenWarning(long timeRemaining);
        void onTokenExpired();
    }

    public TokenManager(Context context, TokenExpirationListener listener) {
        this.context = context;
        this.listener = listener;
    }

    public void startTokenExpirationTimer(String token) {
        if (timer != null) {
            timer.cancel();
        }

        long expirationTime = getTokenExpirationTime(token);
        if (expirationTime == -1) {
            return;
        }

        long currentTime = new Date().getTime();
        long timeUntilExpiration = expirationTime - currentTime;

        if (timeUntilExpiration <= 0) {
            listener.onTokenExpired();
            return;
        }

        timer = new CountDownTimer(timeUntilExpiration, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                if (millisUntilFinished <= WARNING_TIME) {
                    listener.onTokenWarning(millisUntilFinished);
                }
            }

            @Override
            public void onFinish() {
                listener.onTokenExpired();
                logout();
            }
        }.start();
    }

    private long getTokenExpirationTime(String token) {
        try {
            String[] parts = token.split("\\.");
            String payload = new String(Base64.decode(parts[1], Base64.URL_SAFE));
            JSONObject jsonPayload = new JSONObject(payload);
            return jsonPayload.getLong("exp") * 1000; // Convertir a millisegundos
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }

    public void logout() {
        if (timer != null) {
            timer.cancel();
        }

        SharedPreferences sharedPreferences = context.getSharedPreferences("decorMiCasa", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();

        Intent intent = new Intent(context, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        context.startActivity(intent);

        if (context instanceof Activity) {
            ((Activity) context).finish();
        }
    }

    public void stopTimer() {
        if (timer != null) {
            timer.cancel();
        }
    }
}