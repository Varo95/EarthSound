package com.EarthSound.models.beans;

import com.EarthSound.interfaces.IBeans.IPlayList;
import com.EarthSound.interfaces.IBeans.IUser;

import java.util.List;

public class User implements IUser {

    private long id;
    private String name;
    private String passwd;
    private String photoURL;
    private List<IPlayList> subsPlaylists;
    private boolean disabled;

    public User(long id, String name, String passwd, String photoURL) {
        this.id = id;
        this.name = name;
        this.passwd = passwd;
        this.photoURL = photoURL;
    }
    public User() {
        this(-1, "", "", "");
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
    public String getPassword() {
        return this.passwd;
    }

    @Override
    public void setPassword(String password) {
        this.passwd = password;
    }

    @Override
    public String getPhotoURL() {
        return this.photoURL;
    }

    @Override
    public void setPhotoURL(String photoURL) {
        this.photoURL = photoURL;
    }

    @Override
    public List<IPlayList> getSubPL() {
        return this.subsPlaylists;
    }

    @Override
    public void setSubPL(List<IPlayList> subPL) {
        this.subsPlaylists = subPL;
    }

    @Override
    public boolean isDisabled() {
        return disabled;
    }

    @Override
    public void setDisabled(boolean disabled) {
        this.disabled = disabled;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return id == user.id;
    }

}
