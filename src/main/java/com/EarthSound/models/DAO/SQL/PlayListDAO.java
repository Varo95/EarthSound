package com.EarthSound.models.DAO.SQL;

import com.EarthSound.interfaces.IBeans.IUser;
import com.EarthSound.interfaces.IBeans.IComment;
import com.EarthSound.interfaces.IBeans.ISong;
import com.EarthSound.interfaces.SQL.IPlayListDAO;
import com.EarthSound.models.beans.PlayList;
import com.EarthSound.utils.connections.SQL;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class PlayListDAO extends PlayList implements IPlayListDAO {

    private static final Logger logger = LoggerFactory.getLogger(PlayListDAO.class);

    private enum querys {
        SELECT_ALL("SELECT playlist.id as id,name,description,id_ucreator, COUNT(id_user) as nsubs FROM playlist, suscribe GROUP BY playlist.id"),
        SELECT_BY_ID("SELECT playlist.id,name,description,id_ucreator,COUNT(id_user) as nsubs FROM playlist, suscribe WHERE playlist.id=? GROUP BY playlist.id"),
        INSERT("INSERT INTO playlist(name,description,id_ucreator) VALUES(?,?,?)"),
        UPDATE_BY_ID("UPDATE playlist SET name=?,description=?,id_ucreator=? WHERE id=?"),
        DELETE_BY_ID("DELETE FROM playlist WHERE id=?"),

        GETNSUBS("SELECT COUNT(id_user) as nsub from suscribe WHERE id_playlist=?"),
        GETSONGS("SELECT id_song from pl_songs WHERE id_playlist=?"),
        GETCOMMENTS("SELECT id from comment WHERE id_playlist=?"),
        GETSUBS("SELECT id_user from suscribe WHERE id_playlist=?"),

        ADDSONGPL("INSERT INTO pl_songs(id_playlist,id_song) VALUES(?,?)"),
        DELETESONGPL("DELETE FROM pl_songs WHERE id_playlist=? AND id_song=?");
        private String q;

        querys(String q) {
            this.q = q;
        }

        public String getQ() {
            return this.q;
        }
    }

    public PlayListDAO() {
        super();
    }

    public PlayListDAO(long id) {
        super();
        List<Object> params = new ArrayList<>();
        params.add(id);
        ResultSet rs = SQL.execQuery(querys.SELECT_BY_ID.getQ(), params);
        if (rs != null) {
            try {
                while (rs.next()) {
                    PlayList p = instanceBuilder(rs);
                    setId(p.getId());
                    setName(p.getName());
                    setDescription(p.getDescription());
                    setCreator(p.getCreator());
                    setSubs(null);
                    setComments(null);
                }
            } catch (SQLException e) {
                logger.error("Hubo un problema con la conexión en la base de datos a la hora de instanciar la playlist" +
                        "con el id: "+id+"\n con el mensaje:\n "+e.getMessage());
            }
        }
    }

    @Override
    public void persist() {
        querys q;
        List<Object> params = new ArrayList<>();
        params.add(getName());
        params.add(getDescription());
        params.add(getCreator().getId());
        if (getId() == -1) {
            q = querys.INSERT;
        } else {
            q = querys.UPDATE_BY_ID;
            params.add(getId());
        }
        long rs = SQL.execUpdate(q.getQ(), params, (q == querys.INSERT));
        if (q == querys.INSERT) {
            this.setId(rs);
        }
    }

    @Override
    public void remove() {
        querys q = querys.DELETE_BY_ID;
        List<Object> params = new ArrayList<>();
        params.add(getId());
        long rs = SQL.execUpdate(q.getQ(), params, false);
        if (rs == -1) {
            logger.error("No se pudo eliminar la playlist con el id:"+getId());
        }
    }

    @Override
    public long getNSubs() {
        long result = super.getNSubs();
        List<Object> params = new ArrayList<>();
        params.add(getId());
        ResultSet rs = SQL.execQuery(querys.GETNSUBS.getQ(), params);
        if (rs != null) {
            try {
                while (rs.next()) {
                    result = rs.getLong("nsub");
                }
            } catch (SQLException e) {
                logger.error("Hubo un error al intentar replegar el numero de suscriptores de la playlist "+getName()+":\n"+e.getMessage());
            }
        }
        return result;
    }

    @Override
    public List<IUser> getSubs() {
        List<IUser> result = super.getSubs();
        List<Object> params = new ArrayList<>();
        params.add(getId());
        ResultSet rs = SQL.execQuery(querys.GETSUBS.getQ(), params);
        if(rs!=null){
            result = new ArrayList<>();
            try{
                while(rs.next()){
                    result.add(new UserDAO(rs.getLong("id_user")));
                }
            }catch (SQLException e){
                logger.error("Hubo un error al intentar replegar la lista de suscriptores de la playlist "+getName()+":\n"+e.getMessage());
            }
        }
        return result;
    }

    @Override
    public List<ISong> getPlayList() {
        List<ISong> result = new ArrayList<>();
        List<Object> params = new ArrayList<>();
        params.add(getId());
        try {
            ResultSet rs = SQL.execQuery(querys.GETSONGS.getQ(), params);
            if (rs != null) {
                result = new ArrayList<>();
                while (rs.next()) {
                    SongDAO s = new SongDAO(rs.getLong("id_song"));
                    result.add(s);
                }
                super.setPlayList(result);
            }
        } catch (SQLException e) {
            logger.error("Hubo un error en la conexión a la base de datos al replegar las canciones de la playlist:"+getName()+
                    "\nCon el mensaje:\n"+e.getMessage());
        }
        return result;
    }

    @Override
    public List<IComment> getComments() {
        List<IComment> result = super.getComments();
        List<Object> params = new ArrayList<>();
        params.add(getId());
        try {
            ResultSet rs = SQL.execQuery(querys.GETCOMMENTS.getQ(), params);
            if (rs != null) {
                result = new ArrayList<>();
                while (rs.next()) {
                    CommentDAO c = new CommentDAO(rs.getLong("id"));
                    c.setPlayList(this);
                    result.add(c);
                }
                super.setComments(result);
            }
        } catch (SQLException e) {
            logger.error("Hubo un error en la conexión a la base de datos al replegar los comentarios de la playlist: "+getName()+
                    "\nCon el mensaje:\n"+e.getMessage());
        }
        return result;
    }

    @Override
    public boolean addSong(ISong song) {
        List<Object> params = new ArrayList<>();
        params.add(getId());
        params.add(song.getId());
        long id = SQL.execUpdate(querys.ADDSONGPL.getQ(), params, true);
        return id != -1;
    }

    @Override
    public boolean removeSong(ISong song) {
        List<Object> params = new ArrayList<>();
        params.add(getId());
        params.add(song.getId());
        long id = SQL.execUpdate(querys.DELETESONGPL.getQ(), params, false);
        return id != -1;
    }

    //Utilidades

    private static List<PlayList> list;

    public static List<PlayList> listAll(){
        try {
            ResultSet rs = SQL.execQuery(querys.SELECT_ALL.getQ(), null);
            list = new ArrayList<>();
            while (rs.next()) {
                PlayList p = instanceBuilder(rs);
                list.add(p);
            }
        } catch (SQLException e) {
            logger.error("Hubo un error en la conexión a la base de datos al cargar la lista de PlayList:"+
                    "\nCon el mensaje:\n"+e.getMessage());
        }
        return list;
    }

    public static PlayList instanceBuilder(ResultSet rs) {
        PlayList result = new PlayList();
        if (rs != null) {
            try {
                result.setId(rs.getLong("id"));
                result.setName(rs.getString("name"));
                result.setDescription(rs.getString("description"));
                result.setCreator(new UserDAO(rs.getLong("id_ucreator")));
                result.setNSubs(rs.getLong("nsubs"));
            } catch (SQLException e) {
                logger.error("Hubo un error al instanciar un PlayList con el instancebuilder en:\n"+e.getMessage());
            }
        }
        return result;
    }
}
