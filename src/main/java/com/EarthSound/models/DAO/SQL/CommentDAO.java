package com.EarthSound.models.DAO.SQL;


import com.EarthSound.interfaces.SQL.ICommentDAO;
import com.EarthSound.models.beans.Comment;
import com.EarthSound.utils.connections.SQL;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class CommentDAO extends Comment implements ICommentDAO {

    private static final Logger logger = LoggerFactory.getLogger(CommentDAO.class);

    private enum querys {
        SELECT_ALL("SELECT id,date,text,id_user,id_playlist FROM comment"),
        SELECT_BY_ID("SELECT id,date,text,id_user,id_playlist FROM comment WHERE id=?"),
        INSERT("INSERT INTO comment(text,id_user,id_playlist) VALUES(?,?,?)"),
        UPDATE_BY_ID("UPDATE comment SET text=?,id_user=?,id_playlist=? WHERE id=?"),
        DELETE_BY_ID("DELETE FROM comment WHERE id=?");
        private String q;
        querys(String q) {
            this.q = q;
        }
        public String getQ() {
            return this.q;
        }
    }
    public CommentDAO(){
        super();
    }

    public CommentDAO(long id){
        super();
        List<Object> params = new ArrayList<>();
        params.add(id);
        ResultSet rs = SQL.execQuery(querys.SELECT_BY_ID.getQ(), params);
        if (rs != null) {
            try {
                while (rs.next()) {
                    Comment c = instanceBuilder(rs);
                    setId(c.getId());
                    setTime(c.getTime());
                    setComment(c.getComment());
                    setUser(c.getUser());
                    setPlayList(null);
                }
                rs.close();
            }catch (SQLException e){
                logger.error("Hubo un error en la conexión a la base de datos al instanciar el CommentDAO con el id: "+id+
                        "\nCon el mensaje:\n"+e.getMessage());
            }
        }
    }

    @Override
    public void persist() {
        querys q;
        List<Object> params = new ArrayList<>();
        params.add(getComment());
        params.add(getUser().getId());
        params.add(getPlayList().getId());
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
            logger.error("Hubo un error al intentar eliminar el comentario con el id: "+getId());
        }
    }
    //Utilidades

    public static Comment instanceBuilder(ResultSet rs){
        Comment result = new Comment();
        if (rs != null) {
            try {
                result.setId(rs.getLong("id"));
                result.setTime(rs.getTimestamp("date").toLocalDateTime());
                result.setComment(rs.getString("text"));
                result.setUser(new UserDAO(rs.getLong("id_user")));
                result.setPlayList(new PlayListDAO(rs.getLong("id_playlist")));
            } catch (SQLException e) {
                logger.error("Hubo un error al instanciar un Comentario con el instancebuilder en:\n"+e.getMessage());
            }
        }
        return result;
    }
}
