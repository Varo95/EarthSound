package com.EarthSound.models.DAO.SQL;

import com.EarthSound.interfaces.IBeans.IDisc;
import com.EarthSound.interfaces.SQL.IArtistDAO;
import com.EarthSound.models.beans.Artist;
import com.EarthSound.utils.connections.SQL;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ArtistDAO extends Artist implements IArtistDAO {

    private static final Logger logger = LoggerFactory.getLogger(ArtistDAO.class);

    private enum querys {
        SELECT_ALL("SELECT id,name,nationality,photoURL FROM artist"),
        SELECT_BY_ID("SELECT id,name,nationality,photoURL FROM artist WHERE id=?"),
        INSERT("INSERT INTO artist(name,nationality,photoURL) VALUES(?,?,?)"),
        UPDATE_BY_ID("UPDATE artist SET name=?,nationality=?,photoURL=? WHERE id=?"),
        DELETE_BY_ID("DELETE FROM artist WHERE id=?"),

        GETDISCS("SELECT id FROM disc WHERE id_artist=? ");
        private String q;

        querys(String q) {
            this.q = q;
        }

        public String getQ() {
            return this.q;
        }
    }

    public ArtistDAO() {
        super();
    }

    public ArtistDAO(long id) {
        super();
        List<Object> params = new ArrayList<>();
        params.add(id);
        ResultSet rs = SQL.execQuery(querys.SELECT_BY_ID.getQ(), params);
        if (rs != null) {
            try {
                while (rs.next()) {
                    Artist a = instanceBuilder(rs);
                    setId(a.getId());
                    setName(a.getName());
                    setNationality(a.getNationality());
                    setPhoto(a.getPhotoURL());
                    setDiscs(null);
                }
            } catch (SQLException e) {
                logger.error("Hubo un error en la conexión a la base de datos al instanciar el ArtistaDAO con el id: "+id+
                        "\nCon el mensaje:\n"+e.getMessage());
            }
        }
    }

    @Override
    public void persist() {
        querys q;
        List<Object> params = new ArrayList<>();
        params.add(getName());
        params.add(getNationality());
        params.add(getPhotoURL());
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
            logger.error("Hubo un error al intentar eliminar el artista con el id: "+getId());
        }
    }

    @Override
    public List<IDisc> getDiscs() {
        List<IDisc> result = super.getDiscs();
        List<Object> params = new ArrayList<>();
        params.add(getId());
        try {
            ResultSet rs = SQL.execQuery(querys.GETDISCS.getQ(), params);
            if (rs != null) {
                result = new ArrayList<>();
                while (rs.next()) {
                    DiscDAO w = new DiscDAO(rs.getLong("id"));
                    w.setArtist(this);
                    result.add(w);
                }
                super.setDiscs(result);
            }
        } catch (SQLException e) {
            logger.error("Hubo un error en la conexión a la base de datos al replegar los discos del artista:"+getName()+
                    "\nCon el mensaje:\n"+e.getMessage());
        }
        return result;
    }

    @Override
    public void setDiscs(List<IDisc> discs) {
        super.setDiscs(discs);
    }

    //Utilidades

    public static Artist instanceBuilder(ResultSet rs) {
        Artist result = new Artist();
        if (rs != null) {
            try {
                result.setId(rs.getLong("id"));
                result.setName(rs.getString("name"));
                result.setNationality(rs.getString("nationality"));
                result.setPhoto(rs.getString("photoURL"));
                result.setDiscs(null);
            } catch (SQLException e) {
                logger.error("Hubo un error al instanciar un Artista con el instancebuilder en:\n"+e.getMessage());
            }
        }
        return result;
    }

    private static List<Artist> list;

    public static List<Artist> listAll() {
        try {
            ResultSet rs = SQL.execQuery(querys.SELECT_ALL.getQ(), null);
            list = new ArrayList<>();
            while (rs.next()) {
                Artist a = instanceBuilder(rs);
                list.add(a);
            }
        } catch (SQLException e) {
            logger.error("Hubo un error en la conexión a la base de datos al cargar la lista de Artistas:"+
                    "\nCon el mensaje:\n"+e.getMessage());
        }
        return list;
    }

}
