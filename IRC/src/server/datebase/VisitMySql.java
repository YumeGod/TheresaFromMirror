
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

    public static void main(String[] args) {
        buy(1, "SuperSkidder", "superskidderbest");
    }

    public static String getItems(String name) {
        String Sql2 = "SELECT items from user where name = '" + name + "'";
        try {
            stt = conn.createStatement();
            ResultSet result2 = stt.executeQuery(Sql2);
            String res = "";
            while (result2.next()) {
                res = res + result2.getString(1);
            }
            return res;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return "";
    }

    public static void recharge(String name, String key) {
        //sql注入过滤
        if (!is_allowed_character(name) || !is_allowed_character(key)) {
            return;
        }
        try {
            conn = DBHelper.getConnection();
            if (conn == null)
                return;
            String Sql = "SELECT fcoin from user where name = '" + name + "'";
            stt = conn.createStatement();
            ResultSet result = stt.executeQuery(Sql);
            int coin;
            if (result.next()) {
                coin = result.getInt(1);
                System.out.println("User:" + name + " has " + coin + " FCoins");
            } else {
                return;
            }
            //判断key是否存在
            String Sql2 = "SELECT `key` FROM `keys` WHERE `key` = '" + key + "'";
            ResultSet result2 = conn.createStatement().executeQuery(Sql2);
            if (result2.next()) {
                String key2 = result2.getString(1);
                System.out.println(key2);
                if (!Objects.equals(key2, "")) {
                    System.out.println("Key exist");

                    //移除Key
                    String Sql3 = "DELETE FROM `keys` WHERE `key` = \"" + key + "\"";
                    conn.createStatement().execute(Sql3);
                    System.out.println("Removed key:" + key);
                    //添加金币
                    String Sql4 = "UPDATE `user` SET `fcoin` = " + (coin + 5) + " WHERE `name` = \"" + name + "\"";
                    conn.createStatement().execute(Sql4);
                    System.out.println("Added Coin");
                }
            } else {
                System.out.println("Key not found");
            }

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

    public static void buy(int id, String name, String password) {
        //sql注入过滤
        if (!is_allowed_character(name) || !is_allowed_character(password)) {
            return;
        }


        if (!verify(name, password)) {
            return;
        }

        //读取物品信息
        try {
            //获取用户FCoin
            conn = DBHelper.getConnection();
            if (conn == null)
                return;
            String Sql = "SELECT fcoin from user where name = '" + name + "'";
            stt = conn.createStatement();
            ResultSet result = stt.executeQuery(Sql);
            int coin;
            if (result.next()) {
                coin = result.getInt(1);
                System.out.println("User:" + name + " has " + coin + " FCoins");
            } else {
                return;
            }

            //判断item是否存在
            String Sql2 = "SELECT price from ornaments where ornament = " + id;
            stt = conn.createStatement();
            ResultSet result2 = stt.executeQuery(Sql2);
            int item;
            if (result2.next()) {
                item = result2.getInt(1);
                System.out.println("Item " + " exist ,price:" + item);
//                String Sql3 = " price from ornaments where ornament = " + id;
//                stt = conn.createStatement();
//                stt.execute(Sql2);

            } else {
                System.out.println("Key not found");
            }

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