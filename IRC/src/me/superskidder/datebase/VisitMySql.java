
package me.superskidder.datebase;

import java.sql.Connection;
import java.sql.ResultSet;
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

    public static String verify(String name, String password, String hwid) {
        //sql注入过滤
        if (!is_allowed_character(name) || !is_allowed_character(password) || !is_allowed_character(hwid)) {
            return "Failed(SQL INJECTION)";
        }
        try {
            conn = DBHelper.getConnection();
            if (conn == null)
                return "Failed to connect to database";
            String Sql = "SELECT * FROM user WHERE name='" + name + "' AND password='" + password + "'";
            stt = conn.createStatement();
            set = stt.executeQuery(Sql);
            while (set.next()) {
                if (set.getString(1) != null) {
                    Sql = "SELECT * FROM user WHERE name='" + name + "' AND password='" + password + "' AND hwid='" + hwid + "'";
                    stt = conn.createStatement();
                    set = stt.executeQuery(Sql);
                    while (set.next()) {
                        if (set.getString(1) != null)
                            return "Success";
                    }
                    Sql = "SELECT * FROM user WHERE name='" + name + "'";
                    stt = conn.createStatement();
                    set = stt.executeQuery(Sql);
                    while (set.next()) {
                        if ((System.currentTimeMillis() - set.getInt(6)) > 1000 * 60 * 60 * 24 * 14) {
                            Sql = "UPDATE user SET hwid='" + hwid + "' WHERE name='" + name + "'";
                            stt = conn.createStatement();
                            stt.execute(Sql);
                            Sql = "UPDATE user SET date='" + System.currentTimeMillis() + "' WHERE name='" + name + "'";
                            stt = conn.createStatement();
                            stt.execute(Sql);
                            return "Succeed to reset HWID!";
                        }
                    }
                    return "Failed to verify(HWID not match)";
                }

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
        return "Failed to verify(Unknown reason)";
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