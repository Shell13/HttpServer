import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Date;

public class HttpServer {

    public static void main(String[] args) throws IOException {
        ServerSocket serverSocket = new ServerSocket(8080);
        while (true){
            Socket socket = serverSocket.accept();
            System.err.println("Client accepted");
            new Thread(new SocketProcessor(socket)).start();
        }
    }

    private static class SocketProcessor implements Runnable{

        private Socket socket;
        private InputStream inputStream;
        private OutputStream outputStream;

        private SocketProcessor(Socket socket) throws IOException {
            this.socket = socket;
            this.inputStream = socket.getInputStream();
            this.outputStream = socket.getOutputStream();
        }

        @Override
        public void run() {
            try {
                readInputHeaders();
                String response = "Hello from tomorrow\r\n" + new Date().toString();
                writeResponse(response);
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            System.err.println("Client processing finished");
        }

        private void writeResponse(String s) throws IOException {
            String response = "HTTP/1.1 200 OK\r\nServer: myServer/"+new Date()+"\r\n" +
                    "Content-Type: text/html/\r\nContent-Length: " + s.length() + "\r\n" +
                    "Connection: close\r\n\r\n";
            String result = response + s;
            outputStream.write(result.getBytes());
            outputStream.flush();
        }

        private void readInputHeaders() throws IOException {
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            while (true){
                String s = reader.readLine();
                if (s == null || s.trim().length() == 0){
                    break;
                }
            }
        }
    }
}
