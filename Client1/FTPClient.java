import java.io.*;
import java.net.*;
import java.util.StringTokenizer;

public class FTPClient {

//    private int SERVER_PORT = 5000;
//    private String SERVER_PROXY = "localhost";

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
        try {
            long filesize = in.readLong();
            if(filesize == -1) {
                System.out.println("File " + name + " is unavailable on the server");
                return;
            }
            System.out.println("Downloading file: " + name);
            System.out.println("Size: " + filesize + " bytes");
            FileOutputStream fos = new FileOutputStream(name);
            fos.flush();
            byte[] bytes = new byte[8192*2];
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
        if(length == 0) {
            System.out.println("File " + name + " is unavailable");
            return;
        }
        byte bytes[] = new byte[8192*2];
        try {
            System.out.println("Size: " + length + " bytes");
            out.flush();
            out.writeLong(length);
            InputStream filein = new FileInputStream(file);
            int count;
            while ((count = filein.read(bytes)) > 0) {
                out.write(bytes, 0, count);
            }
            out.flush();
            System.out.println("Uploaded file: " + name);
        }
        catch(FileNotFoundException e) {
            System.out.println(e);
        }
        catch(IOException e) {
            System.out.println("Exception while taking inputs/reading file");
        }

    }

    private void run() {
        boolean conn_established = false;
        String command = "";
        String operation = "";
        String host;
        StringTokenizer st;
        int port;
        bufferedReader = new BufferedReader(new InputStreamReader(System.in));
        System.out.println("\tEnter \"ftpclient <host> <port>\" to connect to the required host");
        while(!conn_established) {
            try {
                command = bufferedReader.readLine();
                st = new StringTokenizer(command);
                operation = st.nextToken();
                operation = operation.toLowerCase();
                if(!operation.equals("ftpclient")){
                    System.out.println("Connection refused, please use \"ftpclient\"");
                    continue;
                }
                host = st.nextToken();
                port = Integer.parseInt(st.nextToken());
                socket = new Socket(host, port);
                System.out.println("Connected to the server");
                conn_established = true;
            }
            catch (UnknownHostException u) {
                System.out.println("Host unknown, please enter correct host");
            }
            catch (ConnectException e) {
                System.out.println("Connection refused, please try again with correct hostname and port number");
            }
            catch(IOException i) {
                System.out.println(i);
            }
        }

        try {
            out = new DataOutputStream(new BufferedOutputStream(socket.getOutputStream()));
            out.flush();
            in = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
            in_obj = new ObjectInputStream(socket.getInputStream());
        }
        catch(IOException i) {
            i.printStackTrace();
        }

        boolean user_authenticated = false;
        while(!user_authenticated) {
            try {
                getAuthCredentials(bufferedReader);
                auth_msg = (String) in_obj.readObject();
                System.out.println(auth_msg);
                if (auth_msg.charAt(0) == 'W')
                    user_authenticated = true;
            }
            catch (IOException i) {
                i.printStackTrace();
            }
            catch (ClassNotFoundException ex) {
                System.out.println(ex);
            }
        }
        try {
            if (auth_msg.charAt(0) == 'W') {
                boolean exec = true;
                while (exec) {
                    System.out.print("Enter command: ");
                    command = bufferedReader.readLine();
                    st = new StringTokenizer(command);
                    sendMessage(command);
                    operation = st.nextToken();
                    operation = operation.toLowerCase();
                    if(operation.equals("get")) {
                        receiveFile(st.nextToken());
                    }
                    else if(operation.equals("dir")) {
                        String files = (String) in_obj.readObject();
                        System.out.println("Files available on server: ");
                        System.out.println(files);
                    }
                    else if(operation.equals("upload")) {
                        uploadFile(st.nextToken());
                    }
                    else if(operation.equals("exit")) {
                        System.out.println("Exiting program");
                        exec = false;
                    }
                    else {
                        System.out.println("Operation not implemented");
                        System.out.println("Please use one of the following:");
                        System.out.println("\tdir (list all files on server)");
                        System.out.println("\tget <filename> (download a file from the server)");
                        System.out.println("\tupload <filename> (upload a file to the server)");
                    }
                }
            }
        } catch (ClassNotFoundException ex) {
            System.out.println(ex);
        } catch (IOException i) {
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