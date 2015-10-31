package com.robot.database;

import java.sql.*;
import java.util.ArrayList;

public class Connector {
    String driver = "com.mysql.jdbc.Driver";
//    String url = "jdbc:mysql://localhost/corpus?Unicode=true&characterEncoding=UTF8";
    String url = "jdbc:mysql://robot.mokfc.com/corpus?Unicode=true&characterEncoding=UTF8";
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

    public void add_url(String a_url) {
        if (a_url.length() > 250) {
            System.err.printf("New url's length > 250 and pass\n");
            return;
        }
        try {
            if(!is_connected() && !connect())
                return;
            Statement statement = conn.createStatement();
            String sql = "insert into `corpus`.`urls` values(NULL, '" + a_url + "', 0) on duplicate key update urls.getted = urls.getted;";
            statement.executeUpdate(sql);
            statement.close();
        } catch (SQLException e1) {
//            System.err.printf("add_url err : %s\n", e1);
//            e1.printStackTrace();
            return;
        }
    }

    public ArrayList<String> get_urls(int count) {
        ArrayList<String> urls = new ArrayList<String>();
        try {
            if(!is_connected() && !connect())
                return urls;
            Statement statement = conn.createStatement();
            String sql = "select * from urls where urls.getted = 0 order by rand() limit " + count + ";";
            ResultSet rs = statement.executeQuery(sql);
            while (rs.next()) {
                int url_id = rs.getInt("id");
                urls.add(rs.getString("url"));
                set_url_getted(url_id);
            }
            rs.close();
            statement.close();
        } catch (SQLException e1) {
//            e1.printStackTrace();
            return urls;
        }
        return urls;
    }

    public void set_url_getted(int a_url_id) {
        try {
            if(!is_connected() && !connect())
                return;
            Statement statement = conn.createStatement();
            String sql = "update corpus.urls set getted=1 where id=" + a_url_id + ";";
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
