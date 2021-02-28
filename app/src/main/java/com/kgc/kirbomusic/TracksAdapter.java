package com.kgc.kirbomusic;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class TracksAdapter extends BaseAdapter {
    private Context ctx;
    LayoutInflater layoutInflater;
    ArrayList<Track> objects;

    public TracksAdapter(Context ctx, ArrayList<Track> objects){
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
        if(view == null){
            view = layoutInflater.inflate(R.layout.tracks_item, parent, false);
        }
        if(objects != null) {
            Track t = objects.get(position);
            ((ImageView) view.findViewById(R.id.cover)).setImageBitmap(t.cover);
            ((TextView) view.findViewById(R.id.trackName)).setText(t.trackName);
            ((TextView) view.findViewById(R.id.releaseDate)).setText(((t.released) ? "Вышел " : "Выйдет ") + t.releaseDate);
        }
        return view;
    }
}
