import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class FTPServer {

    private String USERNAME = "gator";
    private String PASSWORD = "password";

    private int SERVER_PORT = 5000;

    private ServerSocket server = null;
    private Socket socket = null;
    private DataOutputStream out;
    private DataInputStream in;

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
            ObjectOutputStream out_obj = new ObjectOutputStream(socket.getOutputStream());;
            out_obj.flush();
            out_obj.writeObject(msg);
            out_obj.flush();
            System.out.println("Sending message to client");
        }
        catch(IOException i){
            System.out.println(i);
        }
    }

    private void performAuthentication() {
        String username, password, auth_message;
        try {
            username = (String) in.readUTF();
            password = (String) in.readUTF();
            if(username.equals(getUsername()) && password.equals(getPassword())){
                System.out.println("Authentication successful");
                auth_message = "Welcome, " + username;
            }else{
                System.out.println("Authentication failed");
                auth_message = "Login Failed";
            }
            sendMessage(auth_message);
        }
        catch(IOException i){
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
            performAuthentication();
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