package com.warrior.hangsu.administrator.strangerbookreader.analysis;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;

import android.os.Environment;

import com.warrior.hangsu.administrator.strangerbookreader.utils.Logger;

/**
 * 给它一个书籍文件地址,返回书籍内容
 *
 * @author Administrator
 */
public class AnalysisTXTBook extends AnalysisBook {
    private RandomAccessFile aFile;
    private FileChannel fc;
    private MappedByteBuffer out;


    public AnalysisTXTBook(String path) {
        try {
            // 这个类是随机读取文件的类 可以从任意位置读文件
            aFile = new RandomAccessFile(path, "rw");
            // 文件通道 直接强行把存储内的文件放到内存中 效率高 最大单个文件能达到2G
            fc = aFile.getChannel();
            bookLength = aFile.length();
            out = fc.map(FileChannel.MapMode.READ_WRITE, 0, aFile.length());
            Logger.d("aFile.length():" + aFile.length());
            // 一定要关上
            fc.close();
            aFile.close();
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }


    @Override
    public String getBookContent(int start, int lenth) {
        String s = "";
        //开始位置不能小于0
        if (start < 0) {
            start = 0;
        }
        int end;
        //判断所要的长度是否超出文字总数 如果超出就给他到最后一个字的
        if (bookLength >= start + lenth) {
            end = start + lenth;
        } else {
            end = (int) bookLength;
        }
        //这样请求到最后发现返回值是空的话 就知道已经结束了
        if (start >= end) {
            return "";
        }
//        Logger.d("out.array().length" + out.array().length);
        for (int i = start; i < end; i++) {
            // System.out.print((char) out.get(i));
            s = s + (char) out.get(i);
        }
        return s;

    }

    @Override
    public long getBookLength() {
        return bookLength;
    }

}
