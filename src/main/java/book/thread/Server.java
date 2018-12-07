package book.thread;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;

/**
 * jerry
 */
public class Server implements Runnable{

    ServerSocket serverSocket;
    volatile boolean keepProcessing = true;

    public Server(int port, int millisecondsTimeout) throws IOException {
        serverSocket = new ServerSocket(port);
        serverSocket.setSoTimeout(millisecondsTimeout);
    }

    @Override
    public void run() {
        System.out.println("Server Starting\n");

        while(keepProcessing){
            try{
                System.out.printf("accepting client\n");
                Socket socket = serverSocket.accept();
                System.out.println("got client\n");
                process(socket);
            }catch (IOException e) {
                handle(e);
            }
        }
    }

    private void handle(IOException e) {
        if(!(e instanceof SocketException)){
            e.printStackTrace();
        }
    }

    private void process(Socket socket) {
        if(socket == null){
            return;
        }
        try{
            System.out.printf("Server: getting message\n");
            String message = MessageUtils.getMessage(socket);
            System.out.printf("Server: got message: %s\n", message);
            Thread.sleep(1000);
            System.out.printf("Server: sending reply: %s\n", message);
            MessageUtils.sendMessage(socket, "Processed" + message);
            System.out.printf("Server: sent\n");
            closeIgnoringException(socket);
        }catch(Exception e){
            e.printStackTrace();
        }

    }

    private void closeIgnoringException(Socket socket) {
        if(socket != null){
            try{
                socket.close();
            }catch(IOException ignore){
                ignore.printStackTrace();
            }
        }

    }
}
