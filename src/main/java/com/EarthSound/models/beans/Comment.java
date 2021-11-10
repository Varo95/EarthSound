package com.EarthSound.models.beans;

import com.EarthSound.interfaces.IBeans.IComment;
import com.EarthSound.interfaces.IBeans.IUser;
import com.EarthSound.interfaces.IBeans.IPlayList;

import java.time.LocalDateTime;

public class Comment implements IComment{

    private long id;
    private IUser user;
    private LocalDateTime time;
    private String comment;
    private IPlayList playList;

    public Comment(long id, IUser user, LocalDateTime time, String comment, IPlayList playList) {
        this.id = id;
        this.user = user;
        this.time = time;
        this.comment = comment;
        this.playList = playList;
    }

    public Comment(){
        this(-1,null,null,"",null);
    }

    @Override
    public long getId() {
        return this.id;
    }

    @Override
    public void setId(long id) {
        this.id=id;
    }

    @Override
    public IUser getUser() {
        return this.user;
    }

    @Override
    public void setUser(IUser user) {
        this.user=user;
    }

    @Override
    public LocalDateTime getTime() {
        return this.time;
    }

    @Override
    public void setTime(LocalDateTime time) {
        this.time=time;
    }

    @Override
    public String getComment() {
        return this.comment;
    }

    @Override
    public void setComment(String content) {
        this.comment=content;
    }

    @Override
    public IPlayList getPlayList() {
        return this.playList;
    }

    @Override
    public void setPlayList(IPlayList playList) {
        this.playList=playList;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Comment comment = (Comment) o;
        return id == comment.id;
    }

}
