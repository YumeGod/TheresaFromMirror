package server.datebase;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBHelper {

    private static Connection Conn;

    private static String URL = "jdbc:mysql://localhost:3306/fpsmaster";
    private static String UserName = "FPSMaster";
    private static String Password = "DaG7mySiEWAXYHGk";

    public static Connection getConnection() {

        try {
            Class.forName("com.mysql.jdbc.Driver"); // 加载驱动
            System.out.println("Load Driver successfully");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        try {
            Conn = DriverManager.getConnection(URL, UserName, Password);
            System.out.println("Connect successfully");
            return Conn;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

}