import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class FileServer {
    public static void main(String[] args) {
        try {
            ServerSocket serverSocket = new ServerSocket(5555); // Choose a port number

            System.out.println("Server waiting for client on port " + serverSocket.getLocalPort());

            while (true) {
                Socket socket = serverSocket.accept();

                System.out.println("Just connected to " + socket.getRemoteSocketAddress());

                // Receive file
                receiveFile(socket);

                socket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void receiveFile(Socket socket) throws IOException {
        DataInputStream dis = new DataInputStream(socket.getInputStream());

        String fileName = dis.readUTF();
        long fileSize = dis.readLong();

        FileOutputStream fos = new FileOutputStream("received_" + fileName);
        BufferedOutputStream bos = new BufferedOutputStream(fos);

        byte[] buffer = new byte[1024];
        int bytesRead;

        System.out.println("Receiving file...");

        while ((bytesRead = dis.read(buffer, 0, buffer.length)) != -1) {
            bos.write(buffer, 0, bytesRead);
        }

        bos.close();
        System.out.println("File received successfully!");
    }
}
