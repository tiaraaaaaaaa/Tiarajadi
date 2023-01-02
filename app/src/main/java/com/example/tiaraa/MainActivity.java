package com.example.tiaraa;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Objects;

//implementasi dari onclicklistener


public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    //View Objects
    private Button buttonScan;
    private TextView textViewNama, textViewKelas, textViewNim;
    //qr code scanner object
    private IntentIntegrator qrScan;
    private Object context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//View objects
        buttonScan = (Button) findViewById(R.id.buttonScan);
        textViewNama = (TextView) findViewById(R.id.textViewNama);
        textViewKelas = (TextView) findViewById(R.id.textViewKelas);
        textViewNim = (TextView) findViewById(R.id.textViewNim);//intialisasi scan object
        qrScan = new IntentIntegrator(this);

//implementasi onclick listener
        buttonScan.setOnClickListener(this);
    }
    //untuk mendapatkan hasil scanning

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode,
                resultCode, data);
        if (result != null) {
//jika qrcode tidak ada sama sekali
            if (result.getContents() == null) {
                Toast.makeText(this, "Hasil SCAN tidak ada", Toast.LENGTH_LONG).show();
            } else {
//jika qr ada/ditemukan data nya
                try {
//konversi datanya ke json


                    JSONObject obj = new JSONObject(result.getContents());


//di set nilai datanya ke textviews
                    textViewNama.setText(obj.getString("nama"));
                    textViewKelas.setText(obj.getString("kelas"));
                    textViewNim.setText(obj.getString("nim"));
                } catch (JSONException e) {
                    e.printStackTrace();
//jika kontolling ada di sini
//itu berarti format encoded tidak cocok
//dalam hal ini kita dapat menampilkan data apapun yg tesedia pada qrcode
//untuk di toast

                    Toast.makeText(this, result.getContents(),
                            Toast.LENGTH_LONG).show();

                    //1.browser
                    String url = new String(result.getContents());
                    String address;
                    String http = "http://";
                    String https = "https://";
                    address = new String(result.getContents());
                    if (address.contains(http) || address.contains(https)){
                        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                        startActivity(browserIntent);
                    }
                    // 2.kirim email pada barcode yang sudah ter-scan

                    String alamatEmail = new String(result.getContents());
                    String at = "@";

                    if (alamatEmail.contains(at))

                    {
                        Intent intent = new Intent(Intent.ACTION_SEND);
                        String[] recipients = {alamatEmail.replace("http://","")};
                        intent.putExtra(Intent.EXTRA_EMAIL, recipients);
                        intent.putExtra(Intent.EXTRA_SUBJECT, "Subject Email");
                        intent.putExtra(Intent.EXTRA_TEXT, "Type Here");
                        intent.putExtra(Intent.EXTRA_CC, "");
                        intent.setType("text/html");
                        intent.setPackage("com.google.android.gm");
                        startActivity(Intent.createChooser(intent, "Send mail"));
                    }

                    //3a. Dial number
                    String number;
                    number = new String(result.getContents());

                    if(number.matches("^[0-9]*$") && number.length() > 10){
                        Intent callIntent = new Intent(Intent.ACTION_CALL);
                        Intent dialIntent = new Intent(Intent.ACTION_DIAL);
                        dialIntent.setData(Uri.parse("tel:" + number));
                        callIntent.setData(Uri.parse("tel:" + number));
                        startActivity(callIntent);
                        startActivity(dialIntent);
                    }

                    //4.buka koordinat maps

                    String uriMaps = new String(result.getContents());
                    String maps = "http://maps.google.com/maps?q=loc:" + uriMaps;
                    String testDoubleData1 = ",";
                    String testDoubleData2 = ".";

                    boolean b = uriMaps.contains(testDoubleData1) && uriMaps.contains(testDoubleData2);
                    if (b) {
                        Intent mapsIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(maps));
                        mapsIntent.setPackage("com.google.android.apps.maps");
                        startActivity(mapsIntent);
                    }

                }

            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public void onClick (View view){
//inisialisasi scanning qr code
        qrScan.initiateScan();
    }
}
