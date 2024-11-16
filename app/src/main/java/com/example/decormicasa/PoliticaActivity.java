package com.example.decormicasa;

import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class PoliticaActivity extends AppCompatActivity  {

    private TextView sectionContent;
    private ImageView arrowIcon;
    private boolean isExpanded = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_privacidad);

        sectionContent = findViewById(R.id.sectionContent);
        arrowIcon = findViewById(R.id.arrowIcon);

        findViewById(R.id.toggleCard).setOnClickListener(v -> toggleSection());
    }

    private void toggleSection() {
        if (isExpanded) {
            sectionContent.setVisibility(View.GONE);
            Animation rotateDown = AnimationUtils.loadAnimation(this, R.anim.rotate_down);
            arrowIcon.startAnimation(rotateDown);
        } else {
            sectionContent.setVisibility(View.VISIBLE);
            Animation rotateUp = AnimationUtils.loadAnimation(this, R.anim.rotate_up);
            arrowIcon.startAnimation(rotateUp);
        }
        isExpanded = !isExpanded;
    }
}