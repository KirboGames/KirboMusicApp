package com.kgc.kirbomusic.ui.music;

import android.app.DownloadManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import com.kgc.kirbomusic.MainActivity;
import com.kgc.kirbomusic.R;
import com.kgc.kirbomusic.Track;
import com.kgc.kirbomusic.TracksAdapter;
import org.apache.commons.io.FileUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

public class MusicFragment extends Fragment {
    JSONArray Music;
    ArrayList<Track> tracks;
    TracksAdapter tracksAdapter;
    private MusicViewModel musicViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        musicViewModel =
                ViewModelProviders.of(this).get(MusicViewModel.class);
        View root = inflater.inflate(R.layout.fragment_music, container, false);

        tracks = new ArrayList<Track>();
        Music = MainActivity.readDatabase(inflater.getContext());
        try {
            if(Music != null)
                fillData(inflater.getContext().getExternalFilesDir(Environment.getDataDirectory().getAbsolutePath()).getAbsolutePath());
        } catch (JSONException e) {e.printStackTrace();}

        tracksAdapter = new TracksAdapter(inflater.getContext(), tracks);

        ListView lvMain = (ListView) root.findViewById(R.id.tracksList);
        lvMain.setAdapter(tracksAdapter);

        return root;
    }
    public static void downloadTrack(Context ctx, String trackName, File trackFile) throws IOException {
        if(!trackFile.exists()) {
            DownloadManager downloadmanager = (DownloadManager) ctx.getSystemService(Context.DOWNLOAD_SERVICE);
            Uri uri = Uri.parse("https://raw.githubusercontent.com/KirboGames/KirboMusic/main/Music/" + trackName);

            DownloadManager.Request request = new DownloadManager.Request(uri);
            request.setTitle(trackName);
            request.setDescription("Downloading");
            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
            request.setVisibleInDownloadsUi(true);
            request.setDestinationInExternalPublicDir(Environment.DIRECTORY_MUSIC, "Kirbo-" + trackName);

            downloadmanager.enqueue(request);
        /*File musicDir = new File(Environment.getExternalStorageDirectory() + "/KirboMusic");
        if(!musicDir.exists()) musicDir.mkdir();
        trackFile = new File(musicDir + "/" + trackName);*/
        }
    }
    public void fillData(String appDir) throws JSONException {
        for(int i = Music.length() - 1; i >= 0; i--){
            JSONObject track = Music.getJSONObject(i);
            Bitmap bitmap = BitmapFactory.decodeFile(appDir + "/Cover/" + track.getString("cover") + ".jpg");
            tracks.add(new Track(bitmap, track.getString("name"), track.getString("releaseDate"), track.getBoolean("released"), track.getString("track")));
        }
    }
}