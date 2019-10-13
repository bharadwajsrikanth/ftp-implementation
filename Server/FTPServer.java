import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.StringTokenizer;
import java.util.regex.Pattern;

public class FTPServer {

    private String USERNAME = "gator";
    private String PASSWORD = "password";

    private int SERVER_PORT = 5000;

    private static ServerSocket server = null;
    private DataOutputStream out;
    private DataInputStream in;
    private ObjectOutputStream out_obj;

    static int clientNum;

    public FTPServer() {
    }

    public static void main(String args[]) {
        System.out.println("The server is started");
        System.out.println("Waiting for a client ...");
        clientNum = 1;
        try {
            server = new ServerSocket(5000);
            while (true) {
                new Util(server.accept(), clientNum).start();
                System.out.println("Client: " + clientNum + " connected");
                clientNum++;
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                server.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }

    private static class Util extends Thread {
        private String USERNAME = "gator";
        private String PASSWORD = "password";

        private int SERVER_PORT = 5000;

        private Socket socket = null;
        private DataOutputStream out;
        private DataInputStream in;
        private ObjectOutputStream out_obj;

        private int num;

        public Util(Socket socket, int num) {
            this.socket = socket;
            this.num = num;
        }

        public String getUsername() {
            return this.USERNAME;
        }

        public String getPassword() {
            return this.PASSWORD;
        }

        void sendMessage(String msg) {
            try {
                out_obj.flush();
                out_obj.writeObject(msg);
                out_obj.flush();
            } catch (IOException i) {
                System.out.println(i);
            }
        }

        private void receiveFile(String name) {
            System.out.println("Receiving File: " + name);
            try {
                FileOutputStream fout = new FileOutputStream(name);
                long filesize = in.readLong();
                System.out.println("Filesize: " + filesize);
                fout.flush();
                byte[] bytes = new byte[8192];
                int count;
                while (filesize > 0 && (count = in.read(bytes, 0, (int) Math.min(bytes.length, filesize))) != -1) {
                    fout.write(bytes, 0, count);
                    filesize -= count;
                }
                fout.close();
            } catch (FileNotFoundException e) {
                System.out.println("Error occurred while storing the file.");
            } catch (IOException e) {
                System.out.println("Exception while taking inputs/writing file");
            }
        }

        private void sendFile(String name) {
            File file = new File(name);
            long length = file.length();
            InputStream filein;
            byte bytearray[] = new byte[8192];

            try {
                if(!file.exists()) {
                    System.out.println("File not found on the server.");
                    out.flush();
                    out.writeLong(-1);
                    out.flush();
                    return;
                }
                System.out.println("Sending file " + name + " to client");
                out.flush();
                out.writeLong(length);
                filein = new FileInputStream(file);
                int count;
                while ((count = filein.read(bytearray)) > 0) {
                    out.write(bytearray, 0, count);
                }
                out.flush();
            } catch (FileNotFoundException e) {
                System.out.println("File not found on the server.");
            } catch (IOException e) {
                System.out.println("Exception while taking inputs/reading file");
            }
        }

        String getFiles(File directory) {
            StringBuilder files = new StringBuilder("");
            for (File fe : directory.listFiles()) {
                //if(!Pattern.matches("*class", fe.getName()) && !Pattern.matches("*java", fe.getName())) {
                files.append("\t" + fe.getName() + "\n");
                //}
            }
            return files.toString();
        }

        private void performAuthentication() {
            String username, password, auth_message;
            boolean auth_success = false;
            while(!auth_success) {
                try {
                    username = (String) in.readUTF();
                    password = (String) in.readUTF();
                    if (username.equals(getUsername()) && password.equals(getPassword())) {
                        auth_success = true;
                        System.out.println("Authentication successful, sending message to client");
                        auth_message = "Welcome, " + username;
                        sendMessage(auth_message);
                    } else {
                        System.out.println("Authentication failed");
                        auth_message = "Login Failed, please try again";
                        sendMessage(auth_message);
                    }

                } catch (IOException i) {
                    System.out.println(i);
                }
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
                    if(operation.equals("get")) {
                        filename = st.nextToken();
                        sendFile(filename);
                    }
                    else if(operation.equals("dir")) {
                        File cur_directory = new File(".");
                        String files = getFiles(cur_directory);
                        System.out.println("Sending file names to client");
                        sendMessage(files);
                    }
                    else if(operation.equals("upload")) {
                        receiveFile(st.nextToken());
                    }
                    else if(operation.equals("exit")) {
                        System.out.println("Client exiting");
                    }
                    else {
                        System.out.println("Invalid Input");
                    }
                }
            } catch (IOException i) {
                System.out.println(i);
            }
        }

        public void run() {
            try {
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
            } catch (IOException i) {
                System.out.println(i);
            } finally {
                System.out.println("Closing connection");
                try {
                    in.close();
                    out.close();
                } catch (IOException ex) {
                    System.out.println(ex);
                }
            }
        }
    }
}