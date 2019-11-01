package com.example.encript_git;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.util.Xml;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.xmlpull.v1.XmlSerializer;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Scanner;

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

        button_create_xml.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                escribirArchivo();
                leerArchivo();
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

    //metodo escritura de archivo prueba de escritura en XML
    public void escribirArchivo(){
        String encriptado = this.textEncoded.getText().toString();
        String desencriptado = this.textDecoded.getText().toString();
        int cont =1;

        //tratamiento de la fecha y la hora
        Date date = new Date();
        DateFormat hourdateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        ArrayList<String> array = new ArrayList<String>();
        array.add(encriptado);
        array.add(desencriptado);
        try{
            OutputStreamWriter archivo = new OutputStreamWriter(openFileOutput("prueba.xml", Context.MODE_APPEND));
            archivo.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
            archivo.write("\n");
            archivo.write("<content_file>");
            archivo.write("\n");
            archivo.write("     <datos "+ "id='"+cont+"'>");
            archivo.write("\n");
            archivo.write("         <time>"+hourdateFormat.format(date)+"</time>");
            archivo.write("\n");
            for (int i = 0; i < array.size(); i++){

                if (i == array.size()-1){
                    archivo.write("         <desencriptado>"+array.get(i)+"</desencriptado>");
                    archivo.write("\n");
                }
                else{
                    archivo.write("         <encriptado>"+array.get(i)+"</encriptado>");
                    archivo.write("\n");
                }
                cont++;
            }
            archivo.write("     </datos>");
            archivo.write("\n");
            archivo.write("</content_file>");
            archivo.close();

            leerArchivo();
        }catch (Exception e){
            Log.e("Archivo", "error al escribir archivo");
        }

    }

    //metodo para leer archivos
    public void leerArchivo(){
        try{
            BufferedReader aux = new BufferedReader(new InputStreamReader(openFileInput("prueba.xml")));
            String texto = aux.readLine();
            Toast.makeText(this, "texto en "+ texto, Toast.LENGTH_LONG).show();
        }catch (Exception e) {
            Log.e("Archivo", "error al leer el archivo");
            e.printStackTrace();
        }
    }
}
