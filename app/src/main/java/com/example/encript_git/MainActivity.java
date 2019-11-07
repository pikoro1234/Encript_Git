package com.example.encript_git;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

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
        this.text = findViewById(R.id.textEntrada);
        this.textEncoded = findViewById(R.id.textSalida);
        this.textDecoded = findViewById(R.id.textDesencriptado);
        this.button = findViewById(R.id.btnEntrada);
        this.button_salida = findViewById(R.id.btnSalida);
        this.button_create_xml = findViewById(R.id.btnXml);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                crypt(v);
                //ocultamos el teclado al presionar boton para mayor visibilidad
                InputMethodManager inputMethodManager = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                inputMethodManager.hideSoftInputFromWindow(textEncoded.getWindowToken(), 0);
            }
        });

        button_salida.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                descrypt(v);
            }
        });

        button_create_xml.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                escribirArchivo();
                //vaciamos los text al crear xml
                text.setText("");
                textDecoded.setText("");
                textEncoded.setText("");
            }
        });
    }

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

        } catch (Exception e) {

            System.out.println("An error ocurred encrypting the password");
        }
    }

    public void descrypt(View view) {
        try {
            String password_desct = this.textEncoded.getText().toString(); //this.text.getText().toString();
            String decodedText;
            RSA decodeRsa = new RSA();

            decodeRsa.setContext(getBaseContext());
            decodeRsa.openFromDiskPrivateKey("rsa.pri");
            decodeRsa.openFromDiskPublicKey("rsa.pub");
            decodedText = decodeRsa.Decrypt(password_desct);
            textDecoded.setText(decodedText);
        } catch (Exception e) {

            System.out.println("An error ocurred desencrypting the password");
        }
    }

    public void escribirArchivo() {
        String encriptado = this.textEncoded.getText().toString();
        String desencriptado = this.textDecoded.getText().toString();
        int cont = 1;
        char simbolo = '"';
        Date date = new Date();
        DateFormat hourdateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.getDefault());

        if (!comprobamosArchivo()) {
            String xml = "<?xml version=" + simbolo + "1.0" + simbolo + "encoding=" + simbolo + "UTF-8" + simbolo + "?>\n" +
                    "<content_file>\n" +
                    "</content_file>\n";
            try {
                OutputStreamWriter archivo = new OutputStreamWriter(getApplication().openFileOutput("prueba.xml", Context.MODE_PRIVATE));
                archivo.write(xml);
                archivo.close();

            } catch (Exception e) {

                e.printStackTrace();
            }
        }
        if (comprobamosArchivo()) {
            try {
                InputStream lectura = getApplication().openFileInput("prueba.xml");
                BufferedReader aux = new BufferedReader(new InputStreamReader(lectura));
                String linea;
                String newDataXml = "";
                while ((linea = aux.readLine()) != null) {
                    if (linea.contains("id=")) {
                        cont++;
                    }
                    if (linea.equals("</content_file>")) {
                        newDataXml += "\t<data id=" + simbolo + cont + simbolo + ">\n" +
                                "\t\t<time>" + hourdateFormat.format(date) + "</time>\n" +
                                "\t\t<text>" + desencriptado + "</text>\n" +
                                "\t\t<cipher_text>" + encriptado + "</cipher_text>\n" +
                                "\t</data>\n" +
                                "</content_file>\n";
                    } else {
                        newDataXml += linea + "\n";
                    }
                }
                OutputStreamWriter archivo = new OutputStreamWriter(getApplication().openFileOutput("prueba.xml", Context.MODE_PRIVATE));
                archivo.write(newDataXml);
                archivo.close();
                lectura.close();
                Toast.makeText(this, "archivo XML creado", Toast.LENGTH_LONG).show();
            } catch (Exception e) {

                Log.e("Archivo", "error al escribir archivo");
            }
        }

    }

    private boolean comprobamosArchivo() {
        boolean estaCreado;
        try {
            InputStream lectura = getApplication().openFileInput("prueba.xml");
            lectura.close();
            estaCreado = true;
        } catch (Exception e) {
            estaCreado = false;
        }
        return estaCreado;
    }
}
