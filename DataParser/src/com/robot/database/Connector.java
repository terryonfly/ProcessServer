package com.robot.database;

import com.robot.webparser.URLModel;

import java.sql.*;

import java.util.ArrayList;

public class Connector {
    String driver = "com.mysql.jdbc.Driver";
    String url = "jdbc:mysql://localhost/corpus?Unicode=true&characterEncoding=UTF8";
//        String url = "jdbc:mysql://robot.mokfc.com/corpus?Unicode=true&characterEncoding=UTF8";
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

    public ArrayList<URLModel> get_unparsed_urls(int count) {
        ArrayList<URLModel> urls = new ArrayList<URLModel>();
        try {
            if(!is_connected() && !connect())
                return urls;
            Statement statement = conn.createStatement();
            String sql = "select * from urls where urls.getted = 3 order by rand() limit " + count + ";";
            ResultSet rs = statement.executeQuery(sql);
            while (rs.next()) {
                int url_id = rs.getInt("id");
                String url = rs.getString("url");
                URLModel urlModel = new URLModel();
                urlModel.url_id = url_id;
                urlModel.url = url;
                urls.add(urlModel);
                set_url_status(url_id, 4);
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

    public void add_web_content(String a_web_content) {
        try {
            if(!is_connected() && !connect())
                return;
            a_web_content = a_web_content.replaceAll("'", "\\'");
            Statement statement = conn.createStatement();
            String sql = "insert into `corpus`.`web_content` values (NULL, '" + a_web_content + "', 0);";
            statement.executeUpdate(sql);
            statement.close();
        } catch (SQLException e1) {
//            System.err.printf("add_web_content err : %s\n", e1);
//            e1.printStackTrace();
            return;
        }
    }

    public String get_web_content() {
        String web_content = "";
        try {
            if(!is_connected() && !connect())
                return web_content;
            Statement statement = conn.createStatement();
            String sql = "select * from web_content where web_content.has_split = 0 limit 5000;";
            ResultSet rs = statement.executeQuery(sql);
            while (rs.next()) {
                int web_content_id = rs.getInt("id");
                web_content += rs.getString("web_content");
                web_content += "\n";
                set_web_content_getted(web_content_id);
            }
            rs.close();
            statement.close();
        } catch (SQLException e1) {
//            e1.printStackTrace();
            return web_content;
        }
        return web_content;
    }

    public void set_web_content_getted(int a_web_content_id) {
        try {
            if(!is_connected() && !connect())
                return;
            Statement statement = conn.createStatement();
            String sql = "update corpus.web_content set has_split=1 where id=" + a_web_content_id + ";";
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
