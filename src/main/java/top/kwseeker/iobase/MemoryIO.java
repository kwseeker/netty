package top.kwseeker.iobase;

import java.io.*;
import java.nio.CharBuffer;

/**
 * 相关类：
 *      CharArrayReader  （可以以字符流的方式读取char数组）
 *      CharArrayWriter
 *      BufferedReader
 *      BufferedWriter
 *      ByteArrayInputStream
 *      ByteArrayOutputStream
 *      BufferedInputStream
 *      BufferedOutputStream
 *      StringReader
 *      StringWriter
 */
public class MemoryIO {

    public static void main(String[] args) {

        try {

            //CharArrayWriter
            //CharArrayReader
            CharArrayWriter charArrayWriter = new CharArrayWriter();
            charArrayWriter.write("char array from somewhere");
            char[] chars = charArrayWriter.toCharArray();
            CharArrayReader charArrayReader = new CharArrayReader(chars);
            int data1;
            while((data1 = charArrayReader.read()) != -1) {
                System.out.print((char) data1);
            }
            System.out.println();
            charArrayWriter.close();
            charArrayReader.close();

            //BufferedWriter
            //BufferedReader
            BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter("temp/test.txt"));
            bufferedWriter.write("Test message write to BufferedWriter");
            bufferedWriter.flush();
            BufferedReader bufferedReader = new BufferedReader(new FileReader("temp/test.txt"));
            String line;
            while((line = bufferedReader.readLine()) != null) {
                System.out.println(line);
            }
            bufferedWriter.close();
            bufferedReader.close();

            //BufferedInputStream
            //BufferedOutputStream
            BufferedOutputStream outputStream = new BufferedOutputStream(new FileOutputStream("temp/test.txt", true));
            outputStream.write("\nAppending message write to BufferedOutputStream".getBytes());
            outputStream.flush();
            BufferedInputStream inputStream = new BufferedInputStream(new FileInputStream("temp/test.txt"));
            int d;
            StringBuffer s = new StringBuffer();
            while((d = inputStream.read()) != -1) {
                s.append((char)d);
            }
            System.out.println(s.toString());
            outputStream.close();
            inputStream.close();

            //ByteArrayInputStream
            //ByteArrayOutputStream
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            byteArrayOutputStream.write("byte array from somewhere".getBytes());
            byte[] bytes = byteArrayOutputStream.toByteArray();
            InputStream inputStream1 = new ByteArrayInputStream(bytes);  //将 byte 数组转换成 输入流
            int data;
            StringBuffer sb = new StringBuffer();
            while((data = inputStream1.read()) != -1) {
                sb.append((char)data);
            }
            System.out.println(sb.toString());

            //StringReader
            //StringWriter
            Writer writer = new StringWriter();
            writer.write("This must be a joke");
            String temp = writer.toString();
            System.out.println(temp);
            Reader reader = new StringReader("This also is a joke");
            int data2;
            StringBuffer sb1 = new StringBuffer();
            while((data2 = reader.read()) != -1) {
                sb1.append((char)data2);
            }
            System.out.println(sb1.toString());

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
