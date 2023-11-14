import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class FileServerGUI extends JFrame {
    private ServerSocket serverSocket;
    private boolean isServerRunning = false;
    private JButton startButton;
    private JButton stopButton;

    public FileServerGUI() {
        initUI();
    }

    private void initUI() {
        setTitle("File Server");
        setSize(300, 200);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        startButton = new JButton("Start Server");
        stopButton = new JButton("Stop Server");

        startButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!isServerRunning) {
                    startServer();
                }
            }
        });

        stopButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (isServerRunning) {
                    stopServer();
                }
            }
        });

        JPanel panel = new JPanel();
        panel.setLayout(new FlowLayout());
        panel.add(startButton);
        panel.add(stopButton);

        add(panel);

        setLocationRelativeTo(null); // Center the frame on the screen
    }

    private void startServer() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    serverSocket = new ServerSocket(5555); // Choose a port number
                    isServerRunning = true;
                    updateUI();

                    while (isServerRunning) {
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
        }).start();
    }

    private void stopServer() {
        try {
            serverSocket.close();
            isServerRunning = false;
            updateUI();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void receiveFile(Socket socket) throws IOException {
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

        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                JOptionPane.showMessageDialog(FileServerGUI.this, "File received successfully!");
            }
        });
    }

    private void updateUI() {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                startButton.setEnabled(!isServerRunning);
                stopButton.setEnabled(isServerRunning);
            }
        });
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                FileServerGUI serverGUI = new FileServerGUI();
                serverGUI.setVisible(true);
            }
        });
    }
}
