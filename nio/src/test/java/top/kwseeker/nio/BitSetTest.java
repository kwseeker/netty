package top.kwseeker.nio;

import org.junit.Assert;
import org.junit.Test;

import java.util.BitSet;

/**
 * java.util.BitSet 测试
 * 数据结构
 * registered = {BitSet@808} "{}"
 *  words = {long[1]@811}   //long型数组，数组默认size=1,包含64bits (8*8 = 2^6), 私有方法 wordIndex() 是获取某个数值所在元素的数组索引
 *  wordsInUse = 0
 *  sizeIsSticky = false
 * 支持自动扩容
 */
public class BitSetTest {

    //Java NIO EPollArrayWrapper 中使用BitSet 记录已经注册监听的FD值
    @Test
    public void testNioUseBitSet() {
        BitSet registered = new BitSet();
        int fdVal = 24;
        registered.set(fdVal);                  //将第（24+1）个位上的值设置为1
        registered.set(30);
        boolean exist = registered.get(fdVal);
        System.out.println(registered.nextSetBit(fdVal));   //获取下一设置为1的位索引值
        System.out.println(registered.nextSetBit(fdVal+1));
        Assert.assertTrue(exist);
        registered.clear(fdVal);
        exist = registered.get(fdVal);
        Assert.assertFalse(exist);
    }
}
