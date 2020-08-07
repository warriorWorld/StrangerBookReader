package com.warrior.hangsu.administrator.strangerbookreader.sort;

import com.warrior.hangsu.administrator.strangerbookreader.bean.FileBean;

import java.util.Comparator;

/**
 * 这个是给文件排序并重命名的
 */
public class FileComparatorByTime implements Comparator<FileBean> {

    @Override
    public int compare(FileBean f1, FileBean f2) {
        return Long.compare(f2.modifiedDate, f1.modifiedDate);
    }
}
