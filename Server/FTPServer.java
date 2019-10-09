import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class FTPServer {

    private int SERVER_PORT = 5000;

    private ServerSocket server = null;
    private Socket socket = null;
    private DataOutputStream out;
    private DataInputStream in;

    public FTPServer() {}

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
        }
        catch(IOException ex) {
            System.out.println(ex);
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