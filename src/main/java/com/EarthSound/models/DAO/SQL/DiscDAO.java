package com.EarthSound.models.DAO.SQL;

import com.EarthSound.interfaces.IBeans.ISong;
import com.EarthSound.interfaces.SQL.IDiscDAO;
import com.EarthSound.models.beans.Disc;
import com.EarthSound.utils.connections.SQL;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class DiscDAO extends Disc implements IDiscDAO {

    private static final Logger logger = LoggerFactory.getLogger(DiscDAO.class);

    private enum querys {
        SELECT_ALL("SELECT id,name,photoURL,pub_date FROM disc"),
        SELECT_BY_ID("SELECT id,name,photoURL,pub_date,id_artist FROM disc WHERE id=?"),
        INSERT("INSERT INTO disc(name,photoURL,pub_date,id_artist) VALUES(?,?,?,?)"),
        UPDATE_BY_ID("UPDATE disc SET name=?,photoURL=?,pub_date=?, id_artist=? WHERE id=?"),
        DELETE_BY_ID("DELETE FROM disc WHERE id=?"),

        GETSONGS("SELECT id FROM song WHERE id_disc=? ");
        private String q;

        querys(String q) {
            this.q = q;
        }

        public String getQ() {
            return this.q;
        }
    }

    public DiscDAO(){
        super();
    }

    public DiscDAO(long id) {
        super();
        List<Object> params = new ArrayList<>();
        params.add(id);
        ResultSet rs = SQL.execQuery(querys.SELECT_BY_ID.getQ(), params);
        if (rs != null) {
            try {
                while (rs.next()) {
                    Disc d = instanceBuilder(rs);
                    setId(d.getId());
                    setName(d.getName());
                    setPhoto(d.getPhoto());
                    setPublicationDate(d.getPublicationDate());
                    setArtist(d.getArtist());
                    setSongs(null);
                }
                rs.close();
            } catch (SQLException e) {
                logger.error("Hubo un error en la conexión a la base de datos al instanciar el DiscDAO con el id: "+id+
                        "\nCon el mensaje:\n"+e.getMessage());
            }
        }
    }

    @Override
    public List<ISong> getSongs() {
        List<ISong> result = super.getSongs();
        List<Object> params = new ArrayList<>();
        params.add(getId());
        try {
            ResultSet rs = SQL.execQuery(querys.GETSONGS.getQ(), params);
            if (rs != null) {
                result = new ArrayList<>();
                while (rs.next()) {
                    SongDAO s = new SongDAO(rs.getLong("id"));
                    s.setDisc(this);
                    result.add(s);
                }
                super.setSongs(result);
                rs.close();
            }
        } catch (SQLException e) {
            logger.error("Hubo un error en la conexión a la base de datos al replegar las canciones del disco:"+getName()+
                    "\nCon el mensaje:\n"+e.getMessage());
        }
        return result;
    }

    @Override
    public void setSongs(List<ISong> songs) {
        super.setSongs(songs);
    }

    @Override
    public void persist() {
        querys q;
        List<Object> params = new ArrayList<>();
        params.add(getName());
        params.add(getPhoto());
        params.add(getPublicationDate());
        params.add(getArtist().getId());
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
            logger.error("Hubo un error al intentar eliminar el disco con el id: "+getId());
        }
    }

    //------------------Utilidades---------
    //-------------------------------------
    public static Disc instanceBuilder(ResultSet rs) {
        Disc result = new Disc();
        if (rs != null) {
            try {
                result.setId(rs.getLong("id"));
                result.setName(rs.getString("name"));
                result.setPhoto(rs.getString("photoURL"));
                result.setPublicationDate(rs.getDate("pub_date").toLocalDate());
                result.setArtist(new ArtistDAO(rs.getLong("id_artist")));
                result.setSongs(null);
            } catch (SQLException e) {
                logger.error("Hubo un error al instanciar un Disco con el instancebuilder en:\n"+e.getMessage());
            }
        }
        return result;
    }
}
