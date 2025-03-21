package com.EarthSound.models.beans;

import com.EarthSound.interfaces.IBeans.IComment;
import com.EarthSound.interfaces.IBeans.IPlayList;
import com.EarthSound.interfaces.IBeans.ISong;
import com.EarthSound.interfaces.IBeans.IUser;

import java.util.List;

public class PlayList implements IPlayList {

    private long id;
    private String name;
    private String description;
    private IUser creator;
    private long nSubs;
    private List<ISong> songs;
    private List<IComment> comments;
    private List<IUser> subscribers;

    public PlayList(long id, String name, String description, IUser creator, long nSubs, List<ISong> songs, List<IUser> subscribers) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.creator = creator;
        this.nSubs = nSubs;
        this.songs = songs;
        this.subscribers = subscribers;
    }

    public PlayList() {
        this(-1, "", "", null, -1, null, null);
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
    public String getDescription() {
        return this.description;
    }

    @Override
    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public IUser getCreator() {
        return this.creator;
    }

    @Override
    public void setCreator(IUser creator) {
        this.creator = creator;
    }

    @Override
    public long getNSubs() {
        return this.nSubs;
    }

    @Override
    public void setNSubs(long nSubs) {
        this.nSubs = nSubs;
    }

    @Override
    public List<ISong> getPlayList() {
        return this.songs;
    }

    @Override
    public void setPlayList(List<ISong> songs) {
        this.songs = songs;
    }

    @Override
    public List<IComment> getComments() {
        return this.comments;
    }

    @Override
    public void setComments(List<IComment> comments) {
        this.comments = comments;
    }

    @Override
    public List<IUser> getSubs() {
        return this.subscribers;
    }

    @Override
    public void setSubs(List<IUser> subscribers) {
        this.subscribers = subscribers;
    }

    @Override
    public String toString() {
        return this.name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PlayList playList = (PlayList) o;
        return id == playList.id;
    }

}
