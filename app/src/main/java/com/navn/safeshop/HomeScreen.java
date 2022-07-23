package com.navn.safeshop;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class HomeScreen extends AppCompatActivity {
    private Button startButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_screen);
        startButton = (Button)findViewById(R.id.StartButton);
        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OpenMapsActivity();
            }
        });
    }
    public void OpenMapsActivity(){
        Intent intent  = new Intent(this, MapsActivity.class);
        startActivity(intent);
    }
}