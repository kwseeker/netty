package top.kwseeker.iodemo;

import lombok.extern.slf4j.Slf4j;
import top.kwseeker.iodemo.nio.NioServer;

@Slf4j
public class Server {

    public static void main(String[] args) {
//        try {
//            log.info("启动 BioServer ...");
//            BioServer.start(8081);
//
//            log.info("启动 PaioServer ...");
//            BioServer.start(8082);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }

        log.info("启动 NioServer ...");
        NioServer.start(8083);
    }
}
