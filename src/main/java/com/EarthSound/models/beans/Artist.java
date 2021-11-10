package com.EarthSound.models.beans;

import com.EarthSound.interfaces.IBeans.IArtist;
import com.EarthSound.interfaces.IBeans.IDisc;

import java.util.List;

public class Artist implements IArtist {

    private long id;
    private String name;
    private String nationality;
    private String photoURL;
    private List<IDisc> discs;

    public Artist(long id, String name, String nationality, String photoURL, List<IDisc> discs) {
        this.id = id;
        this.name = name;
        this.nationality = nationality;
        this.photoURL = photoURL;
        this.discs = discs;
    }

    public Artist() {
        this(-1, "", "", "", null);
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
    public String getNationality() {
        return this.nationality;
    }

    @Override
    public void setNationality(String nationality) {
        this.nationality = nationality;
    }

    @Override
    public String getPhotoURL() {
        return this.photoURL;
    }

    @Override
    public void setPhoto(String photoURL) {
        this.photoURL = photoURL;
    }

    @Override
    public List<IDisc> getDiscs() {
        return this.discs;
    }

    @Override
    public void setDiscs(List<IDisc> discs) {
        this.discs = discs;
    }

    public String toCombox() {
        return this.name + "."+this.id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Artist artist = (Artist) o;
        return id == artist.id;
    }

}
