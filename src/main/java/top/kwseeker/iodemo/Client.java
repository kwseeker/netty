package top.kwseeker.iodemo;

import top.kwseeker.iodemo.nio.NioClient;

public class Client {

    public static void main(String[] args) {
//        Socket socket = null;
//        DataOutputStream writer = null;
//        try {
//            socket = new Socket("localhost", 8081);
//            writer = new DataOutputStream(socket.getOutputStream());
//            writer.writeUTF("Test Message send to BioServer");
//
//            socket = new Socket("localhost", 8082);
//            writer = new DataOutputStream(socket.getOutputStream());
//            writer.writeUTF("Test Message send to PaioServer");
//
//        } catch (IOException e) {
//            e.printStackTrace();
//        } finally {
//            try {
//                if(socket != null) {
//                    socket.close();
//                }
//                if(writer != null) {
//                    writer.close();
//                }
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }

        NioClient nioClient = new NioClient("localhost", 8083);
        nioClient.exec();
    }
}
