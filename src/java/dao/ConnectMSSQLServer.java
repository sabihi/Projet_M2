/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dao;

/**
 *
 * @author Naoufal
 */
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ConnectMSSQLServer {

    private static final String HOST = "jdbc:sqlserver";
    private static final String SERVER_NAME = "localhost";
    private static final int PORT = 1433;
    private static final String DATABASE_NAME = "pweb";
    private static final String USER = "sa";
    private static final String PASSWORD = "s123456N";

    /**
     * Create URL for connection based on given informations.
     *
     * @return Complete URL string
     */
    public static String makeURL() {
        if (USER.equals("") && PASSWORD.equals("")) {
            return HOST + "://" + SERVER_NAME + ":" + PORT + ";" + "databasename=" + DATABASE_NAME + ";"
                    + "integratedSecurity=true";
        }

        return HOST + "://" + SERVER_NAME + ":" + PORT + ";" + "databasename=" + DATABASE_NAME + ";"
                + "user=" + USER + ";" + "password=" + PASSWORD + ";";
    }

    /**
     * Get connection to server.
     *
     * @return connection
     */
    public static Connection getConnect() {
        Connection cnn = null;
        String url = makeURL();
        //System.out.println(url);
        try {
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
            //System.out.println("Driver Ok");
            cnn = DriverManager.getConnection(url);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return cnn;
    }

//    public static void main(String[] args) throws SQLException {
//        ConnectMSSQLServer connServer = new ConnectMSSQLServer();
//        connServer.getConnect();
//        ResultSet resultSet=connServer.select("SELECT * FROM [dbo].[ws]");
//        
//        while(resultSet.next()){
//                System.out.println(resultSet.getString("id")+" "+resultSet.getString("wsname")+"\n");                
//            }
//    }
    
    public static ResultSet select(String sql) throws SQLException {
    Connection cnn = getConnect();
    PreparedStatement pst = cnn.prepareStatement(sql);
    return pst.executeQuery();
  }

    

   

}
