
import org.apache.commons.cli.*;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Scanner;
import java.util.logging.Logger;

/**
 * @author Roman Zelenik
 */
public class Main {

    static Logger logger = Logger.getLogger("Main");
    static int CLIENT_COUNT = 1, CLIENT_FIRST_ID = 1, PORT = 80;
//    static String HOSTNAME = "http://localhost";
    static String HOSTNAME = "http://remote-touch.azurewebsites.net";
//    static String SUB_DOMAIN = "";
    static String SUB_DOMAIN = "/socket";
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
    }

    private static void run() throws URISyntaxException {

        URI uri = new URI(HOSTNAME + ":" + PORT+SUB_DOMAIN);
//        URI uri = new URI(HOSTNAME );
//        URI uri = new URI("http://remote-touch.azurewebsites.net");

        SocketIOClient[] clients = new SocketIOClient[CLIENT_COUNT];

        for (int i = 0; i < CLIENT_COUNT; i++) {
            clients[i] = new SocketIOClient(i + CLIENT_FIRST_ID, uri);
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
