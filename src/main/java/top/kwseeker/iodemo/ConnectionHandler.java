package top.kwseeker.iodemo;

import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

@Slf4j
public class ConnectionHandler implements Runnable {

    private Socket socket;

    ConnectionHandler(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            //PrintWriter writer = new PrintWriter(socket.getOutputStream(), true);

            String content;
            while (true) {
                if((content = reader.readLine()) == null) {
                    break;
                }
                log.info("Message from client: {}", content);
            }
        } catch (IOException e) {
            e.printStackTrace();
            log.error("BioServer ConnectionHandler exception: {}", e.getMessage());
        } finally {
            try {
                socket.close();
                log.info("BioServer connection socket stopped");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
