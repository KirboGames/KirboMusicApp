package com.kgc.kirbomusic.ui.music;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import com.android.volley.RequestQueue;
import com.kgc.kirbomusic.MainActivity;
import com.kgc.kirbomusic.R;
import com.kgc.kirbomusic.Track;
import com.kgc.kirbomusic.TracksAdapter;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
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
    public void fillData(String appDir) throws JSONException {
        for(int i = 0; i < Music.length(); i++){
            JSONObject track = Music.getJSONObject(i);
            Bitmap bitmap = BitmapFactory.decodeFile(appDir + "/Cover/" + track.getString("cover") + ".jpg");
            tracks.add(new Track(bitmap, track.getString("track"), track.getString("releaseDate"), track.getBoolean("released")));
        }
    }
}