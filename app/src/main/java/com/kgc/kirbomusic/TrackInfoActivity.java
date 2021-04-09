package com.kgc.kirbomusic;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.widget.ImageView;
import androidx.appcompat.app.AppCompatActivity;
import org.json.JSONException;
import org.json.JSONObject;

public class TrackInfoActivity extends AppCompatActivity {
    JSONObject Track;
    Bitmap cover;
    Intent intent;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_track_info);
        intent = getIntent();
        try {
            Track = new JSONObject(intent.getStringExtra("Track"));
            System.out.println(Track.getString("name"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        try {
            setContent();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    private void setContent() throws JSONException{
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = 128;
        Bitmap bitmap = BitmapFactory.decodeFile(intent.getStringExtra("FilesDir") + "/Cover/" + Track.getString("cover") + ".jpg", options);
        ((ImageView) findViewById(R.id.TrackCover)).setImageBitmap(bitmap);
    }
}
