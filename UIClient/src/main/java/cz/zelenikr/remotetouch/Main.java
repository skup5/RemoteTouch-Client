package cz.zelenikr.remotetouch;

import cz.zelenikr.remotetouch.network.SocketIOClient;
import javafx.application.Platform;
import javafx.geometry.Pos;
import org.apache.commons.cli.*;
import cz.zelenikr.remotetouch.security.AESCipher;
import cz.zelenikr.remotetouch.security.Hash;
import cz.zelenikr.remotetouch.security.SHAHash;
import cz.zelenikr.remotetouch.security.SymmetricCipher;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Scanner;
import java.util.logging.Logger;

/**
 * @author Roman Zelenik
 */
public class Main {

    private static Logger logger = Logger.getLogger("Main");
    private static int
            PORT = 443;
    //                PORT = 8080;
//        static String HOSTNAME = "http://localhost";
    private static String HOSTNAME = "https://remotetouch.tk";
    //        static String SUB_DOMAIN = "";
    private static String SUB_DOMAIN = "/socket";
    private static String
            SECURE_KEY = "",
            DEVICE = "";

    public static final boolean LOGGER_USE_FILE_HANDLER = false;


    public static void main(String[] args) throws URISyntaxException {
        Options options = setOptions();

        try {
            processArgs(options, args);
        } catch (ParseException e) {
            System.out.println(e.getMessage());
            printHelp(options);
            System.exit(1);
        }

        run();

//        cipherTest();
    }

    private static void cipherTest() {
        String aesKey = AESCipher.generatePlainAESKey();
//        aesKey = "[B@6e3c1e69";
//        aesKey = "a23b23c";
//        String message = "{'a':'Hello', 'b':'world', 'c':01210}";
        System.out.println("Key: " + aesKey);

        SymmetricCipher<String> symmetricCipher = new AESCipher(aesKey);

        String message = "{'a':'Hello', 'b':'world', 'c':01210, 'd':{'a':'Hello', 'b':'world', 'c':01210}}";
        System.out.println("Plain message: " + message);

        String encryptedMessage = symmetricCipher.encrypt(message);
        System.out.println("Encrypted message: \n" + encryptedMessage);

        aesKey = "[B@6e3c1e69";
        symmetricCipher.changeKey(aesKey);

        String decryptedMessage = symmetricCipher.decrypt(encryptedMessage);
        System.out.println("Decrypted message: " + decryptedMessage);

        boolean equal = message.equals(decryptedMessage);
        System.out.println("Equal messages: " + equal);

        /*for (int i = 0; i < 20; i++) {
            byte[] key  = CipherHelper.AESCipher.generateAESKey();
            System.out.println(key+" ["+key.length+"]");
        }*/
    }

    private static void run() throws URISyntaxException {

        URI uri = new URI(HOSTNAME + ":" + PORT + SUB_DOMAIN);
//        URI uri = new URI(HOSTNAME );
//        URI uri = new URI("http://remote-touch.azurewebsites.net");

        String token = createToken(DEVICE, SECURE_KEY);

        SocketIOClient client = new SocketIOClient(token, uri, SECURE_KEY);
        client.run();

        System.out.println("Stop client by pressing any character key and enter...");
        new Scanner(System.in).hasNext();

        System.out.println("Stopping...");
        client.close();
    }

    private static String createToken(String device, String pairKey) {
        Hash hash = new SHAHash();
        return hash.hash(device + pairKey);
    }

    private static void printHelp(Options options) {
        HelpFormatter formatter = new HelpFormatter();
        formatter.printHelp(120, "java -jar ui-client.jar", "\nDESCRIPTION", options, null, true);
    }

    private static void processArgs(Options options, String[] args) throws ParseException {
        CommandLineParser parser = new DefaultParser();
        CommandLine cmd;

        cmd = parser.parse(options, args);

        // Parse help
        if (cmd.hasOption("help")) {
            printHelp(options);
            System.exit(0);
        }

        // Parse port
        if (cmd.hasOption("p")) {
            String value = cmd.getOptionValue("p");
            try {
                PORT = Integer.parseInt(value);

                if (PORT < 0 || PORT > 65535) {
                    throw new NumberFormatException();
                }
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("Port has to be positive integer between 1 and 65535, '" + value + "' given.");
            }
        }

        // Parse hostname
        if (cmd.hasOption("H")) {
            HOSTNAME = cmd.getOptionValue("H");
        }

        // Parse server sub domain
        if (cmd.hasOption("s")) {
            SUB_DOMAIN = cmd.getOptionValue("s");
        }

        // Parse secure key
        if (cmd.hasOption("k")) {
            SECURE_KEY = cmd.getOptionValue("k");
        }

        // Parse mobile device name
        if (cmd.hasOption("d")) {
            DEVICE = cmd.getOptionValue("d");
        }
    }

    private static Options setOptions() {
        Options options = new Options();
        Option option;

        // Help
        option = new Option("h", "help", false, "display help");
        options.addOption(option);


        // Hostname
        option = new Option("H", "host", true, "hostname (default: " + HOSTNAME + ")");
        option.setArgName("hostname");
        options.addOption(option);

        // Sub domain
        option = new Option("s", "sub-domain", true, "sub domain (default: " + SUB_DOMAIN + ")");
        option.setArgName("subDomain");
        options.addOption(option);

        // Port
        option = new Option("p", "port", true, "port (default: " + PORT + ")");
        option.setArgName("port");
        options.addOption(option);

        // Secure key
        option = new Option("k", "key", true, "secure key to pair mobile device");
        option.setArgName("secureKey");
        option.setRequired(true);
        options.addOption(option);

        // Mobile device name
        option = new Option("d", "device", true, "name of mobile device you want to pair");
        option.setArgName("mobileDevice");
        option.setRequired(true);
        options.addOption(option);

        return options;
    }
}
