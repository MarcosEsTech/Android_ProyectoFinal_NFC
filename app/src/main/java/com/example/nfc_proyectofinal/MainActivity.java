package com.example.nfc_proyectofinal;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.FormatException;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.Ndef;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.nio.charset.Charset;

public class MainActivity extends AppCompatActivity {

    EditText messageToWrite;
    TextView messageToRead;
    Button btnWrite;

    NfcAdapter nfcAdapter;

    Tag tag;
    Ndef ndef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_main );

        init();
        initNFC();
    }

    private void init(){
        messageToWrite = findViewById(R.id.messageToWrite);
        messageToRead = findViewById(R.id.messageToRead);
        btnWrite = findViewById(R.id.btnWrite);

        btnWrite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                writeInNFC();
            }
        });
    }

    private void initNFC(){

        nfcAdapter = NfcAdapter.getDefaultAdapter(this);
        if(nfcAdapter == null) {
            Toast.makeText( this, "This device doesn't support NFC",
                    Toast.LENGTH_SHORT ).show();
            finish();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        IntentFilter tagDetected = new
                IntentFilter(NfcAdapter.ACTION_TAG_DISCOVERED);
        IntentFilter ndefDetected = new
                IntentFilter(NfcAdapter.ACTION_NDEF_DISCOVERED);
        IntentFilter techDetected = new
                IntentFilter(NfcAdapter.ACTION_TECH_DISCOVERED);

        IntentFilter[] intentFilters = new IntentFilter[]{tagDetected,
                ndefDetected, techDetected};
        Intent intent = new Intent(this, getClass());
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0,
                intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);
        nfcAdapter.enableForegroundDispatch(this, pendingIntent,
                intentFilters, null);
    }
    @Override
    protected void onPause() {
        super.onPause();
        nfcAdapter.disableForegroundDispatch(this);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
        if(tag != null){
            ndef = Ndef.get(tag);

            //LLamada a la función de ejemplo para ejecutar el código de la aplicación

            funcionDeEjemplo();
        }
    }

    private void writeInNFC(){
        String message = messageToWrite.getText().toString();
        if(ndef != null){
            try{
                ndef.connect();
                NdefRecord mimeRecord =
                        NdefRecord.createMime("text/plain",
                                message.getBytes( Charset.forName("UTF-8")));

                ndef.writeNdefMessage(new NdefMessage(mimeRecord));
                ndef.close();
                Toast.makeText(this, "NFC Tag Written Succesfully!",

                        Toast.LENGTH_SHORT).show();
            }catch (IOException | FormatException e){
                e.printStackTrace();
                Toast.makeText(this, "Error during writing, is the NFC tag close enough to your device?", Toast.LENGTH_SHORT).show();
            }
        }else{
            Toast.makeText(this, "No NFC Tag detected!",
                    Toast.LENGTH_SHORT).show();
        }
    }

    private void funcionDeEjemplo(){
        //a realizar por la aplicación
    }
}
