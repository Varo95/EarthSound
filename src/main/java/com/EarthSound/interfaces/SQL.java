package com.EarthSound.interfaces;
import com.EarthSound.interfaces.IBeans.IPlayList;
import com.EarthSound.interfaces.IBeans.ISong;
import com.EarthSound.interfaces.IBeans.IComment;

public interface SQL extends IDAO {
    interface IArtistDAO extends SQL {

    }

    interface ICommentDAO extends SQL {

    }

    interface IDiscDAO extends SQL {

    }

    interface IGenreDAO extends SQL {

    }

    interface IPlayListDAO extends SQL {
        boolean addSong(ISong song);
        boolean removeSong(ISong song);
    }

    interface ISongDAO extends SQL {

    }

    interface IUserDAO extends SQL {
        boolean subscribe(IPlayList pl);
        boolean unSubscribe(IPlayList pl);
    }
}
