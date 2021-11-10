package com.EarthSound.models.beans;

import com.EarthSound.interfaces.IBeans.ISong;
import com.EarthSound.interfaces.IBeans.IGenre;
import com.EarthSound.interfaces.IBeans.IDisc;

public class Song implements ISong {

    private long id;
    private String name;
    private double duration;
    private IGenre genre;
    private IDisc disc;
    private long nPlays;
    private String songURL;

    public Song(long id, String name, double duration, IGenre genre, long nPlays, String songURL) {
        this.id = id;
        this.name = name;
        this.duration = duration;
        this.genre = genre;
        this.nPlays = nPlays;
        this.songURL = songURL;
    }

    public Song() {
        this(-1, "", -1, null, 0, "");
    }

    @Override
    public long getId() {
        return this.id;
    }

    @Override
    public void setId(long id) {
        this.id = id;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public double getDuration() {
        return this.duration;
    }

    @Override
    public void setDuration(double duration) {
        this.duration = duration;
    }

    @Override
    public IGenre getGenre() {
        return this.genre;
    }

    @Override
    public void setGenre(IGenre genre) {
        this.genre = genre;
    }

    @Override
    public IDisc getDisc() {
        return this.disc;
    }

    @Override
    public void setDisc(IDisc disc) {
        this.disc = disc;
    }

    @Override
    public long getNPlays() {
        return this.nPlays;
    }

    @Override
    public void setNPlays(long nPlays) {
        this.nPlays = nPlays;
    }

    @Override
    public String getSongURL() {
        return this.songURL;
    }

    @Override
    public void setSongURL(String songURL) {
        this.songURL = songURL;
    }

    public String toCombox() {
        return this.name + "." + this.id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Song song = (Song) o;
        return id == song.id;
    }

}
