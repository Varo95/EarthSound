package com.EarthSound.models.beans;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "DataConnection")
@XmlAccessorType(XmlAccessType.FIELD)
public class DataConnection {

    public enum types {
        MYSQL("MySQL"),
        H2("H2");
        private String ct;
        types(String ct) { this.ct = ct; }
        public String getCt(){ return this.ct; }
    }

    private String host;
    private String db;
    private String user;
    private String password;
    private String type;

    public DataConnection(String host, String db, String user, String password, String type) {
        this.host = host;
        this.db = db;
        this.user = user;
        this.password = password;
        if(type.equals(types.MYSQL.name())){
            this.type = "MySQL";
        }else{
            this.type = "H2";
        }
    }

    public DataConnection() {
        this("","","","","");
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getDb() {
        return db;
    }

    public void setDb(String db) {
        this.db = db;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}