package com.warrior.hangsu.administrator.strangerbookreader.business.main;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.flexbox.FlexboxLayout;
import com.warrior.hangsu.administrator.strangerbookreader.R;
import com.warrior.hangsu.administrator.strangerbookreader.base.BaseFragment;
import com.warrior.hangsu.administrator.strangerbookreader.bean.ClassifyListBean;
import com.warrior.hangsu.administrator.strangerbookreader.utils.ToastUtils;
import com.warrior.hangsu.administrator.strangerbookreader.widget.tag.ToggleTag;

import java.util.ArrayList;

/**
 * 个人信息页
 */
public class ClassifyFragment extends BaseFragment {
    private FlexboxLayout fictionPressFbl;
    private ArrayList<ClassifyListBean> fictionPressTagList = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View v = inflater.inflate(R.layout.fragment_classify, container, false);
        initUI(v);
        doGetData();
        return v;
    }

    private void initUI(View v) {
        fictionPressFbl = (FlexboxLayout) v.findViewById(R.id.fiction_press_fbl);
    }


    protected void doGetData() {
        fictionPressTagList = new ArrayList<>();
        ClassifyListBean item = new ClassifyListBean();
        item.setTag("战斗");
        item.setUrl("https://www.fictionpress.com/fiction/Action/");
        fictionPressTagList.add(item);
        ClassifyListBean item1 = new ClassifyListBean();
        item1.setTag("恐怖");
        item1.setUrl("https://www.fictionpress.com/fiction/Horror/");
        fictionPressTagList.add(item1);
        ClassifyListBean item2 = new ClassifyListBean();
        item2.setTag("浪漫");
        item2.setUrl("https://www.fictionpress.com/fiction/Romance/");
        fictionPressTagList.add(item2);
        ClassifyListBean item3 = new ClassifyListBean();
        item3.setTag("传记");
        item3.setUrl("https://www.fictionpress.com/fiction/Biography/");
        fictionPressTagList.add(item3);
        ClassifyListBean item4 = new ClassifyListBean();
        item4.setTag("搞笑");
        item4.setUrl("https://www.fictionpress.com/fiction/Humor/");
        fictionPressTagList.add(item4);
        addTags(fictionPressFbl, fictionPressTagList);
    }

    private void addTags(FlexboxLayout flexboxLayout, ArrayList<ClassifyListBean> list) {
        for (int i = 0; i < list.size(); i++) {
            addOneNewTag(flexboxLayout, list.get(i));
        }
    }

    /**
     * 在页面中添加一个标签
     */
    private void addOneNewTag(FlexboxLayout flexboxLayout, final ClassifyListBean item) {
        ToggleTag tagBtn = new ToggleTag(getActivity());
        tagBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ToastUtils.showSingleToast(item.getUrl());
            }
        });
        tagBtn.setItem(item);
        flexboxLayout.addView(tagBtn);
    }
}
