package top.kwseeker.iobase;

import lombok.extern.slf4j.Slf4j;

import java.io.*;

/**
 * 使用 java.io 的类实现文件的操作：
 *  1）文件的创建，目录的创建
 *  2）文件写、读、重命名、权限设置、执行
 *  3）文件的删除，目录的删除
 * 相关类
 *  File （指代一个文件或目录，继承 Serializable 和 Comparable<File> 接口）
 *  FileSystem
 *    WinNTFileSystem
 *  FileDescriptor
 *  FileFilter
 *  FilenameFilter
 *  FilePermission
 *  FileInputStream （字节流的方式写文件）
 *  FileOutputStream （字节流的方式读文件）
 *  FileReader （字符流的方式读文件，继承InputStreamReader）
 *  FileWriter （字符流的方式写文件）
 *  RandomAccessFile
 */
@Slf4j
public class FileIO {

    public static void main(String[] args) {
        File directory = null;
        Writer fileWriter = null;
        Reader fileReader = null;
        BufferedReader input = null;
        try {

            /**
             * 根据路径创建File对象
             */
            String path = "temp";
            String file = "test.txt";
            directory = new File(path);
            File testFile = new File(path + "/" + file);
            if(!directory.exists() && directory.mkdir()) {
                File testFile2 = File.createTempFile("test2", ".txt", new File("temp"));    //路径+前缀+long随机数+后缀
                log.info("Parent: " + testFile2.getParent());
            }
            //判断文件或目录是否存在
            if(!testFile.exists()) {
                if(testFile.createNewFile()) {      //（1）,文件默认权限是 644
                    log.info("文件创建成功");
                } else {
                    log.info("文件创建失败");
                    return;
                }
            }

            /**
             * 文件描述对象
             */
//            FileDescriptor fileDesc = testFile.get

            /**
             * 文件读写操作
             */
            if(testFile.isFile() && testFile.length() == 0) {
                log.info("文件为空，现在写入数据");    //（2）

                //以字符流的方式读写
                fileWriter = new FileWriter(testFile);   //创建一个针对 testFile 的 Writer, 默认以覆盖的方式写
                CharSequence cs = "/bin/bash";
                fileWriter.append('#')
                        .append('!')
                        .append(cs);
                fileWriter.write('\n');
                fileWriter.write("echo hello world");
                fileWriter.flush(); //前面的写操作是将数据写入内存，flush()将内存的数据写入到文件的存储空间

                log.info("testFile length: " + testFile.length());

                fileReader = new FileReader(testFile);
                int charInt;
                for(charInt = fileReader.read(); charInt != -1; charInt = fileReader.read()) {  //FileReader.read() 一次读一个字符
                    System.out.print((char)charInt);
                }
                System.out.println();

                //以字节流的方式读写
                //注意：前面用的FileWriter，换为OutputStream后不指定append标志的话，会清空之前写的内容，重新写
                OutputStream outputStream = new FileOutputStream(testFile, true);   //附加写
                outputStream.write('\n');
                outputStream.write("echo hello world ".getBytes());
                outputStream.write('2');  //写入int的低8位数据
                outputStream.flush();
                outputStream.close();

                log.info("testFile length: " + testFile.length());

                InputStream inputStream = new FileInputStream(testFile);
                //InputStream inputStream = new FileInputStream(testFile.getName());
                int available = inputStream.available();
                FileDescriptor fd = ((FileInputStream) inputStream).getFD();
                fd.sync();  //同步等待所有更改的数据或属性都写入到这个文件后才返回
                byte[] content = new byte[(int)testFile.length()];
                int readByteCount = inputStream.read(content);
                log.info("readByteCount:" + readByteCount + "\n"
                    + "content: " + new String(content));
                inputStream.close();

                //使用 RandomAccessFile 实现文件的灵活读写 (替换文件第二行数据)
                RandomAccessFile randomAccessFile = new RandomAccessFile(testFile, "rw");
                randomAccessFile.readLine();
                long firstEnd = randomAccessFile.getFilePointer();
                randomAccessFile.readLine();
                long pointer = randomAccessFile.getFilePointer();
                byte[] temp = new byte[(int)(testFile.length() - pointer)];
                randomAccessFile.readFully(temp);   //从当前位置读到结尾
                randomAccessFile.seek(firstEnd);
                randomAccessFile.writeBytes("echo hello Arvin\n");
                randomAccessFile.write(temp);

                randomAccessFile.close();
            }

            /**
             * 文件权限控制
             * 改变后缀赋予执行权限并以shell脚本执行
             */
            File dest = new File("temp/test.sh");
            if(testFile.renameTo(dest)) {
                if(dest.setExecutable(true) && System.getProperty("os.name").toLowerCase().equals("linux")) {
                    log.info("dest path: " + dest.getPath());
                    Process ps = Runtime.getRuntime().exec(dest.getPath());
                    ps.waitFor();
                    //读取ps执行返回结果
                    input = new BufferedReader(new InputStreamReader(ps.getInputStream()));
                    String line;
                    while ((line = input.readLine()) != null) {
                        System.out.println(line);
                    }
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            try {
                if(fileWriter != null) {
                    fileWriter.close();
                }
                if(fileReader != null) {
                    fileReader.close();
                }
                if(input != null) {
                    input.close();
                }
                //清理文件
//                if(directory != null) {
//                    File[] files = directory.listFiles();
//                    if(files != null && files.length > 0) {
//                        for(File f : files) {
//                            boolean deleteRet = f.delete();
//                            log.info(f.getName() + "删除" + (deleteRet?"成功":"失败"));
//                        }
//                    }
//                    log.info("目录" + directory.getName() + "删除" + (directory.delete()?"成功":"失败"));
//                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
