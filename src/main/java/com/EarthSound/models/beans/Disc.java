package com.EarthSound.models.beans;

import com.EarthSound.interfaces.IBeans.ISong;
import com.EarthSound.interfaces.IBeans.IDisc;
import com.EarthSound.interfaces.IBeans.IArtist;

import java.time.LocalDate;
import java.util.List;

public class Disc implements IDisc {

    private long id;
    private String name;
    private LocalDate publicationDate;
    private String photoURL;
    private IArtist artist;
    private List<ISong> songs;

    public Disc(long id, String name, LocalDate publicationDate, String photoURL, IArtist artist, List<ISong> songs) {
        this.id = id;
        this.name = name;
        this.publicationDate = publicationDate;
        this.photoURL = photoURL;
        this.artist = artist;
        this.songs = songs;
    }

    public Disc() {
        this(-1, "", null, "", null, null);
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
    public LocalDate getPublicationDate() {
        return this.publicationDate;
    }

    @Override
    public void setPublicationDate(LocalDate publicationDate) {
        this.publicationDate = publicationDate;
    }

    @Override
    public String getPhoto() {
        return this.photoURL;
    }

    @Override
    public void setPhoto(String photoURL) {
        this.photoURL = photoURL;
    }

    @Override
    public long getnDiscPlay() {
        long result = 0;
        if (this.songs!=null && !this.songs.isEmpty()) {
            for (final ISong song : this.songs) {
                result += song.getNPlays();
            }
        }
        return result;
    }

    @Override
    public IArtist getArtist() {
        return this.artist;
    }

    @Override
    public void setArtist(IArtist artist) {
        this.artist = artist;
    }

    @Override
    public List<ISong> getSongs() {
        return this.songs;
    }

    @Override
    public void setSongs(List<ISong> songs) {
        this.songs = songs;
    }

    @Override
    public String toString() {
        return this.name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Disc disc = (Disc) o;
        return id == disc.id;
    }
}
