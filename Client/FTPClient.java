import java.io.*;
import java.net.*;
import java.util.StringTokenizer;

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

    private void receiveFile(String name) {
        //System.out.println("File name: " + name);
        try {
            FileOutputStream fos = new FileOutputStream(name);
            long filesize = in.readLong();
            fos.flush();
            byte[] bytes = new byte[8192];
            int count;
            while (filesize>0 && (count = in.read(bytes,0,(int)Math.min(bytes.length,filesize))) != -1) {
                fos.write(bytes, 0, count);
                filesize -= count;
            }
            fos.close();
        }
        catch(FileNotFoundException e) {
            System.out.println("Error occurred while storing the file.");
        }
        catch(IOException e){
            System.out.println("Exception while taking inputs/writing file");
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

            if (auth_msg.charAt(0) == 'W') {
                boolean exec = true;
                String command = "";
                String operation = "";
                StringTokenizer st;
                while (exec) {
                    System.out.print("Enter command: ");
                    command = bufferedReader.readLine();
                    st = new StringTokenizer(command);
                    sendMessage(command);
                    operation = st.nextToken();
                    switch (operation.charAt(0)) {
                        case 'g':
                        case 'G':
                            receiveFile(st.nextToken());
                            break;
                        case 'd':
                        case 'D':
                            String files = (String) in_obj.readObject();
                            System.out.println("Files available on server: ");
                            System.out.println(files);
                            break;
                        case 'e':
                        case 'E':
                            System.out.println("Exiting program");
                            exec = false;
                            break;
                        default:
                            System.out.println("Operation not implemented");
                    }
                }
            }
        }
        catch(UnknownHostException u) {
            System.out.println(u);
        }
        catch(ClassNotFoundException ex) {
            System.out.println(ex);
        }
        catch(IOException i) {
            i.printStackTrace();
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