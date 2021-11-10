package com.EarthSound.models.DAO.SQL;

import com.EarthSound.interfaces.SQL.ISongDAO;
import com.EarthSound.models.beans.Song;
import com.EarthSound.utils.connections.SQL;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class SongDAO extends Song implements ISongDAO {

    private static final Logger logger = LoggerFactory.getLogger(SongDAO.class);

    private enum querys {
        SELECT_ALL("SELECT id,name,duration,songURL,id_genre,n_plays,id_disc FROM song"),
        SELECT_BY_ID("SELECT id,name,duration,songURL,id_genre,n_plays,id_disc FROM song WHERE id=?"),
        GETDISC("SELECT id,name,photourl,fecha from disco WHERE id=?"),
        INSERT("INSERT INTO song(name,duration,songURL,id_genre,n_plays,id_disc) VALUES(?,?,?,?,?,?)"),
        UPDATE_BY_ID("UPDATE song SET name=?,duration=?,songURL=?,id_genre=?,n_plays=?,id_disc=? WHERE id=?"),
        DELETE_BY_ID("DELETE FROM song WHERE id=?"),
        ADDNPLAY("UPDATE song SET n_plays=? WHERE id=?");
        private String q;
        querys(String q) {
            this.q = q;
        }
        public String getQ() {
            return this.q;
        }
    }

    public SongDAO(){
        super();
    }

    public SongDAO(long id){
        super();
        List<Object> params = new ArrayList<>();
        params.add(id);
        ResultSet rs = SQL.execQuery(querys.SELECT_BY_ID.getQ(), params);
        if (rs != null) {
            try {
                while (rs.next()) {
                    Song s = instanceBuilder(rs);
                    setId(s.getId());
                    setName(s.getName());
                    setDuration(s.getDuration());
                    setSongURL(s.getSongURL());
                    setGenre(s.getGenre());
                    super.setNPlays(s.getNPlays());
                    setDisc(s.getDisc());
                }
            }catch (SQLException e){
                logger.error("Hubo un error en la conexión a la base de datos al instanciar el SongDAO con el id: "+id+
                        "\nCon el mensaje:\n"+e.getMessage());
            }
        }
    }

    @Override
    public void persist() {
        querys q;
        List<Object> params = new ArrayList<>();
        params.add(getName());
        params.add(getDuration());
        params.add(getSongURL());
        params.add(getGenre().getId());
        params.add(getNPlays());
        params.add(getDisc().getId());
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
        if(rs==-1){
            logger.error("No se pudo eliminar la canción con el id:"+getId());
        }
    }

    @Override
    public void setNPlays(long nPlays) {
        if(getId()!=-1) {
            querys q = querys.ADDNPLAY;
            List<Object> params = new ArrayList<>();
            params.add(nPlays);
            params.add(getId());
            long rs = SQL.execUpdate(q.getQ(),params, false);
            if(rs ==-1){
                logger.error("No se pudo actualizar el número de plays de la canción: "+getId()+"."+getName());
            }
        }
    }

    //Utils
    public static Song instanceBuilder(ResultSet rs){
        Song result = new Song();
        if (rs != null) {
            try {
                result.setId(rs.getLong("id"));
                result.setName(rs.getString("name"));
                result.setDuration(rs.getDouble("duration"));
                result.setSongURL(rs.getString("songURL"));
                result.setGenre(new GenreDAO(rs.getLong("id_genre")));
                result.setNPlays(rs.getLong("n_plays"));
                result.setDisc(new DiscDAO(rs.getLong("id_disc")));
            } catch (SQLException e) {
                logger.error("Hubo un error al instanciar una Canción con el instancebuilder en:\n"+e.getMessage());
            }
        }
        return result;
    }
}
