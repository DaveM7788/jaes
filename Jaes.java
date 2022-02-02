import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Console;
import java.io.BufferedInputStream;
import java.io.InputStream;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.math.BigInteger;

public class Jaes {

    static File input;
    static String encryptOrDe;
    static char[] keyFromConsole;
    static boolean quietMode;

    public static void main(String[] args) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException,
                                                  IllegalBlockSizeException, BadPaddingException, IOException {

        if (args.length == 2 || args.length == 3) {
            input = new File(args[0]);
            encryptOrDe = args[1];
            if (!(encryptOrDe.equals("e") || encryptOrDe.equals("d"))) printIncorrectUsage();
        } else {
            printIncorrectUsage();
        }

        if (args.length == 3 && args[2].equals("-q")) {
            quietMode = true;
        } else {
            quietMode = false;
        }
        String secret = handleSecretKey();

        if (input.isDirectory()) {
            System.out.println("Input file is a directory. Only files are supported");
            System.exit(0);
        } else {
            boolean toEncrypt = encryptOrDe.equals("e");
            cryptFile(secret, input.getPath(), input.getPath(), toEncrypt);
        }
    }

    private static void printIncorrectUsage() {
        System.out.println("Incorrect CLI arguments\n");
        System.out.println("arg 1: path of file to encrypt or decrypt");
        System.out.println("arg 2: (e)encrypt or (d)decrypt");
        System.out.println("optional arg 3: pass in -q if you don't want to view file contents after encryption");
        System.out.println("usage: \n$ java Jaes input.txt e");
        System.exit(0);
    }

    private static String handleSecretKey() throws NoSuchAlgorithmException {
        Console console = System.console();

        if (console == null) {
            System.out.println("Could not find console. Exiting...");
            System.exit(0);
        } else {
            System.out.println("Enter key for encryption or decryption: ");
            keyFromConsole = console.readPassword();
        }
        String convertedKey = String.valueOf(keyFromConsole);

        MessageDigest md = MessageDigest.getInstance("SHA-256");
        md.update(convertedKey.getBytes(StandardCharsets.UTF_8));
        byte[] digest = md.digest();
        String hex = String.format("%064x", new BigInteger(1, digest));
        return hex.substring(0, 32);
    }

    private static void cryptFile(String secretKey, String fileInputPath, String fileOutPath, boolean isEncryption)
    throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IOException,
           IllegalBlockSizeException, BadPaddingException {

        String successAppend = "";
        var key = new SecretKeySpec(secretKey.getBytes(), "AES");
        var cipher = Cipher.getInstance("AES");
        if (isEncryption) {
            cipher.init(Cipher.ENCRYPT_MODE, key);
            successAppend = "encrypted";
        } else {
            cipher.init(Cipher.DECRYPT_MODE, key);
            successAppend = "decrypted";
        }

        var fileInput = new File(fileInputPath);
        var inputStream = new FileInputStream(fileInput);
        var inputBytes = new byte[(int) fileInput.length()];
        inputStream.read(inputBytes);

        var outputBytes = cipher.doFinal(inputBytes);

        var fileEncryptOut = new File(fileOutPath);
        var outputStream = new FileOutputStream(fileEncryptOut);
        outputStream.write(outputBytes);

        inputStream.close();
        outputStream.close();

        handlePostCryption(fileOutPath, successAppend);
    }

    private static void handlePostCryption(String fileOutPath, String successAppend) throws IOException {
        if (!quietMode) {
            System.out.println("File successfully " + successAppend);
            System.out.println("New File: " + fileOutPath);

            InputStream input = new BufferedInputStream(new FileInputStream(fileOutPath));
            byte[] buffer = new byte[8192];

            try {
                for (int length = 0; (length = input.read(buffer)) != -1;) {
                    System.out.write(buffer, 0, length);
                }
            } finally {
                input.close();
            }
        }
    }
}