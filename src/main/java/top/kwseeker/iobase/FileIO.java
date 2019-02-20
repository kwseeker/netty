package top.kwseeker.iobase;

import lombok.extern.slf4j.Slf4j;

import java.io.*;

/**
 * 使用 java.io 的类实现文件的操作：
 *  1）文件的创建，目录的创建
 *  2）文件写、读、重命名、权限设置、执行
 *  3）文件的删除，目录的删除
 */
@Slf4j
public class FileIO {

    public static void main(String[] args) {
        File directory = null;
        Writer fileWriter = null;
        Reader fileReader = null;
        BufferedReader input = null;
        try {
            //根据路径创建File对象
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

            if(testFile.isFile() && testFile.length() == 0) {
                log.info("文件为空，现在写入数据");    //（2）
                fileWriter = new FileWriter(testFile);   //创建一个针对 testFile 的 Writer, 默认以覆盖的方式写
                CharSequence cs = "/bin/bash";
                fileWriter.append('#')
                        .append('!')
                        .append(cs);
                fileWriter.write("\n");
                fileWriter.write("echo hello world");
                fileWriter.flush();

                log.info("testFile length: " + testFile.length());

                fileReader = new FileReader(testFile);
                int charInt;
                for(charInt = fileReader.read(); charInt != -1; charInt = fileReader.read()) {  //FileReader.read() 一次读一个字符
                    System.out.print((char)charInt);
                }
            }

            //改变后缀赋予执行权限并以shell脚本执行
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
                if(directory != null) {
                    File[] files = directory.listFiles();
                    if(files != null && files.length > 0) {
                        for(File f : files) {
                            boolean deleteRet = f.delete();
                            log.info(f.getName() + "删除" + (deleteRet?"成功":"失败"));
                        }
                    }
                    log.info("目录" + directory.getName() + "删除" + (directory.delete()?"成功":"失败"));
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
