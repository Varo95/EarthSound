package com.EarthSound.utils.connections;

import com.EarthSound.Start;
import com.EarthSound.models.beans.DataConnection;
import com.EarthSound.utils.Dialog;
import com.EarthSound.utils.Tools;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLTimeoutException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * No te he traducido los comentarios de esta clase por pereza, son métodos reutilizados
 * de mi propio código del año pasado, aunque ligeramente modificados
 */
public class SQL {
    private static Connection con;
    private static final DataConnection dc = Tools.loadXML();

    private static final Logger logger = LoggerFactory.getLogger(SQL.class);

    /**
     * This method is used to establish DB Connection on com.EarthSound.utils.connections.SQL
     * Must be called once, at the start of the app
     *
     */
    public static void connect() {
        if (dc !=null && con == null) {
            org.h2.Driver b = new org.h2.Driver();
            try{
                con = switch (dc.getType()){
                    case "MySQL" -> DriverManager.getConnection("jdbc:mysql://" + dc.getHost() + "/" + dc.getDb(), dc.getUser(), dc.getPassword());
                    case "H2" -> DriverManager.getConnection("jdbc:h2:./" + dc.getDb() + ";USER=" + dc.getUser() + ";PASSWORD=" + dc.getPassword());
                    default -> null;
                };
            }catch (final SQLException e){
                logger.error("Error en la conexión {}: \n{}", dc.getType(), e.getMessage());
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
            } catch (final SQLException e) {
                logger.error("Ocurrió un error de conexión con la base de datos: \n{}", e.getMessage());
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

    public static ResultSet execQuery(final String q, final List<Object> params) {
        ResultSet result;
        if (con != null) {
            final PreparedStatement ps = prepareQuery(q, params);
            try {
                result = ps.executeQuery();
            } catch (final SQLException e) {
                final String message = e.getClass().equals(SQLTimeoutException.class) ? "El driver ha determinado que el tiempo especificado por setQueryTimeOut ha sido excedido\nCon la sentencia: " + q : "Hubo un error en la conexión con la base de datos" + e.getMessage() + "\nCon la sentencia: " + q;
                logger.error(message);
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
    private static PreparedStatement prepareQuery(final String q, final List<Object> params) {
        PreparedStatement ps = null;
        try {
            ps = con.prepareStatement(q, Statement.RETURN_GENERATED_KEYS);
        } catch (final SQLException e) {
            final String message = e.getClass().equals(SQLTimeoutException.class) ? "El driver ha determinado que el tiempo especificado por setQueryTimeOut ha sido excedido\nCon la sentencia: " + q : "Hubo un error en la conexión con la base de datos" + e.getMessage() + "\nCon la sentencia: " + q;
            logger.error(message);
        }
        if (params != null && ps != null) {
            int i = 1;
            for (final Object o : params) {
                try {
                    ps.setObject(i++, o);
                } catch (final SQLException e) {
                    logger.error("Hubo un error al intentar setear el parámetro {} del objeto {}{}", i, o.getClass().getName(), e.getMessage());
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
    public static long execUpdate(final String q, final List<Object> params, final boolean insert) {
        long result;
        if (con != null) {
            final PreparedStatement ps = prepareQuery(q, params);
            try {
                result = ps.executeUpdate();
                if (insert) {
                    try (final ResultSet generatedKeys = ps.getGeneratedKeys()) {
                        if (generatedKeys.next()) result = generatedKeys.getLong(1);
                        else result = -1;
                    } catch (final SQLException e) {
                        final String message = e.getClass().equals(SQLTimeoutException.class) ? "El driver ha determinado que el tiempo especificado por setQueryTimeOut ha sido excedido\nCon la sentencia: " + q : "Hubo un error en la conexión con la base de datos" + e.getMessage() + "\nCon la sentencia: " + q;
                        logger.error(message);
                    }
                }
                ps.close();
            } catch (final SQLException e) {
                final String message = e.getClass().equals(SQLTimeoutException.class) ? "El driver ha determinado que el tiempo especificado por setQueryTimeOut ha sido excedido\nCon la sentencia: " + q : "Hubo un error en la conexión con la base de datos" + e.getMessage() + "\nCon la sentencia: " + q;
                logger.error(message);
                result = -1;
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
    private static void checkStructure(final String type) {
        try {
            con.setAutoCommit(false);
            for(final String table : getTables(type)){
                execUpdate(table, null, false);
            }
            con.setAutoCommit(true);
        } catch (final SQLException ex) {
            logger.error("Hubo un error en la conexión con la base de datos o se intentó acceder a una conexión ya cerrada: {}", ex.getMessage());
            Dialog.showError("ERROR", "Error creando tablas", "Hubo un error de conexión con la BBDD");
        }
    }

    /**
     * This method is used to get the tables from the SQL file
     * @param type type of connection
     * @return List of tables
     */
    private static List<String> getTables(final String type){
        final List<String> result = new ArrayList<>();
        try(final Reader reader = new InputStreamReader(Objects.requireNonNull(Start.class.getResourceAsStream("db/" + type + "_Tables.sql")));
            final BufferedReader fr = new BufferedReader(reader)){
                final StringBuilder sb = new StringBuilder();
                int i;
                while((i = fr.read()) != -1){
                    sb.append((char) i);
                }
                result.addAll(Arrays.asList(sb.toString().split(";")));
        }catch (final IOException e){
            logger.error("Hubo un error al intentar leer el archivo de estructura de la base de datos: {}", e.getMessage());
        }
        return result;
    }
}