package com.EarthSound.utils.connections;

import com.EarthSound.models.beans.DataConnection;
import com.EarthSound.utils.Dialog;
import com.EarthSound.utils.Tools;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.List;

/**
 * No te he traducido los comentarios de esta clase por pereza, son métodos reutilizados
 * de mi propio código del año pasado, aunque ligeramente modificados
 */
public class SQL {
    private static Connection con;
    private static final DataConnection dc = Tools.loadXML();

    private static final Logger logger = LoggerFactory.getLogger(SQL.class);

    /**
     * This method is used to establish DB Connection on com.EarthSound.utils.connections.+SQL
     * Must be called once, at the start of the app
     *
     */
    public static void connect() {
        if (dc !=null && con == null) {
            org.h2.Driver b = new org.h2.Driver();
            if (dc.getType().equals("mySQL")) {
                try {
                    con = DriverManager.getConnection("jdbc:mysql://" + dc.getHost() + "/" + dc.getDb(), dc.getUser(), dc.getPassword());
                } catch (SQLException e) {
                    logger.error("Error en la conexión mysql: \n" + e.getMessage());
                }
            } else if (dc.getType().equals("H2")) {
                try {
                    con = DriverManager.getConnection("jdbc:h2:./" + dc.getDb() + ";USER=" + dc.getUser() + ";PASSWORD=" + dc.getPassword());
                } catch (SQLException e) {
                    logger.error("Error en la conexión H2:\n "+ e.getMessage());
                }
            }
            checkStructure(dc.getType());
        }
    }

    /**
     * This method is used to close DB Connection on com.EarthSound.utils.connections.SQL
     * Must be called once, at the end of the app
     */
    public static void disconnect(){
        if(con!=null){
            try {
                con.close();
            } catch (SQLException e) {
                logger.error("Ocurrió un error de conexión con la base de datos: \n"+e.getMessage());
            }
        }
    }

    /**
     * Esta función ejecuta consultas de un PreparedStatement
     *
     * @param q      Sentencia a ejecutar
     * @param params parámetros de la sentencia
     * @return El último id insertado ó el número de filas afectadas de la consulta
     */

    public static ResultSet execQuery(String q, List<Object> params) {
        ResultSet result;
        if (con != null) {
            PreparedStatement ps = prepareQuery(q, params);
            try {
                result = ps.executeQuery();
            } catch (SQLException e) {
                if (e.getClass().equals(SQLTimeoutException.class)) {
                    logger.error("El driver ha determinado que el tiempo especificado por setQueryTimeOut ha sido excedido\nCon la sentencia: " + q);
                } else
                    logger.error("Hubo un error en la conexión con la base de datos" + e.getMessage() + "\nCon la sentencia: " + q);
                result = null;
            }
        } else {
            result = null;
        }
        return result;
    }

    /**
     * Este método devuelve PreparedStatements a partir de una lista de objetos que se le pasan como parámetro
     * Esta lista debe estar en orden
     *
     * @param q      Sentencia con "?"
     * @param params parámetros a sustituir las "?"
     * @return PreparedStatement ya listo
     */
    private static PreparedStatement prepareQuery(String q, List<Object> params) {
        PreparedStatement ps = null;
        try {
            ps = con.prepareStatement(q, Statement.RETURN_GENERATED_KEYS);
        } catch (SQLException e) {
            if (e.getClass().equals(SQLFeatureNotSupportedException.class)) {
                logger.error("El driver JDBC no soporta Statement.RETURN_GENERATED_KEYS");
            } else
                logger.error("Error en la conexión mysql: " + e.getMessage() + "\nCon la sentencia" + q);
        }
        if (params != null && ps != null) {
            int i = 1;
            for (Object o : params) {
                try {
                    ps.setObject(i++, o);
                } catch (SQLException e) {
                    logger.error("Hubo un error al intentar setear el parámetro " + i + " del objeto " + o.getClass().getName() + e.getMessage());
                    break;
                }
            }
        }
        return ps;
    }

    /**
     * Esta función ejecuta actualizaciones de un PreparedStatement
     *
     * @param q      Sentencia a ejecutar
     * @param params parámetros de la sentencia
     * @param insert Si es true devuelve el último id insertado
     * @return El último id insertado ó el número de filas afectadas de la consulta
     */
    public static long execUpdate(String q, List<Object> params, boolean insert) {
        long result;
        if (con != null) {
            PreparedStatement ps = prepareQuery(q, params);
            try {
                result = ps.executeUpdate();
            } catch (SQLException e) {
                if (e.getClass().equals(SQLTimeoutException.class)) {
                    logger.error("El driver ha determinado que el tiempo especificado por setQueryTimeOut ha sido excedido\nCon la sentencia: " + q);
                } else
                    logger.error("Hubo un error en la conexión con la base de datos" + e.getMessage() + "\nCon la sentencia: " + q);
                result = -1;
            }
            if (insert) {
                try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
                    if (generatedKeys.next())
                        result = generatedKeys.getLong(1);
                    else
                        result = -1;
                } catch (SQLException e) {
                    if (e.getClass().equals(SQLFeatureNotSupportedException.class))
                        logger.error("El driver JDBC no soporta el método getGeneratedKeys()");
                    else
                        logger.error("Error en la conexión mysql: " + e.getMessage() + "\nCon la sentencia" + q);
                }
            }
        } else {
            result = -1;
        }
        return result;
    }

    /**
     * Esta función revisa que las tablas estén creadas y en caso contrario, las crea con las claves foráneas.
     *
     * @param type Tipo de conexión(H2, mySQL...)
     */
    private static void checkStructure(String type) {
        String sql1, sql2, sql3, sql4, sql5, sql6, sql7, sql8, sql9;
        if (type.equals("mySQL")) {
            sql1 = "CREATE TABLE IF NOT EXISTS artist (" +
                    " id bigint(20) NOT NULL AUTO_INCREMENT," +
                    " name varchar(30) COLLATE utf8mb4_spanish_ci NOT NULL," +
                    " nationality varchar(256) COLLATE utf8mb4_spanish_ci NOT NULL," +
                    " photoURL varchar(256) COLLATE utf8mb4_spanish_ci NOT NULL," +
                    " PRIMARY KEY (id)" +
                    ") ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_spanish_ci;";
            sql2 = "CREATE TABLE IF NOT EXISTS disc (" +
                    " id bigint(20) NOT NULL AUTO_INCREMENT," +
                    " name varchar(256) COLLATE utf8mb4_spanish_ci NOT NULL," +
                    " photoURL varchar(256) COLLATE utf8mb4_spanish_ci NOT NULL," +
                    " pub_date date NOT NULL," +
                    " id_artist bigint(20) NOT NULL," +
                    " PRIMARY KEY (id)," +
                    " CONSTRAINT id_artist FOREIGN KEY (id_artist) REFERENCES artist (id)" +
                    " ON DELETE CASCADE ON UPDATE CASCADE" +
                    ") ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_spanish_ci;";
            sql3 = "CREATE TABLE IF NOT EXISTS genre (" +
                    " id bigint(20) NOT NULL AUTO_INCREMENT," +
                    " name varchar(256) COLLATE utf8mb4_spanish_ci NOT NULL" +
                    ") ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_spanish_ci;";
            sql4 = "CREATE TABLE IF NOT EXISTS song (" +
                    " id bigint(20) NOT NULL AUTO_INCREMENT," +
                    " name varchar(256) COLLATE utf8mb4_spanish_ci NOT NULL," +
                    " duration double NOT NULL," +
                    " songURL varchar(256) COLLATE utf8mb4_spanish_ci NOT NULL," +
                    " id_genre bigint(20) NOT NULL," +
                    " n_plays bigint(20) NOT NULL," +
                    " id_disc bigint(20) NOT NULL," +
                    " PRIMARY KEY (id)," +
                    " CONSTRAINT id_genre FOREIGN KEY (id_genre) REFERENCES genre (id)" +
                    " ON DELETE NO ACTION ON UPDATE CASCADE," +
                    " CONSTRAINT id_disc FOREIGN KEY (id_disc) REFERENCES disc (id)" +
                    " ON DELETE CASCADE ON UPDATE CASCADE" +
                    ") ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_spanish_ci;";
            sql5 = "CREATE TABLE IF NOT EXISTS user (" +
                    " id bigint(20) NOT NULL AUTO_INCREMENT," +
                    " name varchar(256) COLLATE utf8mb4_spanish_ci NOT NULL," +
                    " passwd varchar(256) COLLATE utf8mb4_spanish_ci NOT NULL," +
                    " photoURL varchar(256) COLLATE utf8mb4_spanish_ci NOT NULL," +
                    " disabled tinyint(1) NOT NULL," +
                    " PRIMARY KEY(id)" +
                    ") ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_spanish_ci;";
            sql6 = "CREATE TABLE IF NOT EXISTS playlist (" +
                    " id bigint(20) NOT NULL AUTO_INCREMENT," +
                    " name varchar(256) COLLATE utf8mb4_spanish_ci NOT NULL," +
                    " description varchar(256) COLLATE utf8mb4_spanish_ci NOT NULL," +
                    " id_ucreator bigint(20) NOT NULL," +
                    " PRIMARY KEY(id)," +
                    " CONSTRAINT id_ucreator FOREIGN KEY (id_ucreator) REFERENCES user (id)" +
                    " ON DELETE NO ACTION ON UPDATE NO ACTION" +
                    ") ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_spanish_ci;";
            sql7 = "CREATE TABLE IF NOT EXISTS comment (" +
                    " id bigint(20) NOT NULL AUTO_INCREMENT," +
                    " date timestamp NOT NULL DEFAULT current_timestamp() ON UPDATE current_timestamp()," +
                    " text text COLLATE utf8mb4_spanish_ci NOT NULL," +
                    " id_user bigint(20) NOT NULL," +
                    " id_playlist bigint(20) NOT NULL," +
                    " PRIMARY KEY (id)," +
                    " CONSTRAINT id_user FOREIGN KEY (id_user) REFERENCES user (id)" +
                    " ON DELETE NO ACTION ON UPDATE CASCADE," +
                    " CONSTRAINT id_playlist FOREIGN KEY (id_playlist) REFERENCES playlist (id)" +
                    " ON DELETE NO ACTION ON UPDATE CASCADE" +
                    ") ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_spanish_ci;";
            sql8 = "CREATE TABLE IF NOT EXISTS pl_songs (" +
                    " id bigint(20) NOT NULL AUTO_INCREMENT," +
                    " id_playlist bigint(20) NOT NULL," +
                    " id_song bigint(20) NOT NULL," +
                    " PRIMARY KEY (id)," +
                    " CONSTRAINT id_playlist1 FOREIGN KEY (id_playlist) REFERENCES playlist (id)" +
                    " ON DELETE CASCADE ON UPDATE CASCADE," +
                    " CONSTRAINT id_song FOREIGN KEY (id_song) REFERENCES song (id)" +
                    " ON DELETE CASCADE ON UPDATE CASCADE," +
                    " CONSTRAINT id_song_id_pl UNIQUE (ID_PLAYLIST, ID_SONG)" +
                    ") ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_spanish_ci;";
            sql9 = "CREATE TABLE IF NOT EXISTS suscribe (" +
                    " id bigint(20) NOT NULL AUTO_INCREMENT," +
                    " id_user bigint(20) NOT NULL," +
                    " id_playlist bigint(20) NOT NULL," +
                    " PRIMARY KEY (id)," +
                    " CONSTRAINT id_user1 FOREIGN KEY (id_user) REFERENCES user (id)" +
                    " ON DELETE CASCADE ON UPDATE CASCADE," +
                    " CONSTRAINT id_playlist2 FOREIGN KEY (id_playlist) REFERENCES playlist (id)" +
                    " ON DELETE CASCADE ON UPDATE CASCADE" +
                    ") ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_spanish_ci;";
        } else if (type.equals("H2")) {
            sql1 = "CREATE TABLE IF NOT EXISTS artist (" +
                    " id bigint(20) NOT NULL AUTO_INCREMENT," +
                    " name varchar(30) NOT NULL," +
                    " nationality varchar(256) NOT NULL," +
                    " photoURL varchar(256) NOT NULL," +
                    " PRIMARY KEY (id)" +
                    ")";
            sql2 = "CREATE TABLE IF NOT EXISTS disc (" +
                    " id bigint(20) NOT NULL AUTO_INCREMENT," +
                    " name varchar(256) NOT NULL," +
                    " photoURL varchar(256) NOT NULL," +
                    " pub_date date NOT NULL," +
                    " id_artist bigint(20) NOT NULL," +
                    " PRIMARY KEY (id)," +
                    " CONSTRAINT id_artist FOREIGN KEY (id_artist) REFERENCES artist (id)" +
                    " ON DELETE CASCADE ON UPDATE CASCADE" +
                    ")";
            sql3 = "CREATE TABLE IF NOT EXISTS genre (" +
                    " id bigint(20) NOT NULL AUTO_INCREMENT," +
                    " name varchar(256) NOT NULL" +
                    ")";
            sql4 = "CREATE TABLE IF NOT EXISTS song (" +
                    " id bigint(20) NOT NULL AUTO_INCREMENT," +
                    " name varchar(256) NOT NULL," +
                    " duration double NOT NULL," +
                    " songURL varchar(256) NOT NULL," +
                    " id_genre bigint(20) NOT NULL," +
                    " n_plays bigint(20) NOT NULL," +
                    " id_disc bigint(20) NOT NULL," +
                    " PRIMARY KEY (id)," +
                    " CONSTRAINT id_genre FOREIGN KEY (id_genre) REFERENCES genre (id)" +
                    " ON DELETE NO ACTION ON UPDATE CASCADE," +
                    " CONSTRAINT id_disc FOREIGN KEY (id_disc) REFERENCES disc (id)" +
                    " ON DELETE CASCADE ON UPDATE CASCADE" +
                    ")";
            sql5 = "CREATE TABLE IF NOT EXISTS user (" +
                    " id bigint(20) NOT NULL AUTO_INCREMENT," +
                    " name varchar(256) NOT NULL," +
                    " passwd varchar(256) NOT NULL," +
                    " photoURL varchar(256) NOT NULL," +
                    " disabled tinyint(1) NOT NULL," +
                    " PRIMARY KEY(id)" +
                    ")";
            sql6 = "CREATE TABLE IF NOT EXISTS playlist (" +
                    " id bigint(20) NOT NULL AUTO_INCREMENT," +
                    " name varchar(256) NOT NULL," +
                    " description varchar(256) NOT NULL," +
                    " id_ucreator bigint(20) NOT NULL," +
                    " PRIMARY KEY(id)," +
                    " CONSTRAINT id_ucreator FOREIGN KEY (id_ucreator) REFERENCES user (id)" +
                    " ON DELETE NO ACTION ON UPDATE NO ACTION" +
                    ")";
            sql7 = "CREATE TABLE IF NOT EXISTS comment (" +
                    " id bigint(20) NOT NULL AUTO_INCREMENT," +
                    " date timestamp NOT NULL DEFAULT current_timestamp() ON UPDATE current_timestamp()," +
                    " text text NOT NULL," +
                    " id_user bigint(20) NOT NULL," +
                    " id_playlist bigint(20) NOT NULL," +
                    " PRIMARY KEY (id)," +
                    " CONSTRAINT id_user FOREIGN KEY (id_user) REFERENCES user (id)" +
                    " ON DELETE NO ACTION ON UPDATE CASCADE," +
                    " CONSTRAINT id_playlist FOREIGN KEY (id_playlist) REFERENCES playlist (id)" +
                    " ON DELETE NO ACTION ON UPDATE CASCADE" +
                    ")";
            sql8 = "CREATE TABLE IF NOT EXISTS pl_songs (" +
                    " id bigint(20) NOT NULL AUTO_INCREMENT," +
                    " id_playlist bigint(20) NOT NULL," +
                    " id_song bigint(20) NOT NULL," +
                    " PRIMARY KEY (id)," +
                    " CONSTRAINT id_playlist1 FOREIGN KEY (id_playlist) REFERENCES playlist (id)" +
                    " ON DELETE CASCADE ON UPDATE CASCADE," +
                    " CONSTRAINT id_song FOREIGN KEY (id_song) REFERENCES song (id)" +
                    " ON DELETE CASCADE ON UPDATE CASCADE," +
                    " CONSTRAINT id_song_id_pl UNIQUE (ID_PLAYLIST, ID_SONG)" +
                    ")";
            sql9 = "CREATE TABLE IF NOT EXISTS suscribe (" +
                    " id bigint(20) NOT NULL AUTO_INCREMENT," +
                    " id_user bigint(20) NOT NULL," +
                    " id_playlist bigint(20) NOT NULL," +
                    " PRIMARY KEY (id)," +
                    " CONSTRAINT id_user1 FOREIGN KEY (id_user) REFERENCES user (id)" +
                    " ON DELETE CASCADE ON UPDATE CASCADE," +
                    " CONSTRAINT id_playlist2 FOREIGN KEY (id_playlist) REFERENCES playlist (id)" +
                    " ON DELETE CASCADE ON UPDATE CASCADE" +
                    ")";
        } else {
            sql1 = "";
            sql2 = "";
            sql3 = "";
            sql4 = "";
            sql5 = "";
            sql6 = "";
            sql7 = "";
            sql8 = "";
            sql9 = "";
        }
        try {
            con.setAutoCommit(false);
            if (sql1.startsWith("CREATE TABLE IF NOT EXISTS") && sql2.startsWith("CREATE TABLE IF NOT EXISTS")) {
                execUpdate(sql1, null, false);
                execUpdate(sql2, null, false);
                execUpdate(sql3, null, false);
                execUpdate(sql4, null, false);
                execUpdate(sql5, null, false);
                execUpdate(sql6, null, false);
                execUpdate(sql7, null, false);
                execUpdate(sql8, null, false);
                execUpdate(sql9, null, false);
                con.commit();
            }
            con.setAutoCommit(true);
        } catch (SQLException ex) {
            logger.error("Hubo un error en la conexión con la base de datos o se intentó acceder a una conexión ya cerrada: " + ex.getMessage());
            Dialog.showError("ERROR", "Error creando tablas", "Hubo un error de conexión con la BBDD");
        }
    }
}
