import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.net.Socket;

public class Client {

    public static String convertStringToBinary(String text) {
        StringBuilder binaryString = new StringBuilder();
        for (char c : text.toCharArray()) {
            String binaryChar = String.format("%8s", Integer.toBinaryString(c)).replace(' ', '0');
            binaryString.append(binaryChar);
        }
        return binaryString.toString();
    }

    public static String convertBinaryToString(String binaryString) {
        StringBuilder textString = new StringBuilder();

        for (int i = 0; i < binaryString.length(); i += 8) {
            String byteString = binaryString.substring(i, i + 8);
            char c = (char) Integer.parseInt(byteString, 2);
            textString.append(c);
        }

        return textString.toString();
    }

    public static void main(String[] args) {
        Socket s = null;
        DataOutputStream output = null;
        DataInputStream input = null;
        BufferedReader fileReader = null;

        try {
            s = new Socket("localhost", 5000);
            System.out.println("Client Connected at server Handshaking port " + s.getPort());
            System.out.println("Client’s communication port: " + s.getLocalPort());
            System.out.println("Client is Connected");

            output = new DataOutputStream(s.getOutputStream());
            input = new DataInputStream(s.getInputStream());

            fileReader = new BufferedReader(new FileReader("input.txt"));
            String line;
            while ((line = fileReader.readLine()) != null) {
                System.out.println("Read from file (Text): " + line);

                String binaryStr = convertStringToBinary(line);
                System.out.println("Converted to Binary: " + binaryStr);

                output.writeUTF(binaryStr);
                System.out.println("Sent to server (Binary): " + binaryStr);

                String response = input.readUTF();
                System.out.println("Received from server (De-stuffed): " + response);

                String receivedText = convertBinaryToString(response);
                System.out.println("Converted back to original text: " + receivedText);
            }

            output.writeUTF("stop");

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (output != null) output.close();
                if (input != null) input.close();
                if (fileReader != null) fileReader.close();
                if (s != null) s.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
