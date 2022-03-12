package theresa.protection;

import org.apache.commons.codec.digest.DigestUtils;
import org.jetbrains.annotations.NotNull;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

public class HWIDUtil {
    protected static @NotNull String getOriginal() {
        try {
            String toEncrypt = "EmoManIsGay" + System.getProperty("COMPUTERNAME") + System.getenv("PROCESSOR_IDENTIFIER") + System.getenv("PROCESSOR_LEVEL");
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(toEncrypt.getBytes());
            StringBuffer hexString = new StringBuffer();

            byte[] byteData = md.digest();

            for (byte aByteData : byteData) {
                String hex = Integer.toHexString(0xff & aByteData);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }

            return hexString.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return "Error";
        }
    }

    public static String getHWID() {
        String hwid = null;
        try {
            hwid = g(getOriginal());
        } catch (Exception ignored) {
        }
        return hwid;
    }


    private static String g(String text) throws NoSuchAlgorithmException {
        text = Base64.getUrlEncoder().encodeToString(text.getBytes());
        MessageDigest md = MessageDigest.getInstance("SHA-1");
        md.update(text.getBytes(StandardCharsets.UTF_8), 0, text.length());
        text = DigestUtils.sha1Hex(text);
        return text.toUpperCase();
    }
}
