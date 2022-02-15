package cn.loli.client.protection;

import java.lang.reflect.InvocationTargetException;

public class Meme {
    public Meme(){
        try {
            Class.forName("javax.swing.JOptionPane").getDeclaredMethod("showMessageDialog",
                    java.awt.Component.class, Object.class, String.class, int.class).invoke(Class.forName("javax.swing.JOptionPane"),
                    null, "NO DEBUG PLZ? " + "\n" + "Debugging is just skidding with extra work ;)", "Theresa.exe", 0);
        } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}
