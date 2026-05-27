
import java.io.*;
import java.security.MessageDigest;
import java.sql.*;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Base64;
import java.util.Random;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

public class Secure {

    // --- CONFIGURATION ---
    private static final String url = "jdbc:mysql://localhost:3306/LogDB";
    private static final String username = "root";
    private static final String password = "";
    private static final String SECRET_KEY = "MySuperSecretKey";
    private static final String FILE_PATH = "chat_logs.txt";

    public static void main(String[] args) {
        System.out.println("=== SECURE LOG SYSTEM STARTED ===\n");

        
        generateRawLogs();


        processAndSecureLogs();

        System.out.println("\n=== SYSTEM SHUTDOWN ===");
    }


    // LOG GENERATOR METHOD

    private static void generateRawLogs() {
        System.out.println("[*] Generating raw logs in " + FILE_PATH + "...");

        String[] users = {"Kajal", "User102", "Admin"};
        String[] actions = {"Login successful.", "Transferred Rs. 5000.", "Database backup done."};

        try (FileWriter writer = new FileWriter(FILE_PATH, false)) {
            Random rand = new Random();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("hh:mm a");

            for (int i = 0; i < 5; i++) {
                String time = LocalTime.now().minusMinutes(rand.nextInt(60)).format(formatter);
                String user = users[rand.nextInt(users.length)];
                String action = actions[rand.nextInt(actions.length)];

                String logEntry = "[" + time + "] " + user + ": " + action + "\n";
                writer.write(logEntry);
            }
            System.out.println("[+] Raw logs generated successfully.");
        } catch (IOException e) {
            System.out.println("[-] Error generating logs: " + e.getMessage());
        }
    }

    private static void processAndSecureLogs() {
        System.out.println("\n[*] Connecting to Database & Processing Logs...");

        try (Connection conn = DriverManager.getConnection(url,username,password);
             BufferedReader reader = new BufferedReader(new FileReader(FILE_PATH))) {

            String insertSQL = "INSERT INTO secure_logs (encrypted_message, security_hash) VALUES (?, ?)";
            PreparedStatement pstmt = conn.prepareStatement(insertSQL);

            String line;
            int count = 0;

            while ((line = reader.readLine()) != null) {

                String encryptedMsg = encrypt(line);

                String hash = generateHash(line);


                pstmt.setString(1, encryptedMsg);
                pstmt.setString(2, hash);
                pstmt.executeUpdate();

                count++;
            }
            System.out.println("[+] Successfully secured " + count + " logs in MySQL Database.");

        } catch (SQLException | IOException e) {
            System.out.println("[-] Error processing logs: " + e.getMessage());
        }
    }

    //  SECURITY HELPER METHODS

    private static String encrypt(String data) {
        try {
            SecretKeySpec secretKey = new SecretKeySpec(SECRET_KEY.getBytes(), "AES");
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);
            byte[] encryptedBytes = cipher.doFinal(data.getBytes());
            return Base64.getEncoder().encodeToString(encryptedBytes);
        } catch (Exception e) {
            throw new RuntimeException("Error in encryption", e);
        }
    }
    private static String generateHash(String data) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = digest.digest(data.getBytes("UTF-8"));

            StringBuilder hexString = new StringBuilder();
            for (byte b : hashBytes) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (Exception e) {
            throw new RuntimeException("Error in hashing", e);
        }
    }
}