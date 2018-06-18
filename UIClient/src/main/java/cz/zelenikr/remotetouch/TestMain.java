package cz.zelenikr.remotetouch;

import com.google.gson.Gson;
import cz.zelenikr.remotetouch.data.dto.CallType;
import cz.zelenikr.remotetouch.data.dto.event.*;
import cz.zelenikr.remotetouch.data.dto.message.MessageContent;
import cz.zelenikr.remotetouch.data.dto.message.MessageDTO;
import cz.zelenikr.remotetouch.manager.ConnectionManager;
import cz.zelenikr.remotetouch.network.SocketIOClient;
import cz.zelenikr.remotetouch.security.AESCipher;
import cz.zelenikr.remotetouch.security.Hash;
import cz.zelenikr.remotetouch.security.SHAHash;
import cz.zelenikr.remotetouch.security.SymmetricCipher;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.Scanner;
import java.util.logging.Logger;

/**
 * @author Roman Zelenik
 */
public class TestMain {

    private static Logger logger = Logger.getLogger("Main");

    public static int
//            PORT = 443;
            PORT = 8080;
    static String HOSTNAME = "http://localhost";
    //    public static String HOSTNAME = "https://remotetouch.tk";
    //        static String SUB_DOMAIN = "";
    private static String SUB_DOMAIN = "/socket";
    public static String
            SECURE_KEY = "",
            DEVICE = "";


    public static void main(String[] args) throws URISyntaxException {
//        run();

        test();
    }

    private static void test() {
//        urlTest();
//        cipherTest();
//        jsonTest();
//        dateTimeTest();
    }

    private static void urlTest() {
        try {
//            URL url = new URL("https://remotetouch.tk/socket");
            URL url = new URL("http://localhost:8080/socket");
            System.out.println("URL = " + url.toExternalForm());
            URI uri = url.toURI();
            System.out.println("URI = " + uri.toString());
        } catch (MalformedURLException | URISyntaxException e) {
            e.printStackTrace();
        }
    }

    private static void cipherTest() {
        String aesKey = AESCipher.generatePlainAESKey();
//        aesKey = "[B@6e3c1e69";
//        aesKey = "a23b23c";
//        String message = "{'a':'Hello', 'b':'world', 'c':01210}";
        System.out.println("Key: " + aesKey);

        SymmetricCipher<String> symmetricCipher = new AESCipher(aesKey);

//        String message = "{'a':'Hello', 'b':'world', 'c':01210, 'd':{'a':'Hello', 'b':'world', 'c':01210}}";
        String message = "{'a':'Příliš žluťoučký kůň', 'b':'pěl ďábelské ódy.', 'c':01210, 'd':{'a':':-)', 'b':'world', 'c':01210}}";
        System.out.println("Plain message: " + message);

        String encryptedMessage = symmetricCipher.encrypt(message);
        System.out.println("Encrypted message: \n" + encryptedMessage);

//        aesKey = "[B@6e3c1e69";
//        symmetricCipher.changeKey(aesKey);

        String decryptedMessage = symmetricCipher.decrypt(encryptedMessage);
        System.out.println("Decrypted message: " + decryptedMessage);

        boolean equal = message.equals(decryptedMessage);
        System.out.println("Equal messages: " + equal);

        /*for (int i = 0; i < 20; i++) {
            byte[] key  = CipherHelper.AESCipher.generateAESKey();
            System.out.println(key+" ["+key.length+"]");
        }*/
    }

    private static void jsonTest() {
        //TODO: test MultipleMessageContent
        Gson prettyGson = new Gson();
        prettyGson = prettyGson.newBuilder().setPrettyPrinting().create();

        MessageDTO messageDTO = new MessageDTO("asdf789", new EventDTO(EventType.CALL,
                new CallEventContent("Jane Doe", "+420 777 666 555", CallType.INCOMING, System.currentTimeMillis() - 60 * 5000)));
//        System.out.println(gson.toJson(messageDTO));

        MessageContent[] contents = {
                new EventDTO(EventType.CALL, new CallEventContent("John Doe", "666 666 666", CallType.MISSED, System.currentTimeMillis() - 60 * 10000)),
                new EventDTO(EventType.NOTIFICATION, new NotificationEventContent("com.fake.app", "Mock application", "Mock app title", "lorem ipsum etcetera", System.currentTimeMillis() - 2000 * 60)),
                new EventDTO(EventType.SMS, new SmsEventContent("John Doe", "666 666 666", "Sorry, i'm late.", System.currentTimeMillis() - 60 * 5000))
        };
        messageDTO = new MessageDTO("qwert12345", contents);
//        System.out.println(gson.toJson(messageDTO));
//        System.out.println(gson.toJson(messageDTO.getContent()));

        try {
            SocketIOClient client = new SocketIOClient("token", new URI("http://localhost"), "key");
            client.setOnSMSReceived(contents1 -> System.out.println(Arrays.toString(contents1)));
            client.setOnNotificationReceived(contents1 -> System.out.println(Arrays.toString(contents1)));
            client.setOnCallReceived(contents1 -> System.out.println(Arrays.toString(contents1)));

            String strContent = new Gson().toJson(messageDTO.getContent());
//            System.out.println("Plain content: "+strContent);
            AESCipher cipher = new AESCipher("key");
            strContent = cipher.encrypt(strContent);
//            System.out.println("Encrypted content: "+strContent);

            messageDTO = new MessageDTO(messageDTO.getId(), strContent);
            String messageJson = new Gson().toJson(messageDTO);
//            System.out.println("Message JSON: "+messageJson);
//            client.onEventReceived(new JSONObject(messageJson));
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

    }

    private static void dateTimeTest() {
        Calendar today = Calendar.getInstance();
        today.set(Calendar.HOUR_OF_DAY, 0);
        today.set(Calendar.MINUTE, 0);
        today.set(Calendar.SECOND, 0);

        Date date = today.getTime();
        System.out.println(date);
        System.out.println("after " + new Date() + " :" + date.after(new Date()));
        System.out.println("after " + new Date(today.getTimeInMillis() - 8000 * 3600) + " :" + date.after(new Date(today.getTimeInMillis() - 8000 * 3600)));

    }

    private static void run() {

        ConnectionManager connectionManager = ConnectionManager.getInstance();
        connectionManager.registerConnectionStateChangedListener(status -> {
            System.out.println(status);
        });
        connectionManager.connect();
        System.out.println("Stop client by pressing any character key and enter...");
        new Scanner(System.in).hasNext();

        System.out.println("Stopping...");
        connectionManager.disconnect();
    }

    public static URI getServerURI() throws URISyntaxException {
//        URI uri = new URI(HOSTNAME );
//        URI uri = new URI("http://remote-touch.azurewebsites.net");

        return new URI(HOSTNAME + ":" + PORT + SUB_DOMAIN);
    }

    public static String createToken(String device, String pairKey) {
        Hash hash = new SHAHash();
        return hash.hash(device + pairKey);
    }
}
