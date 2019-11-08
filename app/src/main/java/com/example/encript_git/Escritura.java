package com.example.encript_git;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class Escritura extends AppCompatActivity {
    TextView text;
    String texto;
    Button boton_atras;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_escritura);

        text = (TextView)  findViewById(R.id.texto);
        Bundle bundle = this.getIntent().getExtras();
        texto = bundle.getString("text");
        text.setText(texto);
        boton_atras = (Button) findViewById(R.id.btn_atras);
        boton_atras.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent (getApplicationContext(), MainActivity.class);
                startActivity(intent);
            }
        });
    }
}
