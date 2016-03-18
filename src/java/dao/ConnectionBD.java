package dao;

import java.sql.DriverManager;
import com.mysql.jdbc.Connection;
import com.mysql.jdbc.Statement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ConnectionBD {

    private String url = "jdbc:mysql://localhost:3306/pweb";
    private String user = "root";
    private String passwd = "";
    private Connection conn;
    private static ConnectionBD mycnx;

    public static ConnectionBD connectionFactory() {
        if (mycnx == null) {
            mycnx = new ConnectionBD();
        }
        return mycnx;
    }

    public static int exec(String req) throws SQLException {
        Connection conn = ConnectionBD.connectionFactory().getConn();
        Statement state = (Statement) conn.createStatement();
        return state.executeUpdate(req);// insert up del
    }

    public static ResultSet select(String req) throws SQLException {
        Connection conn = ConnectionBD.connectionFactory().getConn();
        Statement state = (Statement) conn.createStatement();
        return state.executeQuery(req);
    }

    private ConnectionBD() {
        try {
            Class.forName("com.mysql.jdbc.Driver");
            System.out.println("DRIVER OK ! ");
            conn = (Connection) DriverManager.getConnection(url, user, passwd);
            System.out.println("Connection effective !");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void loadAssocietedAccount(int id) {
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getPasswd() {
        return passwd;
    }

    public void setPasswd(String passwd) {
        this.passwd = passwd;
    }

    public Connection getConn() {
        return conn;
    }

    public void setConn(Connection conn) {
        this.conn = conn;
    }

    public ConnectionBD(String url, String user, String passwd) {
        super();
        this.url = url;
        this.user = user;
        this.passwd = passwd;
    }

}
