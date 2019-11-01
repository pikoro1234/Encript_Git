package com.example.encript_git;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {
    //variables de bottones y textview
    private Button button;
    private Button button_salida;
    private Button button_create_xml;
    private TextView text;
    private TextView textEncoded;
    private TextView textDecoded;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //asignado valor a elementos existentes en el DOM mediante el ID
        this.text = findViewById(R.id.textEntrada);
        this.textEncoded = findViewById(R.id.textSalida);
        this.textDecoded = findViewById(R.id.textDesencriptado);
        this.button = findViewById(R.id.btnEntrada);
        this.button_salida = findViewById(R.id.btnSalida);
        this.button_create_xml = findViewById(R.id.btnXml);
        //boton con el cual llama la funcion de encriptado y ejecuta al click
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                crypt(v);
            }
        });

        //boton llamado a la funcion desencriptado al detectar evento click
        button_salida.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                descrypt(v);
            }
        });
    }

    //metodo de encriptado
    public void crypt(View view) {

        try {
            String password = this.text.getText().toString();
            String encodedText;
            RSA encodeRsa = new RSA();

            encodeRsa.setContext(getBaseContext());
            encodeRsa.genKeyPair(1024);
            encodeRsa.saveToDiskPrivateKey("rsa.pri");
            encodeRsa.saveToDiskPublicKey("rsa.pub");
            encodedText = encodeRsa.Encrypt(password);
            textEncoded.setText(encodedText);

           /* writeToFile(createXML(password, encodedText, getApplicationContext()), getApplicationContext());
            readFromFile(getApplicationContext());*/

        }catch (Exception e) {
            System.out.println("An error ocurred encrypting the password");
        }
    }

    //metodo de desencriptado
    public void descrypt(View view){
        try{
            String password_desct = this.textEncoded.getText().toString(); //this.text.getText().toString();
            String decodedText;
            RSA decodeRsa = new RSA();

            decodeRsa.setContext(getBaseContext());
            decodeRsa.openFromDiskPrivateKey("rsa.pri");
            decodeRsa.openFromDiskPublicKey("rsa.pub");
            decodedText = decodeRsa.Decrypt(password_desct);
            textDecoded.setText(decodedText);
        }catch (Exception e){
            System.out.println("error al desencriptar");
        }
    }


}
