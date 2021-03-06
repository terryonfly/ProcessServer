package com.robot.database;

import java.sql.*;
import java.util.ArrayList;
import com.robot.webloader.URLModel;

public class Connector {
    String driver = "com.mysql.jdbc.Driver";
    String url = "jdbc:mysql://localhost/corpus?Unicode=true&characterEncoding=UTF8";
//    String url = "jdbc:mysql://robot.mokfc.com/corpus?Unicode=true&characterEncoding=UTF8";
    String user = "root";
    String password = "513939";

    Connection conn = null;

    public boolean connect() {
        try {
            Class.forName(driver);
            if (conn != null) {
                if (!conn.isClosed()) {
                    conn.close();
                }
                conn = null;
            }
            conn = DriverManager.getConnection(url, user, password);
            if (conn.isClosed()) {
                System.out.println("Failure connecting to the Database");
                return false;
            }
        } catch(ClassNotFoundException e) {
            System.out.println("Sorry, can`t find the Driver");
            e.printStackTrace();
            return false;
        } catch (SQLException e1) {
            e1.printStackTrace();
            return false;
        }
        return true;
    }

    public void disconnect() {
        if (conn != null) {
            try {
                if (!conn.isClosed()) {
                    conn.close();
                }
            } catch (SQLException e1) {
                e1.printStackTrace();
            }
            conn = null;
        }
    }

    public boolean is_connected() {
        if (conn == null) {
            return false;
        }
        try {
            return !conn.isClosed();
        } catch (SQLException e1) {
            e1.printStackTrace();
            return false;
        }
    }

    public ArrayList<URLModel> get_urls(int count) {
        ArrayList<URLModel> urls = new ArrayList<URLModel>();
        try {
            if(!is_connected() && !connect())
                return urls;
            Statement statement = conn.createStatement();
            String sql = "select * from urls where urls.getted = 0 order by rand() limit " + count + ";";
            ResultSet rs = statement.executeQuery(sql);
            while (rs.next()) {
                int url_id = rs.getInt("id");
                String url = rs.getString("url");
                URLModel urlModel = new URLModel();
                urlModel.url_id = url_id;
                urlModel.url = url;
                urls.add(urlModel);
                set_url_status(url_id, 1);
            }
            rs.close();
            statement.close();
        } catch (SQLException e1) {
//            e1.printStackTrace();
            return urls;
        }
        return urls;
    }

    /**
     * url status :
     *
     * 0 new url
     * 1 waiting for load
     * 2 load failure
     * 3 load success
     * 4 waiting for parse
     * 5 parse failed
     * 6 parse success
     **/
    public void set_url_status(int a_url_id, int a_status) {
        try {
            if(!is_connected() && !connect())
                return;
            Statement statement = conn.createStatement();
            String sql = "update corpus.urls set getted=" + a_status + " where id=" + a_url_id + ";";
            statement.executeUpdate(sql);
            statement.close();
        } catch (SQLException e1) {
//            e1.printStackTrace();
            return;
        }
    }

    @Override
    protected void finalize() throws Throwable {
        disconnect();
        super.finalize();
    }
}
