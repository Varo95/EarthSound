package com.EarthSound.models.DAO.SQL;

import com.EarthSound.interfaces.IBeans.IUser;
import com.EarthSound.interfaces.IBeans.IPlayList;
import com.EarthSound.interfaces.SQL.IUserDAO;
import com.EarthSound.models.beans.User;
import com.EarthSound.utils.Tools;
import com.EarthSound.utils.connections.SQL;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class UserDAO extends User implements IUserDAO {

    private static final Logger logger = LoggerFactory.getLogger(UserDAO.class);

    private enum querys {
        SELECT_ALL("SELECT id,name,passwd,photoURL,disabled FROM _user"),
        SELECT_BY_ID("SELECT id,name,passwd,photoURL,disabled FROM _user WHERE id=?"),
        SELECT_BY_NAME("SELECT id,passwd,photoURL,disabled FROM _user WHERE name=?"),
        INSERT("INSERT INTO _user(name,passwd,photoURL,disabled) VALUES(?,?,?,?)"),
        UPDATE_BY_ID("UPDATE _user SET name=?,passwd=?,photoURL=?,disabled=? WHERE id=?"),
        DELETE_BY_ID("DELETE FROM _user WHERE id=?"),
        DISABLE_USER_BYID("UPDATE _user SET disabled=? WHERE id=?"),

        GETPASSWORD_BY_ID("SELECT passwd FROM _user WHERE id=?"),
        GETPASSWORD_BY_NAME("SELECT id,passwd FROM _user WHERE name LIKE ?"),
        SUBSCRIBE("INSERT INTO subscribe(id_user, id_playlist) VALUES(?,?)"),
        UNSUBSCRIBE("DELETE FROM subscribe WHERE id_playlist=? AND id_user=?"),

        GETMYPL("SELECT id,name,description FROM playlist WHERE id_ucreator=? "),
        GETSUBSPL("SELECT playlist.id as id,name,description,id_ucreator FROM playlist, subscribe WHERE subscribe.id_playlist=playlist.id AND subscribe.id_user=?");
        private String q;

        querys(String q) {
            this.q = q;
        }

        public String getQ() {
            return this.q;
        }
    }

    public UserDAO(){
        super();
    }

    public UserDAO(long id) {
        super();
        List<Object> params = new ArrayList<>();
        params.add(id);
        ResultSet rs = SQL.execQuery(querys.SELECT_BY_ID.getQ(), params);
        if (rs != null) {
            try {
                while (rs.next()) {
                    User u = instanceBuilder(rs);
                    setId(u.getId());
                    setName(u.getName());
                    setPassword(u.getPassword());
                    setPhotoURL(u.getPhotoURL());
                    setDisabled(u.isDisabled());
                    setSubPL(null);
                }
                rs.close();
            } catch (SQLException e) {
                logger.error("Hubo un error en la conexión a la base de datos al instanciar el UserDAO con el id: "+id+
                        "\nCon el mensaje:\n"+e.getMessage());
            }
        }
    }

    @Override
    public void persist() {
        querys q;
        List<Object> params = new ArrayList<>();
        params.add(getName());
        params.add(Tools.encryptMD5(getPassword()));
        params.add(getPhotoURL());
        params.add(isDisabled());
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
            logger.error("Hubo un error al intentar eliminar el usuario con nombre: "+getId()+"."+getName());
        }
    }

    @Override
    public List<IPlayList> getSubPL() {
        List<IPlayList> result = super.getSubPL();
        List<Object> params = new ArrayList<>();
        params.add(getId());
        try {
            ResultSet rs = SQL.execQuery(querys.GETMYPL.getQ(), params);
            if (rs != null) {
                result = new ArrayList<>();
                while (rs.next()) {
                    PlayListDAO pl = new PlayListDAO(rs.getLong("id"));
                    pl.setCreator(this);
                    result.add(pl);
                }
                rs.close();
            }
            ResultSet rs1 = SQL.execQuery(querys.GETSUBSPL.getQ(), params);
            if (rs1 != null) {
                if(result==null) result=new ArrayList<>();
                while (rs1.next()) {
                    PlayListDAO pl = new PlayListDAO(rs1.getLong("id"));
                    pl.setCreator(new UserDAO(rs1.getLong("id_ucreator")));
                    result.add(pl);
                }
                rs1.close();
            }
            super.setSubPL(result);
        } catch (SQLException e) {
            logger.error("Hubo un error en la conexión a la base de datos al replegar las playlists del usuario:"+getName()+
                    "\nCon el mensaje:\n"+e.getMessage());
        }
        return result;
    }

    @Override
    public void setDisabled(boolean disabled) {
        List<Object> params = new ArrayList<>();
        params.add(disabled);
        params.add(getId());
        long id = SQL.execUpdate(querys.DISABLE_USER_BYID.getQ(), params, false);
        if (id == -1) {
            logger.error("Hubo un error al intentar desactivar el usuario");
        }
    }

    @Override
    public boolean subscribe(IPlayList pl){
        List<Object> params = new ArrayList<>();
        params.add(getId());
        params.add(pl.getId());
        long id = SQL.execUpdate(querys.SUBSCRIBE.getQ(),params,true);
        return id != -1;
    }

    @Override
    public boolean unSubscribe(IPlayList pl){
        List<Object> params = new ArrayList<>();
        params.add(pl.getId());
        params.add(getId());
        long id = SQL.execUpdate(querys.UNSUBSCRIBE.getQ(), params, false);
        return id != -1;
    }

    //Utilidades

    public static User instanceBuilder(ResultSet rs) {
        User result = new User();
        if (rs != null) {
            try {
                result.setId(rs.getLong("id"));
                result.setName(rs.getString("name"));
                result.setPassword(rs.getString("passwd"));
                result.setPhotoURL(rs.getString("photoURL"));
                result.setDisabled(rs.getBoolean("disabled"));
                result.setSubPL(null);
            } catch (SQLException e) {
                logger.error("Hubo un error al instanciar un Usuario con el instancebuilder en:\n"+e.getMessage());
            }
        }
        return result;
    }

    /**
     * This method is used to verify the integrity of user
     * @param userToCheck the user that needs to verify is password
     * @return true if registered and hashpassword matches, false if not registered or not matches
     */
    public static boolean checkUser(IUser userToCheck){
        String encryptedUPwd = Tools.encryptMD5(userToCheck.getPassword());
        List<Object> params = new ArrayList<>();
        String encryptedDBPwd="";
        if(userToCheck.getId()!=-1){
            params.add(userToCheck.getId());
            try {
                ResultSet rs = SQL.execQuery(querys.GETPASSWORD_BY_ID.getQ(), params);
                if (rs != null) {
                    while (rs.next()) {
                        encryptedDBPwd = rs.getString("passwd");
                    }
                    rs.close();
                }
            }catch (SQLException e){
                logger.error("Hubo un error al intentar recuperar la contraseña del usuario por el id. Con el mensaje:\n"+e.getMessage());
            }
        }else{
            params.add(userToCheck.getName());
            try {
                ResultSet rs = SQL.execQuery(querys.GETPASSWORD_BY_NAME.getQ(), params);
                if (rs != null) {
                    while (rs.next()) {
                        encryptedDBPwd = rs.getString("passwd");
                        userToCheck.setId(rs.getLong("id"));
                    }
                    rs.close();
                }
            }catch (SQLException e){
                logger.error("Hubo un error al intentar recuperar la contraseña del usuario con el nombre. Con el mensaje:\n"+e.getMessage());
            }
        }
        return encryptedUPwd.equals(encryptedDBPwd);
    }
}
