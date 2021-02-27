package com.kgc.kirbomusic;

import android.os.Bundle;
import android.os.Environment;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import org.apache.commons.io.FileUtils;
import org.json.JSONArray;
import org.json.JSONException;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Scanner;

public class MainActivity extends AppCompatActivity {
    JSONArray Music;
    RequestQueue RequestQueue;
    String externalFilesDir;
    File music;
    File version;
    File coverDir;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        BottomNavigationView navView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_music, R.id.navigation_news)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(navView, navController);

        externalFilesDir = getExternalFilesDir(Environment.getDataDirectory().getAbsolutePath()).getAbsolutePath();
        music = new File(externalFilesDir + "/music.json");
        version = new File(externalFilesDir + "/version");
        coverDir = new File(externalFilesDir + "/Cover");
        RequestQueue = Volley.newRequestQueue(this);
        createFiles();
        getDataBaseUpdate();
        readDatabase();
        downloadCovers();
    }
    public void readDatabase() {
        String databaseString = "";

        try {
            Scanner reader = new Scanner(music);
            while (reader.hasNextLine()) {
                databaseString += reader.nextLine();
            }
        } catch (Exception e) {}
        try {
            Music = new JSONArray(databaseString);
        } catch (JSONException e) {}
    }

    public void downloadCovers() {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try  {
                    for (int i = 0; i < Music.length(); i++) {
                        File cover = new File(coverDir + "/" + Music.getJSONObject(i).getString("cover") + ".jpg");
                        if (!cover.exists()) {
                            URL url = new URL("https://raw.githubusercontent.com/KirboGames/KirboMusic/main/Cover/" + Music.getJSONObject(i).getString("cover") + ".jpg");
                            URLConnection connection = url.openConnection();
                            FileUtils.copyURLToFile(connection.getURL(), cover);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        thread.start();
    }

    public void createFiles() {
        try {
            if (!music.exists())
                music.createNewFile();
            if (!version.exists()) {
                version.createNewFile();
                FileWriter s = new FileWriter(version.toString());
                s.write("0");
                s.close();
            }
            if (!coverDir.exists()) {
                coverDir.mkdir();
            }
        } catch (IOException e) {}
    }
    public void updateDataBase() {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try  {
                    URL url = new URL("https://raw.githubusercontent.com/KirboGames/KirboMusic/main/music.json");
                    URLConnection connection = url.openConnection();
                    FileUtils.copyURLToFile(connection.getURL(), music);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        thread.start();
    }
    public void getDataBaseUpdate() {
        String Version = "";
        try {
            Scanner myReader = new Scanner(version);
            while (myReader.hasNextLine()) {
                Version += myReader.nextLine();
            }
            myReader.close();
        } catch (Exception e) {

        }
        final int version_ = Integer.parseInt(Version);

        StringRequest StringRequest = new StringRequest(Request.Method.GET, "https://raw.githubusercontent.com/KirboGames/KirboMusic/main/version", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if (version_ < Integer.parseInt(response)) {
                    updateDataBase();
                    try {
                        FileWriter s = new FileWriter(version.toString());
                        s.write(response);
                        s.close();
                    } catch (Exception e) {

                    }
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(MainActivity.this, error.toString(), Toast.LENGTH_LONG).show();
            }
        });
        RequestQueue.add(StringRequest);
    }

}