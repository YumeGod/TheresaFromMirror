package me.superskidder.datebase;

import me.superskidder.Server;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBHelper {

    private static Connection Conn;

    private static String URL = "jdbc:mysql://localhost:3306/"+ Server.INSTANCE.db;
    private static String UserName = Server.INSTANCE.db_userName;
    private static String Password = Server.INSTANCE.db_password;

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