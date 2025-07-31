

import java.util.*;
import java.util.regex.*;
import java.io.FileWriter;
import java.io.FileReader;
import java.io.IOException;
import java.io.BufferedReader;
import javax.swing.JOptionPane;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

// USER CLASS — Handles registration information
class User {

    private String username = null;
    private String password = null;

    private String firstName, lastName;

    public User(String first, String last) {
        this.firstName = first;
        this.lastName = last;
    }

    public boolean setUsername(String u) {
        if (u.contains("_") && u.length() <= 5) {
            username = u;
            return true;
        }
        return false;
    }

    public boolean setPassword(String p) {
        boolean hasUpper = p.matches(".*[A-Z].*");
        boolean hasDigit = p.matches(".*\\d.*");
        boolean hasSpecial = p.matches(".*[!@#$%^&*()].*");
        boolean isLong = p.length() >= 8;
        if (hasUpper && hasDigit && hasSpecial && isLong) {
            password = p;
            return true;
        }
        return false;
    }

    public boolean setPhoneNumber(String num) {
        return num.matches("^\\+27\\d{9,10}$");
    }

    // Getters
    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }
}

// LOGIN CLASS — Verifies login credentials
class Login {

    private final String storedUsername;
    private final String storedPassword;

    private final String firstName;
    private final String lastName;
    private boolean isLoggedIn;

    public Login(String f, String l, String u, String p) {
        this.firstName = f;
        this.lastName = l;
        this.storedUsername = u;
        this.storedPassword = p;
    }

    public boolean loginUser(String u, String p) {
        isLoggedIn = storedUsername.equals(u) && storedPassword.equals(p);
        return isLoggedIn;
    }

    public String returnLoginStatus() {
        return isLoggedIn
                ? "Welcome " + firstName + " " + lastName + ", it is great to see you again."
                : "Username or password incorrect, please try again.";
    }

    public boolean isLoggedIn() {
        return isLoggedIn;
    }
}

// MESSAGE CLASS — Stores message details and handles validation & storage
final class Message {

    private static int messageCount = 0;
    private final String messageID, recipient, content, messageHash;

    public Message(String recipient, String content) {
        this.recipient = recipient;
        this.content = content;
        this.messageID = generateMessageID();
        this.messageHash = createMessageHash();
        messageCount++;
    }

    // Generates random 10-digit ID
    public String generateMessageID() {
        Random rand = new Random();
        StringBuilder id = new StringBuilder();
        for (int i = 0; i < 10; i++) {
            id.append(rand.nextInt(10));
        }
        return id.toString();
    }

    public boolean checkMessageID() {
        return messageID.length() <= 10;
    }

    public int checkRecipientCell() {
        if (!recipient.startsWith("+")) {
            return 0;
        }
        String digitsOnly = recipient.replaceAll("\\D", "");
        return digitsOnly.length() <= 10 ? 1 : 0;
    }

    // Generates a hash like: 00:1:HELLOBYE
    public String createMessageHash() {
        String[] words = content.trim().split("\\s+");
        String first = words.length > 0 ? words[0].toUpperCase() : "";
        String last = words.length > 1 ? words[words.length - 1].toUpperCase() : first;
        return messageID.substring(0, 2) + ":" + messageCount + ":" + first + last;
    }

    // Prompts user to send/store/disregard
    public String SentMessage(Scanner s) {
        System.out.println("1) Send Message\n2) Disregard Message\n3) Store Message");
        System.out.print("Choose an option: ");
        return s.nextLine();
    }

    // Saves message as JSON
    public void storeMessage() {
        JSONObject json = new JSONObject();
        json.put("MessageID", messageID);
        json.put("Recipient", recipient);
        json.put("Message", content);
        json.put("MessageHash", messageHash);

        try (FileWriter file = new FileWriter("messages.json", true)) {
            file.write(json.toJSONString() + "\n");
        } catch (IOException e) {
            System.out.println("An error occurred while storing the message.");
        }
    }

    public static int returnTotalMessages() {
        return messageCount;
    }

    // Display message details
    public String printMessages() {
        return "Message ID: " + messageID + "\nMessage Hash: " + messageHash
                + "\nRecipient: " + recipient + "\nMessage: " + content;
    }

    // Getters
    public String getRecipient() {
        return recipient;
    }

    public String getMessage() {
        return content;
    }

    public String getMessageHash() {
        return messageHash;
    }

    public String getMessageID() {
        return messageID;
    }
}

// MAIN CLASS — Application Logic
public class ChatAppFinal {

    public static void main(String[] args) {
        // ===== REGISTRATION =====
        try (Scanner s = new Scanner(System.in)) {
            // ===== REGISTRATION =====
            System.out.print("First name: ");
            String fname = s.nextLine();
            System.out.print("Last name: ");
            String lname = s.nextLine();
            User u = new User(fname, lname);
            while (!u.setUsername(prompt(s, "Username (_ & ≤5 chars): "))) {
            }
            while (!u.setPassword(prompt(s, "Password (8+, A-Z, #, number): "))) {
            }
            while (!u.setPhoneNumber(prompt(s, "Phone (+27...): "))) {
            }
            
            // ===== LOGIN =====
            Login login = new Login(fname, lname, u.getUsername(), u.getPassword());
            login.loginUser(prompt(s, "Login username: "), prompt(s, "Login password: "));
            System.out.println(login.returnLoginStatus());
            if (!login.isLoggedIn()) {
                return;
            }
            
            // ===== MESSAGE ARRAYS =====
            ArrayList<Message> sentMessages = new ArrayList<>();
            ArrayList<Message> disregardedMessages = new ArrayList<>();
            ArrayList<Message> storedMessages = new ArrayList<>();
            ArrayList<String> messageHashes = new ArrayList<>();
            ArrayList<String> messageIDs = new ArrayList<>();
            
            // ===== MAIN MENU LOOP =====
            boolean running = true;
            while (running) {
                System.out.println("\n1) Send Message\n2) Load Stored Messages\n3) Reports\n4) Quit");
                switch (prompt(s, "Choose: ")) {
                    case "1" -> {
                        // Send messages
                        int count = Integer.parseInt(prompt(s, "How many messages? "));
                        for (int i = 0; i < count; i++) {
                            String r = prompt(s, "Recipient: ");
                            String m = prompt(s, "Message: ");
                            if (m.length() > 250) {
                                System.out.println("Too long.");
                                continue;
                            }
                            
                            Message msg = new Message(r, m);
                            String decision = msg.SentMessage(s);
                            
                            switch (decision) {
                                case "1" -> {
                                    sentMessages.add(msg);
                                    messageHashes.add(msg.getMessageHash());
                                    messageIDs.add(msg.getMessageID());
                                    JOptionPane.showMessageDialog(null, msg.printMessages(), "Message Sent", JOptionPane.INFORMATION_MESSAGE);
                                }
                                case "2" -> disregardedMessages.add(msg);
                                default -> {
                                    msg.storeMessage();
                                    System.out.println("Message stored.");
                                }
                            }
                        }
                    }
                        
                    case "2" -> {
                        // Load messages from JSON
                        try (BufferedReader br = new BufferedReader(new FileReader("messages.json"))) {
                            String line;
                            JSONParser parser = new JSONParser();
                            while ((line = br.readLine()) != null) {
                                JSONObject obj = (JSONObject) parser.parse(line);
                                Message stored = new Message(obj.get("Recipient").toString(), obj.get("Message").toString());
                                storedMessages.add(stored);
                            }
                            System.out.println("Stored messages loaded: " + storedMessages.size());
                        } catch (IOException | ParseException e) {
                            System.out.println("Couldn't load stored messages.");
                        }
                    }
                        
                    case "3" -> // Reporting features
                        reportMenu(s, sentMessages, messageHashes, messageIDs);
                        
                    case "4" -> // Exit
                        running = false;
                        
                    default -> System.out.println("Invalid option.");
                }
            }
            
            System.out.println("App closed.");
        }
    }

    // ===== REPORT MENU =====
    public static void reportMenu(Scanner s, ArrayList<Message> messages, ArrayList<String> hashes, ArrayList<String> ids) {
        System.out.println("a) Sender/recipient\nb) Longest\nc) Search by ID\nd) Search by recipient\ne) Delete by hash\nf) Full report");
        switch (prompt(s, "Report option: ")) {
            case "a" -> {
                for (Message m : messages) {
                    System.out.println("Recipient: " + m.getRecipient());
                }
            }

            case "b" -> {
                Message longest = Collections.max(messages, Comparator.comparingInt(m -> m.getMessage().length()));
                System.out.println("Longest: " + longest.getMessage());
            }

            case "c" -> {
                String id = prompt(s, "Message ID: ");
                for (Message m : messages) {
                    if (m.getMessageID().equals(id)) {
                        System.out.println("To: " + m.getRecipient() + " — " + m.getMessage());
                    }
                }
            }

            case "d" -> {
                String rcpt = prompt(s, "Recipient to find: ");
                for (Message m : messages) {
                    if (m.getRecipient().equals(rcpt)) {
                        System.out.println("Message: " + m.getMessage());
                    }
                }
            }

            case "e" -> {
                String hash = prompt(s, "Hash to delete: ");
                messages.removeIf(m -> m.getMessageHash().equals(hash));
                System.out.println("Deleted if found.");
            }

            case "f" -> {
                for (Message m : messages) {
                    System.out.println(m.printMessages());
                }
            }
        }
    }

    // Helper input method
    private static String prompt(Scanner s, String msg) {
        System.out.print(msg);
        return s.nextLine();
    }
}
