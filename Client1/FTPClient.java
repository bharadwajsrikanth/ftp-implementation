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
            long filesize = in.readLong();
            if(filesize == -1) {
                System.out.println("File " + name + " is unavailable on the server");
                return;
            }
            System.out.println("Downloading file: " + name);
            System.out.println("Size: " + filesize);
            FileOutputStream fos = new FileOutputStream(name);
            fos.flush();
            byte[] bytes = new byte[8192];
            int count;
            while (filesize>0 && (count = in.read(bytes,0,(int)Math.min(bytes.length,filesize))) != -1) {
                fos.write(bytes, 0, count);
                filesize -= count;
            }
            System.out.println("Downloaded file: " + name);
            fos.close();
        }
        catch(FileNotFoundException e) {
            System.out.println("Error occurred while storing the file.");
        }
        catch(IOException e){
            System.out.println("Exception while taking inputs/writing file");
        }
    }

    private void uploadFile(String name) {
        System.out.println("Uploading file: " + name);
        File file = new File(name);
        long length = file.length();

        byte bytes[] = new byte[8192];
        try {
            System.out.println("size: "+length);
            out.flush();
            out.writeLong(length);
            InputStream filein = new FileInputStream(file);
            int count;
            while ((count = filein.read(bytes)) > 0) {
                out.write(bytes, 0, count);
            }
            out.flush();
        }
        catch(FileNotFoundException e) {
            System.out.println("File not found on the server.");
        }
        catch(IOException e) {
            System.out.println("Exception while taking inputs/reading file");
        }
        System.out.println("Uploaded file: " + name);
    }

    private void run() {
        String command = "";
        String operation = "";
        String host;
        StringTokenizer st;
        int port;
        bufferedReader = new BufferedReader(new InputStreamReader(System.in));
        try {
            System.out.println("Enter \"ftpclient <host> <port>\" to connect to the required host");
            command = bufferedReader.readLine();
            st = new StringTokenizer(command);
            operation = st.nextToken();
            host = st.nextToken();
            port = Integer.parseInt(st.nextToken());
            socket = new Socket(host, port);
            System.out.println("Connected");
            out = new DataOutputStream(new BufferedOutputStream(socket.getOutputStream()));
            out.flush();
            in = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
            //bufferedReader = new BufferedReader(new InputStreamReader(System.in));
            getAuthCredentials(bufferedReader);
            in_obj = new ObjectInputStream(socket.getInputStream());
            auth_msg = (String) in_obj.readObject();
            System.out.println(auth_msg);

            if (auth_msg.charAt(0) == 'W') {
                boolean exec = true;
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
                        case 'u':
                        case 'U':
                            uploadFile(st.nextToken());
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
            System.out.println("Host unknown, please enter correct host");
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