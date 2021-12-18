import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Console;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;
import javax.crypto.*;

public class Jaes {

    static File input;
    static String encryptOrDe;
    static char[] keyFromConsole;

    public static void main(String[] args) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException,
    IllegalBlockSizeException, BadPaddingException, IOException {

        if (args.length == 2) {
            input = new File(args[0]);
            encryptOrDe = args[1];
            if (!(encryptOrDe.equals("e") || encryptOrDe.equals("d"))) printIncorrectUsage();
        } else {
            printIncorrectUsage();
        }

        String secret = handleSecretKey();

        if (input.isDirectory()) {
            System.out.println("input is directory");
        } else {
            boolean toEncrypt = encryptOrDe.equals("e");
            cryptFile(secret, input.getPath(), input.getPath(), toEncrypt);
        }        
    }

    private static void printIncorrectUsage() {
        System.out.println("Incorrect CLI arguments\n");
        System.out.println("arg 1: path of input file or directory");
        System.out.println("arg 2: (e)encrypt or (d)decrypt");
        System.out.println("optional arg 3: delete input file(s) afterwards = y or n\n");
        System.out.println("usage: \n$ java Jaes input.txt e y");
    }

    private static String handleSecretKey() {
        Console console = System.console();

        if (console == null) {
            System.out.println("Could not find console. Exiting...");
            System.exit(0);
        } else {
            System.out.println("Enter key for encryption or decryption: ");
            keyFromConsole = console.readPassword();
        }
        String convertedKey = String.valueOf(keyFromConsole);

        if (convertedKey.length() > 16) {
            convertedKey = convertedKey.substring(0, 16);
        } else {
            int inputLength = convertedKey.length();
            int diff = 16 - inputLength;
            String appendToKey = "fR7mYfdr954r9941";
            appendToKey = appendToKey.substring(0, diff);
            convertedKey += appendToKey;
        }
        return convertedKey;
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

        System.out.println("File successfully " + successAppend);
        System.out.println("New File: " + fileOutPath);
    }
}