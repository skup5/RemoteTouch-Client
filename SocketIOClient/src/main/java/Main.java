
import org.apache.commons.cli.*;
import security.AESCipher;
import security.SymmetricCipher;

import java.net.URI;
import java.net.URISyntaxException;
import java.security.NoSuchAlgorithmException;
import java.util.Scanner;
import java.util.logging.Logger;

/**
 * @author Roman Zelenik
 */
public class Main {

    private static Logger logger = Logger.getLogger("Main");
    private static int CLIENT_COUNT = 1, CLIENT_FIRST_ID = 1, PORT = 80;
    //    static String HOSTNAME = "http://localhost";
    private static String HOSTNAME = "http://remote-touch.azurewebsites.net";
    //    static String SUB_DOMAIN = "";
    private static String SUB_DOMAIN = "/socket";
    private static String SECURE_KEY = "[B@6e3c1e69";

    public static final boolean LOGGER_USE_FILE_HANDLER = false;


    public static void main(String[] args) throws URISyntaxException, NoSuchAlgorithmException {
        Options options = setOptions();

        try {
            processArgs(options, args);
        } catch (ParseException e) {
            System.out.println(e.getMessage());
            printHelp(options);
            System.exit(1);
        }

        run();

        //cipherTest();
    }

    private static void cipherTest() throws NoSuchAlgorithmException {
        String aesKey = AESCipher.generatePlainAESKey();
        aesKey = "[B@6e3c1e69";
//        aesKey = "a23b23c";
//        String message = "{'a':'Hello', 'b':'world', 'c':01210}";
        System.out.println("Key: " + aesKey);

        SymmetricCipher symmetricCipher = new AESCipher(aesKey);

        String message = "{'a':'Hello', 'b':'world', 'c':01210, 'd':{'a':'Hello', 'b':'world', 'c':01210}}";
        System.out.println("Plain message: " + message);

        String encryptedMessage = symmetricCipher.encrypt(message);
        System.out.println("Encrypted message: \n" + encryptedMessage);

        String decryptedMessage = symmetricCipher.decrypt(encryptedMessage);
        System.out.println("Decrypted message: " + decryptedMessage);

        boolean equal = message.equals(decryptedMessage);
        System.out.println("Equal messages: " + equal);

        /*for (int i = 0; i < 20; i++) {
            byte[] key  = CipherHelper.AESCipher.generateAESKey();
            System.out.println(key+" ["+key.length+"]");
        }*/
    }

    private static void run() throws URISyntaxException, NoSuchAlgorithmException {

        URI uri = new URI(HOSTNAME + ":" + PORT + SUB_DOMAIN);
//        URI uri = new URI(HOSTNAME );
//        URI uri = new URI("http://remote-touch.azurewebsites.net");

        SocketIOClient[] clients = new SocketIOClient[CLIENT_COUNT];

        for (int i = 0; i < CLIENT_COUNT; i++) {
            clients[i] = new SocketIOClient(i + CLIENT_FIRST_ID, uri, SECURE_KEY);
            clients[i].run();
        }

        System.out.println("Stop clients by pressing any character key and enter...");
        new Scanner(System.in).hasNext();

        System.out.println("Stopping...");
        for (int i = 0; i < CLIENT_COUNT; i++) {
            clients[i].close();
        }
    }

    private static void printHelp(Options options) {
        HelpFormatter formatter = new HelpFormatter();
        formatter.printHelp(120, "java -jar socket-io-client.jar", "\nDESCRIPTION", options, null, true);
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

        // Parse number of clients
        if (cmd.hasOption("n")) {
            String value = cmd.getOptionValue("n");
            try {
                CLIENT_COUNT = Integer.parseInt(value);

                if (CLIENT_COUNT < 1) {
                    throw new NumberFormatException();
                }
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("Number of clients has to be greater then 0, '" + value + "' given.");
            }
        }

        // Parse id of first client
        if (cmd.hasOption("i")) {
            String value = cmd.getOptionValue("i");

            try {
                CLIENT_FIRST_ID = Integer.parseInt(value);

                if (CLIENT_FIRST_ID < 0) {
                    throw new NumberFormatException();
                }
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("Client ID has to start from zero or positive integer, '" + value + "' given\n");
            }
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

    }

    private static Options setOptions() {
        Options options = new Options();
        Option option;

        // Help
        option = new Option("h", "help", false, "display help");
        options.addOption(option);

        // Number of running clients
        option = new Option("n", true, "number of running clients (default: " + CLIENT_COUNT + ")");
        option.setArgName("n");
        options.addOption(option);

        // First client ID
        option = new Option("i", "first-id", true, "id of first client (default: " + CLIENT_FIRST_ID + ")");
        option.setArgName("first-id");
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

        return options;
    }
}
