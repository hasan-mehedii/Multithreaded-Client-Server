import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {

    public static String bitDeStuffing(String stuffedString) {
        StringBuilder destuffed = new StringBuilder();
        int count = 0;

        for (int i = 0; i < stuffedString.length(); i++) {
            char bit = stuffedString.charAt(i);

            if (bit == '1') {
                count++;
                destuffed.append(bit);
            } else if (bit == '0') {
                if (count == 5) {
                    // Skip the stuffed '0' bit
                    count = 0;
                    continue;
                } else {
                    destuffed.append(bit);
                    count = 0;
                }
            }
        }
        return destuffed.toString();
    }

    // ClientHandler class to handle communication with one client
    static class ClientHandler extends Thread {
        private Socket clientSocket;
        private DataInputStream input;
        private DataOutputStream output;

        public ClientHandler(Socket socket) {
            this.clientSocket = socket;
            try {
                input = new DataInputStream(clientSocket.getInputStream());
                output = new DataOutputStream(clientSocket.getOutputStream());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void run() {
            String str;
            try {
                System.out.println("Connected to client at port: " + clientSocket.getPort());

                while (true) {
                    str = input.readUTF();
                    if (str.equalsIgnoreCase("stop")) {
                        System.out.println("Client at port " + clientSocket.getPort() + " disconnected.");
                        break;
                    }

                    System.out.println("Received stuffed string from client " + clientSocket.getPort() + ": " + str);

                    String destuffedString = bitDeStuffing(str);
                    System.out.println("De-stuffed string: " + destuffedString);

                    output.writeUTF(destuffedString);
                }

            } catch (IOException e) {
                System.out.println("Connection with client at port " + clientSocket.getPort() + " lost.");
            } finally {
                try {
                    if (input != null) input.close();
                    if (output != null) output.close();
                    if (clientSocket != null) clientSocket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static void main(String[] args) {
        ServerSocket serverSocket = null;

        try {
            serverSocket = new ServerSocket(5000);
            System.out.println("Server is running on port: " + serverSocket.getLocalPort());

            while (true) {
                System.out.println("Waiting for new client...");
                Socket clientSocket = serverSocket.accept();

                // Create a new thread for each connected client (multiplexing)
                ClientHandler clientThread = new ClientHandler(clientSocket);
                clientThread.start();
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (serverSocket != null) {
                try {
                    serverSocket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
