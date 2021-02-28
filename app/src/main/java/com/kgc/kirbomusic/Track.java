package com.kgc.kirbomusic;

import android.graphics.Bitmap;

public class Track {
    public Bitmap cover;
    public String trackName;
    public String releaseDate;
    public boolean released;
    public String trackFileName;
    public Track(Bitmap cover, String trackName, String releaseDate, boolean released, String trackFileName){
        this.cover = cover;
        this.trackName = trackName;
        this.releaseDate = releaseDate;
        this.released = released;
        this.trackFileName = trackFileName;
    }
}
