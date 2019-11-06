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
import java.io.InputStream;
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

        //CONSTRUCCION DE ESQUEMA PRINCIPAL
        String contenedor="<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"+
                "<content_file>\n"+
                "\t<datos id = '1'>\n"+
                "\t\t<time>"+hourdateFormat.format(date)+"</time>\n"+
                "\t\t<encriptado>"+encriptado+"</encriptado>\n"+
                "\t\t<desencriptado>"+desencriptado+"</desencriptado>\n"+
                "\t</datos>\n"+
                "</content_file>\n";
        try{
            OutputStreamWriter archivo = new OutputStreamWriter(getApplication().openFileOutput("prueba.xml", Context.MODE_APPEND));
            archivo.write(contenedor);
            archivo.close();
            leerArchivo();
        }catch (Exception e){
            Log.e("Archivo", "error al escribir archivo");
        }

    }

    //metodo para leer archivos
    public void leerArchivo(){
        //tratamiento de la fecha y la hora
        Date date = new Date();
        DateFormat hourdateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        String encriptado = this.textEncoded.getText().toString();
        String desencriptado = this.textDecoded.getText().toString();
        try{
            InputStream lectura = getApplication().openFileInput("prueba.xml");
            BufferedReader aux = new BufferedReader(new InputStreamReader(lectura));
            StringBuilder objeto = new StringBuilder();
            String linea;
            String archivo_concat = "";
            while ((linea = aux.readLine()) !=null){
                if (linea.equals("</content_file>")){
                    archivo_concat +="<data id='2'>\n"+
                    "\t\t<time>"+hourdateFormat.format(date)+"</time>\n"+
                    "\t\t<encriptado>"+encriptado+"</encriptado>\n"+
                    "\t\t<desencriptado>"+desencriptado+"</desencriptado>\n"+
                    "\t</data>\n"+
                    "</content_file>\n";
                    objeto.append(linea);
                }else{
                    objeto.append(linea).append("\n");
                }

            }
            Toast.makeText(this, "texto en "+ archivo_concat, Toast.LENGTH_LONG).show();
        }catch (Exception e) {
            Log.e("Archivo", "error al leer el archivo");
            e.printStackTrace();
        }
    }
}
