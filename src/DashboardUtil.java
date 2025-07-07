import java.io.*;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.util.*;


public class DashboardUtil {

    public static void logActivity(String message) {
        try {
            Files.createDirectories(Paths.get("logs")); // ensure logs dir exists
            FileWriter fw = new FileWriter("logs/activity.log", true);
            fw.write(LocalDateTime.now() + " - " + message + "\n");
            fw.close();
        } catch (IOException ignored) {}
    }

    public static void logEncryptedFile(String filePath) {
        try {
            Files.createDirectories(Paths.get("logs"));
            FileWriter fw = new FileWriter("logs/encrypted_files.log", true);
            fw.write(filePath + "\n");
            fw.close();
        } catch (IOException ignored) {}
    }

    public static int getEncryptedFileCount() {
        File log = new File("logs/encrypted_files.log");
        int count = 0;
        try (BufferedReader br = new BufferedReader(new FileReader(log))) {
            while (br.readLine() != null) count++;
        } catch (IOException ignored) {}
        return count;
    }

}
