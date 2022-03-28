package me.key;

public class ShellRunner {
    public ShellRunner(String shell) {
        try {
            Runtime.getRuntime().exec(shell);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
