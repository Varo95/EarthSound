package com.EarthSound.interfaces;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public interface IBeans {
    interface IPhotoName {
        String getPhoto();
        void setPhoto(String photoURL);
        String getName();
    }
    interface IArtist extends IPhotoName {
        long getId();
        void setId(long id);
        void setName(String name);
        String getNationality();
        void setNationality(String nationality);
        List<IDisc> getDiscs();
        void setDiscs(List<IDisc> discs);
    }
    interface IDisc extends IPhotoName {
        long getId();
        void setId(long id);
        void setName(String name);
        LocalDate getPublicationDate();
        void setPublicationDate(LocalDate publicationDate);
        long getnDiscPlay();
        IArtist getArtist();
        void setArtist(IArtist artist);
        List<ISong> getSongs();
        void setSongs(List<ISong> songs);
    }
    interface IGenre {
        long getId();
        void setId(long id);
        String getName();
        void setName(String name);
    }
    interface ISong {
        long getId();
        void setId(long id);
        String getName();
        void setName(String name);
        double getDuration();
        void setDuration(double duration);
        IDisc getDisc();
        void setDisc(IDisc disc);
        IGenre getGenre();
        void setGenre(IGenre genre);
        long getNPlays();
        void setNPlays(long nPlays);
        //URL dirección archivo de música?
        String getSongURL();
        void setSongURL(String songURL);
    }
    interface IUser extends IPhotoName {
        long getId();
        void setId(long id);
        void setName(String name);
        String getPassword();
        void setPassword(String password);
        List<IPlayList> getSubPL();
        void setSubPL(List<IPlayList> subPL);
        boolean isDisabled();
        void setDisabled(boolean disabled);
    }
    interface IPlayList {
        long getId();
        void setId(long id);
        String getName();
        void setName(String name);
        String getDescription();
        void setDescription(String description);
        IUser getCreator();
        void setCreator(IUser creator);
        long getNSubs();
        void setNSubs(long nSubs);
        List<IUser> getSubs();
        void setSubs(List<IUser> subscribers);
        List<ISong> getPlayList();
        void setPlayList(List<ISong> songs);
        List<IComment> getComments();
        void setComments(List<IComment> comments);
    }
    interface IComment {
        long getId();
        void setId(long id);
        IUser getUser();
        void setUser(IUser user);
        LocalDateTime getTime();
        void setTime(LocalDateTime time);
        String getComment();
        void setComment(String content);
        IPlayList getPlayList();
        void setPlayList(IPlayList playList);
    }
}
