package com.kgc.kirbomusic;

import android.content.Context;
import android.content.Intent;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.kgc.kirbomusic.ui.music.MusicFragment;
import org.json.JSONException;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class TracksAdapter extends BaseAdapter {
    private Context ctx;
    LayoutInflater layoutInflater;
    ArrayList<Track> objects;

    public TracksAdapter(Context ctx, ArrayList<Track> objects) {
        this.ctx = ctx;
        this.objects = objects;
        layoutInflater = (LayoutInflater) ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return objects.size();
    }

    @Override
    public Object getItem(int position) {
        return objects.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            view = layoutInflater.inflate(R.layout.tracks_item, parent, false);
        }
        final Context ctx = view.getContext();
        if (objects != null) {
            final Track t = objects.get(position);
            ((ImageView) view.findViewById(R.id.cover)).setImageBitmap(t.cover);
            ((TextView) view.findViewById(R.id.trackName)).setText(t.trackName);
            ((TextView) view.findViewById(R.id.releaseDate)).setText(((t.released) ? "Вышел " : "Выйдет ") + t.releaseDate);
            final File musicDir = new File(Environment.getExternalStorageDirectory() + "/" + Environment.DIRECTORY_MUSIC + "/KirboMusic");
            final File trackFile = new File(musicDir + "/" + t.trackFileName);
            ((ImageView) view.findViewById(R.id.available_telegram)).setVisibility(View.VISIBLE);
            ((ImageView) view.findViewById(R.id.available_vk)).setVisibility(View.VISIBLE);
            ((ImageView) view.findViewById(R.id.available_youtube)).setVisibility(View.VISIBLE);
            ((ImageView) view.findViewById(R.id.available_spotify)).setVisibility(View.VISIBLE);
            try {
                System.out.println(t.trackName + " " + t.track.getString("Youtube"));
                if (t.track.getString("Telegram").equals(""))
                    ((ImageView) view.findViewById(R.id.available_telegram)).setVisibility(View.GONE);
                if (t.track.getString("VK").equals(""))
                    ((ImageView) view.findViewById(R.id.available_vk)).setVisibility(View.GONE);
                if (t.track.getString("Youtube").equals(""))
                    ((ImageView) view.findViewById(R.id.available_youtube)).setVisibility(View.GONE);
                if (t.track.getString("Spotify").equals(""))
                    ((ImageView) view.findViewById(R.id.available_spotify)).setVisibility(View.GONE);

            } catch (JSONException e) {
                e.printStackTrace();
            }
            /*((ImageView) view.findViewById(R.id.trackDownload)).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    try {
                        if(t.released)
                            MusicFragment.downloadTrack(ctx, t.trackFileName, trackFile);
                        else{
                            Toast.makeText(ctx, "Трек еще не вышел!", 3000);
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });*/
        }
        return view;
    }
}
