package top.kwseeker.iobase;

import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.util.Date;

import static java.lang.Thread.interrupted;

/**
 * TODO：使用pipe实现线程间的双工通信
 *
 * 相关类
 *      PipedInputStream （字节流的形式）
 *      PipedOutputStream
 *      PipedReader （字符流的形式）
 *      PipedWriter
 *
 * 管道默认分配空间是1024byte
 * 管道 read() 是阻塞的；
 * available() 是非阻塞的；
 */
@Slf4j
public class ThreadIO {

    public static void main(String[] args) {

        try {
            //以线程的角度看管道
            final PipedInputStream input = new PipedInputStream();         //管道数据输入到线程
            final PipedOutputStream output = new PipedOutputStream(input);  //线程的数据输出到管道
            final PipedWriter writer = new PipedWriter();
            final PipedReader reader = new PipedReader(writer);

            Thread thread1 = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        output.write("[Thread1]: Hello, I'm Thread1. ".getBytes());
                        while(!interrupted()) {
                            try {
                                Thread.sleep(200);
                                output.write("loop message ... ".getBytes());
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });

            Thread thread2 = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        byte[] msg = new byte[64];
                        while(!Thread.interrupted()) {
                            System.out.println(new Date().toString() + " Message Received: ");
                            int num = input.read(msg);
                            log.info("管道输入端输入 " + num + " 字节");
                            System.out.println("\t"+ new String(msg));
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });

            Thread thread3 = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        writer.write("[Thread3]: Hello, I'm Thread3. ");
                        writer.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });

            Thread thread4 = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        int data;
                        StringBuffer sb = new StringBuffer();
                        while((data = reader.read()) != -1) {   //按字符读取
                            sb.append((char)data);
                        }
                        System.out.println("Received Message: " + sb.toString());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });

//            thread2.start();
//            Thread.sleep(100);
//            thread1.start();
            thread3.start();
            thread4.start();

        } catch (IOException e) {
            e.printStackTrace();
        }
//        catch (InterruptedException e) {
//            e.printStackTrace();
//        }
    }
}
