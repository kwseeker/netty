package top.kwseeker.iobase;

import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;

/**
 * 使用 java.io 的类实现文件的操作：
 *  1）文件的创建
 *  2）文件写、读
 *  3）文件的
 */
@Slf4j
public class FileIO {

    public static void main(String[] args) throws IOException {
        //根据路径创建File对象
        String path = "temp";
        String file = "test.txt";
        File testFile = new File(path + "/" + file);
        File testFile2 = File.createTempFile("test2", ".txt", new File("temp"));    //路径+前缀+long随机数+后缀
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
            Writer fileWriter = new FileWriter(testFile);   //创建一个针对 testFile 的 Writer, 默认以覆盖的方式写
            CharSequence cs = "/bin/bash";
            fileWriter.append('#')
                    .append('!')
                    .append(cs);
            fileWriter.write("\n");
            fileWriter.write("echo hello world");
            fileWriter.flush();
            fileWriter.close();
        }

        //改变后缀赋予执行权限并以shell脚本执行

    }
}
