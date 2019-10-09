import java.io.*;
import java.net.*;

public class FTPClient {

    private int SERVER_PORT = 5000;
    private String SERVER_PROXY = "localhost";

    private Socket socket = null;
    private BufferedReader bufferedReader;
    private DataOutputStream out;
    private DataInputStream in;
    private ObjectInputStream in_obj;
    private String auth_msg = "";

    public FTPClient() {}

    private void sendMessage(String msg) {
        try {
            out.writeUTF(msg);
            out.flush();
        }
        catch(IOException i) {
            System.out.println(i);
        }
    }

    private void getAuthCredentials(BufferedReader bufferedReader) {
        String server_response = "";
        String username, password;
        try {
            System.out.print("Enter username: ");
            username = bufferedReader.readLine();
            sendMessage(username);
            System.out.print("Enter password: ");
            password = bufferedReader.readLine();
            sendMessage(password);
        }
        catch(IOException i) {
            System.out.println(i);
        }
    }

    private void run() {
        try {
            socket = new Socket(SERVER_PROXY, SERVER_PORT);
            System.out.println("Connected");
            out = new DataOutputStream(new BufferedOutputStream(socket.getOutputStream()));
            out.flush();
            in = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
            bufferedReader = new BufferedReader(new InputStreamReader(System.in));
            getAuthCredentials(bufferedReader);
            in_obj = new ObjectInputStream(socket.getInputStream());
            auth_msg = (String) in_obj.readObject();
            System.out.println(auth_msg);
        }
        catch(UnknownHostException u) {
            System.out.println(u);
        }
        catch(ClassNotFoundException ex) {
            System.out.println(ex);
        }
        catch(IOException i) {
            System.out.println(i);
        }
        finally {
            System.out.println("Closing connection");
            try {
                in.close();
                out.close();
                socket.close();
            }
            catch(IOException ex) {
                System.out.println(ex);
            }
        }
    }

    public static void main(String args[]) {
        FTPClient cli = new FTPClient();
        cli.run();
    }
}