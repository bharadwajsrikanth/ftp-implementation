# ftp-implementation
A simple version of FTP client/server software implementation. It consists of two programs: ftpclient and ftpserver. First, the ftpserver is started on a computer. It listens on a certain TCP port. Then, the ftpclient is executed on the same or a different computer; the server’s address and port number are supplied in the command line, for example, “ftpclient sand.cise.ufl.edu 5106”. The client will prompt for username and password. After logon, the user can issue three commands at the client side: “dir” is to retrieve the list of file names available at the server, “get <filename>” is to retrieve a file from the server, and “upload <filename>” is to upload a file to the server.

## Compilation
1. Compile the server using 
***javac FTPServer.java***

2. Complie the clients using
***javac FTPClient.java***

## Execution
1. Start the server using
***java FTPServer***

This starts the server which runs on **port 5000**.

2. Start the client using
***java FTPClient***

## Operation
1. Establish the connection with server using command
***ftpclient <host> <port>***

2. Login using following credentials:
username: ***gator***
password: ***password***

3. Run command
***dir***
to retrieve the list of file names available at the server

4. Run command
***get <filename>***
to retrieve a file from the server

5. Run command
***upload <filename>***
to upload a file to the server

6. Run command
***exit***
to close client-server connection
