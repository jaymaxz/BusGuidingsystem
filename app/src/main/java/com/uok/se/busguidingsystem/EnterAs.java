package com.uok.se.busguidingsystem;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class EnterAs extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enter_as);
        final Button button = findViewById(R.id.btnDriver);
        final Button btnPas = findViewById(R.id.btnPessenger);
        final Button btnOwn = findViewById(R.id.btnOwner);
        final Button btnOth = findViewById(R.id.btnOther);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Code here executes on main thread after user presses button
                Intent map = new Intent(EnterAs.this,  MapsActivity.class);
                startActivity(map);
            }
        });
        btnPas.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Code here executes on main thread after user presses button
                Intent map = new Intent(EnterAs.this,  MapsActivity.class);
                startActivity(map);
            }
        });
        btnOwn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Code here executes on main thread after user presses button
                Intent map = new Intent(EnterAs.this,  MapsActivity.class);
                startActivity(map);
            }
        });
        btnPas.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Code here executes on main thread after user presses button
                Intent map = new Intent(EnterAs.this,  MapsActivity.class);
                startActivity(map);
            }
        });
    }
}
