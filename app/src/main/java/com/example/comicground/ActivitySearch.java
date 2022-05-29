package com.example.comicground;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class ActivitySearch extends AppCompatActivity {

    TextView tv;
    EditText etsearch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        getSupportActionBar().hide();

        tv=findViewById(R.id.tv);
        etsearch=findViewById(R.id.etsearch);

        ImageButton btnsearch= findViewById(R.id.btnsearch);
        btnsearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ConnectivityManager connectivityManager= (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo networkInfo=connectivityManager.getActiveNetworkInfo();
                if (networkInfo.isConnected() && networkInfo.isAvailable()) {
                    new HttpAsyncTask().execute();
                } else {
                    Toast.makeText(getApplicationContext(), "No hay conexión a internet (o no disponible)", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private class HttpAsyncTask extends AsyncTask<Void, Void, String> {

        @Override
        protected String doInBackground(Void... voids) {

            HttpURLConnection miHttp=null;
            try {
                //La IP de mi máquina en red no "localhost" ni 127.0.0.1
                String url = "http://192.168.0.11:8080/ComicGround/comics/"+etsearch.getText().toString();
                URL obj = new URL(url);
                HttpURLConnection conn = (HttpURLConnection) obj.openConnection();
                conn.setReadTimeout(5000);
                conn.addRequestProperty("Accept-Language", "en-US,en;q=0.8");
                conn.addRequestProperty("User-Agent", "Mozilla");
                conn.addRequestProperty("Referer", "google.com");

                System.out.println("Request URL ... " + url);

                boolean redirect = false;

                // normally, 3xx is redirect
                int status = conn.getResponseCode();
                if (status != HttpURLConnection.HTTP_OK) {
                    if (status == HttpURLConnection.HTTP_MOVED_TEMP
                            || status == HttpURLConnection.HTTP_MOVED_PERM
                            || status == HttpURLConnection.HTTP_SEE_OTHER)
                        redirect = true;
                }

                System.out.println("Response Code ... " + status);

                if (redirect) {

                    // get redirect url from "location" header field
                    String newUrl = conn.getHeaderField("Location");

                    // get the cookie if need, for login
                    String cookies = conn.getHeaderField("Set-Cookie");

                    // open the new connnection again
                    conn = (HttpURLConnection) new URL(newUrl).openConnection();
                    conn.setRequestProperty("Cookie", cookies);
                    conn.addRequestProperty("Accept-Language", "en-US,en;q=0.8");
                    conn.addRequestProperty("User-Agent", "Mozilla");
                    conn.addRequestProperty("Referer", "google.com");

                    System.out.println("Redirect to URL : " + newUrl);

                }

                BufferedReader in = new BufferedReader(
                        new InputStreamReader(conn.getInputStream()));
                String inputLine;
                String textoFinal="";
                while ((inputLine = in.readLine()) != null) {
                    textoFinal+=inputLine;
                }
                in.close();
                return textoFinal;
            } catch (Exception e) {
                e.printStackTrace();
                return "Error";
            }
        }

        @Override
        protected void onPostExecute(String result) {
            tv.setText(result);
        }

    }
}