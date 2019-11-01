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
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.StringWriter;
import java.util.ArrayList;

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
    /*public void escribirArchivo(){
        String encriptado = this.textEncoded.getText().toString();
        String desencriptado = this.textDecoded.getText().toString();
        try{
            OutputStreamWriter archivo = new OutputStreamWriter(openFileOutput("prueba.xml", Context.MODE_PRIVATE));

            for (int i = 0; i<encriptado.length(); i++){
                if (archivo.length()){
                    archivo.write("\n");
                }
                archivo.write(encriptado.charAt(i));
            }

            archivo.write(encriptado);
            archivo.write(desencriptado);

            archivo.close();

            leerArchivo();
        }catch (Exception e){
            Log.e("Archivo", "error al escribir archivo");
        }

    }*/

    public void escribirArchivo(){
        final String xmlFile = "Datos";
        String encriptation = "encriptation";
        String desencriptation = "desencriptation";

        String texto_encriptado = this.textEncoded.getText().toString();
        String texto_desencriptado = this.textDecoded.getText().toString();
        try{
            FileOutputStream fos = new FileOutputStream("newprueba.xml");
            FileOutputStream fileos = getApplicationContext().openFileOutput(xmlFile, Context.MODE_PRIVATE);
            XmlSerializer xmlSerializer = Xml.newSerializer();
            StringWriter writer = new StringWriter();
            xmlSerializer.setOutput(writer);
            xmlSerializer.startDocument("UTF-8",true);
            xmlSerializer.startTag(null, "Datos");
            xmlSerializer.startTag(null, "encriptation");
            xmlSerializer.text(texto_encriptado);
            xmlSerializer.endTag(null, "encriptation");
            xmlSerializer.startTag(null, "desencriptation");
            xmlSerializer.text(texto_desencriptado);
            xmlSerializer.endTag(null, "desencriptation");
            xmlSerializer.endTag(null, "Datos");
            xmlSerializer.endDocument();
            xmlSerializer.flush();
            String dataWrite = writer.toString();
            fileos.write(dataWrite.getBytes());
            fileos.close();
            leerArchivo();


        }catch (FileNotFoundException e){
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }catch (IllegalArgumentException e){
            e.printStackTrace();
        }catch (IllegalStateException e){
            e.printStackTrace();
        }

    }

    //metodo para leer archivos
    public void leerArchivo(){
        final String xmlFile = "Datos";
        ArrayList<String> Datos = new ArrayList<String>();
        try{
             FileInputStream fis = getApplicationContext().openFileInput(xmlFile);
             InputStreamReader isr = new InputStreamReader(fis);
             char[] inputBuffer = new char[fis.available()];
             isr.read(inputBuffer);
             String data = new String(inputBuffer);
             isr.close();
             fis.close();
        }catch (FileNotFoundException e3){
            Log.e("Datos", "error de lectura archivo catch 1");
        }
        catch (IOException e){
            Log.e("Datos", "error al leer archivo cath 2");
        }
    }
   /* public void leerArchivo(){
        try{
            BufferedReader aux = new BufferedReader(new InputStreamReader(openFileInput("newprueba.xml")));
            String texto = aux.readLine();
            Toast.makeText(this, "texto en "+ texto, Toast.LENGTH_LONG).show();
        }catch (Exception e) {
            Log.e("Archivo", "error al leer el archivo");
        }
    }*/
}
