import java.io.*;
import java.net.*;

public class FTPClient {

    private int SERVER_PORT = 5000;
    private String SERVER_PROXY = "localhost";

    private Socket socket = null;
    private DataOutputStream out;
    private DataInputStream in;

    public FTPClient() {}

    private void run() {
        try {
            socket = new Socket(SERVER_PROXY, SERVER_PORT);
            System.out.println("Connected");
            out = new DataOutputStream(new BufferedOutputStream(socket.getOutputStream()));
            out.flush();
            in = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
        }
        catch(UnknownHostException u) {
            System.out.println(u);
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