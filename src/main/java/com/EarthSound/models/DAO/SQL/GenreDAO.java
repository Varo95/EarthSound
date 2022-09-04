package com.EarthSound.models.DAO.SQL;

import com.EarthSound.interfaces.SQL.IGenreDAO;
import com.EarthSound.models.beans.Genre;
import com.EarthSound.utils.connections.SQL;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class GenreDAO extends Genre implements IGenreDAO {

    private static final Logger logger = LoggerFactory.getLogger(GenreDAO.class);

    private enum querys {
        SELECT_ALL("SELECT id,name FROM genre"),
        SELECT_BY_ID("SELECT id,name FROM genre WHERE id=?"),
        INSERT("INSERT INTO genre(name) VALUES(?)"),
        UPDATE_BY_ID("UPDATE genre SET name=? WHERE id=?"),
        DELETE_BY_ID("DELETE FROM genre WHERE id=?");
        private String q;

        querys(String q) {
            this.q = q;
        }

        public String getQ() {
            return this.q;
        }
    }

    public GenreDAO() {
        super();
    }

    public GenreDAO(long id) {
        super();
        List<Object> params = new ArrayList<>();
        params.add(id);
        ResultSet rs = SQL.execQuery(querys.SELECT_BY_ID.getQ(), params);
        if (rs != null) {
            try {
                while (rs.next()) {
                    Genre g = instanceBuilder(rs);
                    setId(g.getId());
                    setName(g.getName());
                }
                rs.close();
            } catch (SQLException e) {
                logger.error("Hubo un error en la conexión a la base de datos al instanciar el GenreDAO con el id: "+id+
                        "\nCon el mensaje:\n"+e.getMessage());
            }
        }
    }

    @Override
    public void persist() {
        querys q;
        List<Object> params = new ArrayList<>();
        params.add(getName());
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
            logger.error("Hubo un error al intentar eliminar el genero con el id: "+getId());
        }
    }

    //------------------------------------------------------Utilidades------------------------------------------------------
//----------------------------------------------------------------------------------------------------------------------
    public static Genre instanceBuilder(ResultSet rs) {
        Genre result = new Genre();
        if (rs != null) {
            try {
                result.setId(rs.getLong("id"));
                result.setName(rs.getString("name"));
            } catch (SQLException e) {
                logger.error("Hubo un error al instanciar un Genero con el instancebuilder en:\n"+e.getMessage());
            }
        }
        return result;
    }

    private static List<Genre> list;

    public static List<Genre> listAll() {
        try {
            ResultSet rs = SQL.execQuery(querys.SELECT_ALL.getQ(), null);
            list = new ArrayList<>();
            while (rs.next()) {
                Genre g = instanceBuilder(rs);
                list.add(g);
            }
            rs.close();
        } catch (SQLException e) {
            logger.error("Hubo un error en la conexión a la base de datos al cargar la lista de Generos:"+
                    "\nCon el mensaje:\n"+e.getMessage());
        }
        return list;
    }

}