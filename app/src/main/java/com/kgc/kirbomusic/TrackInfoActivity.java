package com.kgc.kirbomusic;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicBlur;
import android.widget.ImageView;
import android.widget.TextView;
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
        Bitmap bitmap = BitmapFactory.decodeFile(intent.getStringExtra("FilesDir") + "/Cover/" + Track.getString("cover") + ".jpg");
        Bitmap bitmap1 = BitmapFactory.decodeFile(intent.getStringExtra("FilesDir") + "/Cover/" + Track.getString("cover") + ".jpg");
        RenderScript rs = RenderScript.create(this);

        final Allocation input = Allocation.createFromBitmap(rs, bitmap); //use this constructor for best performance, because it uses USAGE_SHARED mode which reuses memory
        final Allocation output = Allocation.createTyped(rs, input.getType());
        final ScriptIntrinsicBlur script = ScriptIntrinsicBlur.create(rs, Element.U8_4(rs));
        script.setRadius(20f);
        script.setInput(input);
        script.forEach(output);
        output.copyTo(bitmap1);
        ((ImageView) findViewById(R.id.track_info_track_cover)).setImageBitmap(bitmap);
        ((ImageView) findViewById(R.id.track_info_track_cover_bg)).setImageBitmap(bitmap1);
        ((TextView) findViewById(R.id.track_info_track_name)).setText(Track.getString("name"));
    }
}
