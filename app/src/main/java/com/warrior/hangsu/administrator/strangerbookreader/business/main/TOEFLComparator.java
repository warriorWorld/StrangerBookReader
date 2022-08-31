package com.warrior.hangsu.administrator.strangerbookreader.business.main;

import com.warrior.hangsu.administrator.strangerbookreader.bean.FileBean;

import java.util.Comparator;
import java.util.List;

/**
 * Created by acorn on 2022/8/30.
 */
public class TOEFLComparator implements Comparator<FileBean> {
    @Override
    public int compare(FileBean o, FileBean t1) {
        String[] list0 = o.name.replaceAll(".txt", "").split("-");
        String[] list1 = t1.name.replaceAll(".txt", "").split("-");
        if (list0.length <= 0 || list1.length <= 0 || list0.length != list1.length) {
            return 0;
        }
        int[] il0 = stringToIntArray(list0);
        int[] il1 = stringToIntArray(list1);
        if (il0 == null || il1 == null) {
            return 0;
        }

        for (int i = 0; i < il0.length; i++) {
            if (il0[i] != il1[i]) {
                return il0[i] > il1[i] ? 1 : -1;
            }
        }
        return 0;
    }

    private int[] stringToIntArray(String[] list) {
        if (null == list || list.length <= 0) {
            return null;
        }
        int[] result = new int[list.length];
        for (int i = 0; i < list.length; i++) {
            try {
                result[i] = Integer.parseInt(list[i]);
            } catch (NumberFormatException e) {
                return null;
            }
        }
        return result;
    }
}
