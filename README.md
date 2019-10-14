# ftp-implementation
A simple version of FTP client/server software implementation. It consists of two programs: ftpclient and ftpserver. First, the ftpserver is started on a computer. It listens on a certain TCP port. Then, the ftpclient is executed on the same or a different computer; the server’s address and port number are supplied in the command line, for example, “ftpclient sand.cise.ufl.edu 5106”. The client will prompt for username and password. After logon, the user can issue three commands at the client side: “dir” is to retrieve the list of file names available at the server, “get \<filename\>” is to retrieve a file from the server, and “upload \<filename\>” is to upload a file to the server.

## Compilation
1. Compile the server using

```bash
javac FTPServer.java
```

2. Complie the clients using

```bash
javac FTPClient.java
```

## Execution
1. Start the server using

```bash
java FTPServer
```

This starts the server which runs on **port 5000**.

2. Start the client using

```bash
java FTPClient
```

## Operations
1. Establish the connection with server using command

```bash
ftpclient <host> <port>
```

2. Login using following credentials:

```bash
username: gator
password: password
```

3. Run the following command to retrieve the list of file names available at the server

```bash
dir
```

4. Run the following command to retrieve a file from the server

```bash
get <filename>
```

5. Run the following command to upload a file to the server

```bash
upload <filename>
```

6. Run the following command to close client-server connection

```bash
exit
```
