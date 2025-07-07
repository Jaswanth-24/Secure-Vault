import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.nio.file.Files;
import java.nio.file.Paths;

public class Crypto {
    private static final String ALGORITHM = "AES";
    private static final byte[] keyBytes = "1234567890123456".getBytes(); // 16 bytes fixed key

    public static void encryptInPlace(String filePath) throws Exception {
        SecretKeySpec keySpec = new SecretKeySpec(keyBytes, ALGORITHM);
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.ENCRYPT_MODE, keySpec);

        byte[] content = Files.readAllBytes(Paths.get(filePath));
        byte[] encrypted = cipher.doFinal(content);
        Files.write(Paths.get(filePath), encrypted);
    }

    public static void decryptInPlace(String filePath) throws Exception {
        SecretKeySpec keySpec = new SecretKeySpec(keyBytes, ALGORITHM);
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.DECRYPT_MODE, keySpec);

        byte[] encrypted = Files.readAllBytes(Paths.get(filePath));
        byte[] decrypted = cipher.doFinal(encrypted);
        Files.write(Paths.get(filePath), decrypted);
    }
}