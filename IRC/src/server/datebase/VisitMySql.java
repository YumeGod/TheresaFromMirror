
package server.datebase;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Objects;

public class VisitMySql {

    private static Connection conn;
    private static Statement stt;
    private static ResultSet set;

    public static boolean is_allowed_character(String number) {
        if (number == null) return false;
        return number.matches("[A-Za-z0-9]+");
    }


    public static void register(String name, String password) {
        //sql注入过滤
        if (!is_allowed_character(name) || !is_allowed_character(password)) {
            return;
        }

        if (nameexists(name))
            return;

        try {
            conn = DBHelper.getConnection();
            if (conn == null)
                return;
            String Sql = "INSERT INTO user VALUES ('" + name + "','" + password + "',0,0)";
            stt = conn.createStatement();
            stt.execute(Sql);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                Objects.requireNonNull(conn).close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static boolean verify(String name, String password) {
        try {
            conn = DBHelper.getConnection();
            if (conn == null)
                return false;
            String Sql = "SELECT * FROM user WHERE name='" + name + "' AND password='" + password + "'";
            stt = conn.createStatement();
            set = stt.executeQuery(Sql);
            while (set.next()) {
                if (set.getString(1) != null)
                    return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {

            try {
                set.close();
                conn.close();
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
        return false;
    }

    public static boolean nameexists(String name) {
        try {
            conn = DBHelper.getConnection();
            if (conn == null)
                return false;
            String Sql = "SELECT * FROM user WHERE name='" + name + "'";
            stt = conn.createStatement();
            set = stt.executeQuery(Sql);
            while (set.next()) {
                if (set.getString(1) != null)
                    return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {

            try {
                set.close();
                conn.close();
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
        return false;
    }

}