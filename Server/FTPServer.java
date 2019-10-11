import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.StringTokenizer;
import java.util.regex.Pattern;

public class FTPServer {

    private String USERNAME = "gator";
    private String PASSWORD = "password";

    private int SERVER_PORT = 5000;

    private ServerSocket server = null;
    private Socket socket = null;
    private DataOutputStream out;
    private DataInputStream in;
    private ObjectOutputStream out_obj;

    public FTPServer() {}

    public String getUsername() {
        return this.USERNAME;
    }

    public String getPassword() {
        return this.PASSWORD;
    }

    void sendMessage(String msg)
    {
        try{
            //ObjectOutputStream out_obj = new ObjectOutputStream(socket.getOutputStream());
            out_obj.flush();
            out_obj.writeObject(msg);
            out_obj.flush();
        }
        catch(IOException i){
            System.out.println(i);
        }
    }

    private void sendFile(String name) {
        File file = new File(name);
        long length = file.length();
        InputStream filein;
        byte bytearray[] = new byte[8192];

        try {
            System.out.println("Sending file " + name + " to client");
            out.flush();
            out.writeLong(length);
            filein = new FileInputStream(file);
            int count;
            while ((count = filein.read(bytearray)) > 0) {
                out.write(bytearray, 0, count);
            }
            out.flush();
        }
        catch(FileNotFoundException e) {
            System.out.println("File not found on the server.");
        }
        catch(IOException e){
            System.out.println("Exception while taking inputs/reading file");
        }
    }

    String getFiles(File directory){
        StringBuilder files = new StringBuilder("");
        for(File fe: directory.listFiles()){
            //if(!Pattern.matches("*class", fe.getName()) && !Pattern.matches("*java", fe.getName())) {
                files.append("\t"+fe.getName()+"\n");
                //files.append("\n");
            //}
        }
        return files.toString();
    }

    private void performAuthentication() {
        String username, password, auth_message;
        try {
            username = (String) in.readUTF();
            password = (String) in.readUTF();
            if(username.equals(getUsername()) && password.equals(getPassword())) {
                System.out.println("Authentication successful, sending message to client");
                auth_message = "Welcome, " + username;
                sendMessage(auth_message);
            }
            else {
                System.out.println("Authentication failed");
                auth_message = "Login Failed";
                sendMessage(auth_message);
            }

        }
        catch(IOException i) {
            System.out.println(i);
        }
    }

    private void performOperation() {
        String command, operation, filename;
        StringTokenizer st;
        try {
            while (true) {
                command = (String) in.readUTF();
                st = new StringTokenizer(command);
                operation = st.nextToken();
                System.out.println("Requested operation: " + operation);
                switch (operation.charAt(0)) {
                    case 'g':
                    case 'G':
                        filename = st.nextToken();
                        sendFile(filename);
                        break;
                    case 'd':
                    case 'D':
                        File cur_directory = new File(".");
                        String files = getFiles(cur_directory);
                        System.out.println("Sending file names to client");
                        sendMessage(files);
                        break;
                    default:
                        System.out.println("Invalid Input");
                }
            }
        }
        catch(IOException i) {
            System.out.println(i);
        }
    }

    private void run() {
        try {
            server = new ServerSocket(SERVER_PORT);
            System.out.println("Server started");
        }
        catch(IOException ex) {
            System.out.println(ex);
        }
        System.out.println("Waiting for a client ...");
        try {
            socket = server.accept();
            System.out.println("Client accepted");
            out = new DataOutputStream(
                    new BufferedOutputStream(socket.getOutputStream())
            );
            out.flush();
            in = new DataInputStream(
                    new BufferedInputStream(socket.getInputStream())
            );
            out_obj = new ObjectOutputStream(socket.getOutputStream());
            performAuthentication();
            performOperation();
        }
        catch(IOException i) {
            System.out.println(i);
        }
        finally {
            System.out.println("Closing connection");
            try {
                in.close();
                out.close();
                server.close();
            }
            catch(IOException ex) {
                System.out.println(ex);
            }
        }
    }

    public static void main(String args[]) {
        FTPServer serv = new FTPServer();
        serv.run();
    }
}