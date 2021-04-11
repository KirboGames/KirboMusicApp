package com.kgc.kirbomusic.ui.music;

import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.kgc.kirbomusic.*;
import org.apache.commons.io.FileUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Scanner;

public class MusicFragment extends Fragment {
    ArrayList<Track> tracks;
    TracksAdapter tracksAdapter;
    private MusicViewModel musicViewModel;
    JSONArray Music;
    public com.android.volley.RequestQueue RequestQueue;
    String externalFilesDir;
    File music;
    File version;
    File coverDir;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        musicViewModel =
                ViewModelProviders.of(this).get(MusicViewModel.class);
        View root = inflater.inflate(R.layout.fragment_music, container, false);

        externalFilesDir = inflater.getContext().getExternalFilesDir(Environment.getDataDirectory().getAbsolutePath()).getAbsolutePath();
        music = new File(externalFilesDir + "/music.json");
        version = new File(externalFilesDir + "/version");
        coverDir = new File(externalFilesDir + "/Cover");
        RequestQueue = Volley.newRequestQueue(inflater.getContext());
        createFiles();

        getDataBaseUpdate();
        Music = readDatabase(inflater.getContext());
        downloadCovers(Music, coverDir);

        tracks = new ArrayList<Track>();
        Music = readDatabase(inflater.getContext());
        try {
            if (Music != null)
                fillData(inflater.getContext().getExternalFilesDir(Environment.getDataDirectory().getAbsolutePath()).getAbsolutePath());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        tracksAdapter = new TracksAdapter(inflater.getContext(), tracks);

        ListView lvMain = (ListView) root.findViewById(R.id.tracksList);
        lvMain.setAdapter(tracksAdapter);
        lvMain.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getActivity(), TrackInfoActivity.class);
                try {
                    intent.putExtra("Track", Music.getJSONObject(Music.length() - 1 - position).toString());
                    intent.putExtra("FilesDir", externalFilesDir);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                startActivity(intent);
            }
        });
        return root;
    }

    public static JSONArray readDatabase(Context ctx) {
        String databaseString = "";
        JSONArray Music;
        String externalFilesDir = ctx.getExternalFilesDir(Environment.getDataDirectory().getAbsolutePath()).getAbsolutePath();
        File music = new File(externalFilesDir + "/music.json");
        try {
            Scanner reader = new Scanner(music);
            while (reader.hasNextLine()) {
                databaseString += reader.nextLine();
            }
        } catch (Exception e) {}
        try {
            Music = new JSONArray(databaseString);
            return Music;
        } catch (JSONException e) {}
        return null;
    }

    public static void downloadCovers(final JSONArray Music, final File coverDir) {
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
                //Toast.makeText(MainActivity.this, error.toString(), Toast.LENGTH_LONG).show();
            }
        });
        RequestQueue.add(StringRequest);
    }
    public static void downloadTrack(Context ctx, String trackName, File trackFile) throws IOException {
        if (!trackFile.exists()) {
            DownloadManager downloadmanager = (DownloadManager) ctx.getSystemService(Context.DOWNLOAD_SERVICE);
            Uri uri = Uri.parse("https://raw.githubusercontent.com/KirboGames/KirboMusic/main/Music/" + trackName);

            DownloadManager.Request request = new DownloadManager.Request(uri);
            request.setTitle(trackName);
            request.setDescription("Downloading");
            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
            request.setVisibleInDownloadsUi(true);
            request.setDestinationInExternalPublicDir(Environment.DIRECTORY_MUSIC, "KirboMusic/" + trackName);

            downloadmanager.enqueue(request);
        /*File musicDir = new File(Environment.getExternalStorageDirectory() + "/KirboMusic");
        if(!musicDir.exists()) musicDir.mkdir();
        trackFile = new File(musicDir + "/" + trackName);*/
        }
    }

    public void fillData(String appDir) throws JSONException {
        for (int i = Music.length() - 1; i >= 0; i--) {
            JSONObject track = Music.getJSONObject(i);
            Bitmap bitmap = BitmapFactory.decodeFile(appDir + "/Cover/" + track.getString("cover") + ".jpg");
            tracks.add(new Track(bitmap, track.getString("name"), track.getString("releaseDate"), track.getBoolean("released"), track.getString("track"), track));
            System.out.println(track.getString("Youtube"));
        }
    }
}