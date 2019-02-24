package top.kwseeker.iobase;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * 相关类：
 *      ByteArrayInputStream
 *      ByteArrayOutputStream
 *      CharArrayReader
 *
 */
public class MemoryIO {

    public static void main(String[] args) {

        try {

            byte[] bytes = "byte array from somewhere".getBytes();
            InputStream inputStream = new ByteArrayInputStream(bytes);  //将 byte 数组转换成 输入流
            int data;
            do {
                data = inputStream.read();
            } while (data != 1);

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            outputStream.write(bytes);
            byte[] target = outputStream.toByteArray();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
