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
import com.warrior.hangsu.administrator.strangerbookreader.widget.dialog.SingleLoadBarUtil;
import com.warrior.hangsu.administrator.strangerbookreader.widget.tag.ToggleTag;

import java.util.ArrayList;

/**
 * 个人信息页
 */
public class ClassifyFragment extends BaseFragment {
    private FlexboxLayout fictionPressFbl;
    private ArrayList<ClassifyListBean> fictionPressTagList = new ArrayList<>();
    private ArrayList<ClassifyListBean> fictionPressTagList1 = new ArrayList<>();
    private ArrayList<ClassifyListBean> classicTagList = new ArrayList<>();
    private ArrayList<ClassifyListBean> fanFictionTagList = new ArrayList<>();
    private ArrayList<ClassifyListBean> fanFictionTagList1 = new ArrayList<>();
    private FlexboxLayout fictionPressFbl1;
    private FlexboxLayout classicReaderFbl;
    private FlexboxLayout fanFictionFbl;
    private FlexboxLayout fanFictionFbl1;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        SingleLoadBarUtil.getInstance().showLoadBar(getActivity());
        View v = inflater.inflate(R.layout.fragment_classify, container, false);
        initUI(v);
        try {
            doGetData();
        } catch (Exception e) {
            SingleLoadBarUtil.getInstance().dismissLoadBar();
        }
        return v;
    }

    private void initUI(View v) {
        fictionPressFbl = (FlexboxLayout) v.findViewById(R.id.fiction_press_fbl);
        fictionPressFbl1 = (FlexboxLayout) v.findViewById(R.id.fiction_press_fbl1);
        classicReaderFbl = (FlexboxLayout) v.findViewById(R.id.classic_reader_fbl);
        fanFictionFbl = (FlexboxLayout) v.findViewById(R.id.fan_fiction_fbl);
        fanFictionFbl1 = (FlexboxLayout) v.findViewById(R.id.fan_fiction_fbl1);
    }


    protected void doGetData() {
        new Thread(new Runnable() {
            @Override
            public void run() {
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
                ClassifyListBean item20 = new ClassifyListBean();
                item20.setTag("科幻");
                item20.setUrl("https://www.fictionpress.com/fiction/Sci-Fi/");
                fictionPressTagList.add(item20);
                ClassifyListBean item5 = new ClassifyListBean();
                item5.setTag("散文");
                item5.setUrl("https://www.fictionpress.com/fiction/Essay/");
                fictionPressTagList.add(item5);
                ClassifyListBean item6 = new ClassifyListBean();
                item6.setTag("儿童读物");
                item6.setUrl("https://www.fictionpress.com/fiction/Kids/");
                fictionPressTagList.add(item6);
                ClassifyListBean item7 = new ClassifyListBean();
                item7.setTag("灵异");
                item7.setUrl("https://www.fictionpress.com/fiction/Spiritual/");
                fictionPressTagList.add(item7);
                ClassifyListBean item8 = new ClassifyListBean();
                item8.setTag("寓言");
                item8.setUrl("https://www.fictionpress.com/fiction/Fable/");
                fictionPressTagList.add(item8);
                ClassifyListBean item9 = new ClassifyListBean();
                item9.setTag("日本漫画");
                item9.setUrl("https://www.fictionpress.com/fiction/Manga/");
                fictionPressTagList.add(item9);
                ClassifyListBean item10 = new ClassifyListBean();
                item10.setTag("超自然");
                item10.setUrl("https://www.fictionpress.com/fiction/Supernatural/");
                fictionPressTagList.add(item10);
                ClassifyListBean item11 = new ClassifyListBean();
                item11.setTag("幻想");
                item11.setUrl("https://www.fictionpress.com/fiction/Fantasy/");
                fictionPressTagList.add(item11);
                ClassifyListBean item12 = new ClassifyListBean();
                item12.setTag("神秘");
                item12.setUrl("https://www.fictionpress.com/fiction/Mystery/");
                fictionPressTagList.add(item12);
                ClassifyListBean item13 = new ClassifyListBean();
                item13.setTag("惊悚");
                item13.setUrl("https://www.fictionpress.com/fiction/Thriller/");
                fictionPressTagList.add(item13);
                ClassifyListBean item14 = new ClassifyListBean();
                item14.setTag("综合");
                item14.setUrl("https://www.fictionpress.com/fiction/General/");
                fictionPressTagList.add(item14);
                ClassifyListBean item15 = new ClassifyListBean();
                item15.setTag("神话");
                item15.setUrl("https://www.fictionpress.com/fiction/Mythology/");
                fictionPressTagList.add(item15);
                ClassifyListBean item16 = new ClassifyListBean();
                item16.setTag("西部");
                item16.setUrl("https://www.fictionpress.com/fiction/Western/");
                fictionPressTagList.add(item16);
                ClassifyListBean item17 = new ClassifyListBean();
                item17.setTag("历史");
                item17.setUrl("https://www.fictionpress.com/fiction/Historical/");
                fictionPressTagList.add(item17);
                ClassifyListBean item18 = new ClassifyListBean();
                item18.setTag("竞技");
                item18.setUrl("https://www.fictionpress.com/fiction/Play/");
                fictionPressTagList.add(item18);
                ClassifyListBean item19 = new ClassifyListBean();
                item19.setTag("青年读物");
                item19.setUrl("https://www.fictionpress.com/fiction/Young-Adult/");
                fictionPressTagList.add(item19);


                ClassifyListBean item21 = new ClassifyListBean();
                item21.setTag("家庭");
                item21.setUrl("https://www.fictionpress.com/poetry/Family/");
                fictionPressTagList1.add(item21);
                ClassifyListBean item22 = new ClassifyListBean();
                item22.setTag("幽默");
                item22.setUrl("https://www.fictionpress.com/poetry/Humor/");
                fictionPressTagList1.add(item22);
                ClassifyListBean item23 = new ClassifyListBean();
                item23.setTag("宗教");
                item23.setUrl("https://www.fictionpress.com/poetry/Religion/");
                fictionPressTagList1.add(item23);
                ClassifyListBean item24 = new ClassifyListBean();
                item24.setTag("幻想");
                item24.setUrl("https://www.fictionpress.com/poetry/Fantasy/");
                fictionPressTagList1.add(item24);
                ClassifyListBean item25 = new ClassifyListBean();
                item25.setTag("生活");
                item25.setUrl("https://www.fictionpress.com/poetry/Life/");
                fictionPressTagList1.add(item25);
                ClassifyListBean item26 = new ClassifyListBean();
                item26.setTag("校园");
                item26.setUrl("https://www.fictionpress.com/poetry/School/");
                fictionPressTagList1.add(item26);
                ClassifyListBean item27 = new ClassifyListBean();
                item27.setTag("友谊");
                item27.setUrl("https://www.fictionpress.com/poetry/Friendship/");
                fictionPressTagList1.add(item27);
                ClassifyListBean item28 = new ClassifyListBean();
                item28.setTag("爱情");
                item28.setUrl("https://www.fictionpress.com/poetry/Love/");
                fictionPressTagList1.add(item28);
                ClassifyListBean item29 = new ClassifyListBean();
                item29.setTag("诗歌");
                item29.setUrl("https://www.fictionpress.com/poetry/Song/");
                fictionPressTagList1.add(item29);
                ClassifyListBean item30 = new ClassifyListBean();
                item30.setTag("综合");
                item30.setUrl("https://www.fictionpress.com/poetry/General/");
                fictionPressTagList1.add(item30);
                ClassifyListBean item31 = new ClassifyListBean();
                item31.setTag("大自然");
                item31.setUrl("https://www.fictionpress.com/poetry/Nature/");
                fictionPressTagList1.add(item31);
                ClassifyListBean item32 = new ClassifyListBean();
                item32.setTag("战争");
                item32.setUrl("https://www.fictionpress.com/poetry/War/");
                fictionPressTagList1.add(item32);
                ClassifyListBean item33 = new ClassifyListBean();
                item33.setTag("三行俳句诗");
                item33.setUrl("https://www.fictionpress.com/poetry/Haiku/");
                fictionPressTagList1.add(item33);
                ClassifyListBean item34 = new ClassifyListBean();
                item34.setTag("政治");
                item34.setUrl("https://www.fictionpress.com/poetry/Politics/");
                fictionPressTagList1.add(item34);
                ClassifyListBean item35 = new ClassifyListBean();
                item35.setTag("工作");
                item35.setUrl("https://www.fictionpress.com/poetry/Work/");
                fictionPressTagList1.add(item35);


                ClassifyListBean item36 = new ClassifyListBean();
                item36.setTag("动画/日本漫画");
                item36.setUrl("https://www.fanfiction.net/anime/");
                fanFictionTagList.add(item36);
                ClassifyListBean item37 = new ClassifyListBean();
                item37.setTag("书");
                item37.setUrl("https://www.fanfiction.net/book/");
                fanFictionTagList.add(item37);
                ClassifyListBean item38 = new ClassifyListBean();
                item38.setTag("卡通");
                item38.setUrl("https://www.fanfiction.net/cartoon/");
                fanFictionTagList.add(item38);
                ClassifyListBean item39 = new ClassifyListBean();
                item39.setTag("欧美漫画");
                item39.setUrl("https://www.fanfiction.net/comic/");
                fanFictionTagList.add(item39);
                ClassifyListBean item40 = new ClassifyListBean();
                item40.setTag("游戏");
                item40.setUrl("https://www.fanfiction.net/game/");
                fanFictionTagList.add(item40);
                ClassifyListBean item44 = new ClassifyListBean();
                item44.setTag("混合");
                item44.setUrl("https://www.fanfiction.net/misc/");
                fanFictionTagList.add(item44);
                ClassifyListBean item41 = new ClassifyListBean();
                item41.setTag("电影");
                item41.setUrl("https://www.fanfiction.net/movie/");
                fanFictionTagList.add(item41);
                ClassifyListBean item42 = new ClassifyListBean();
                item42.setTag("节目");
                item42.setUrl("https://www.fanfiction.net/play/");
                fanFictionTagList.add(item42);
                ClassifyListBean item43 = new ClassifyListBean();
                item43.setTag("电视剧");
                item43.setUrl("https://www.fanfiction.net/tv/");
                fanFictionTagList.add(item43);


                ClassifyListBean item45 = new ClassifyListBean();
                item45.setTag("动画/日本漫画");
                item45.setUrl("https://www.fanfiction.net/crossovers/anime/");
                fanFictionTagList1.add(item45);
                ClassifyListBean item46 = new ClassifyListBean();
                item46.setTag("书");
                item46.setUrl("https://www.fanfiction.net/crossovers/book/");
                fanFictionTagList1.add(item46);
                ClassifyListBean item47 = new ClassifyListBean();
                item47.setTag("卡通");
                item47.setUrl("https://www.fanfiction.net/crossovers/cartoon/");
                fanFictionTagList1.add(item47);
                ClassifyListBean item48 = new ClassifyListBean();
                item48.setTag("欧美漫画");
                item48.setUrl("https://www.fanfiction.net/crossovers/comic/");
                fanFictionTagList1.add(item48);
                ClassifyListBean item49 = new ClassifyListBean();
                item49.setTag("游戏");
                item49.setUrl("https://www.fanfiction.net/crossovers/game/");
                fanFictionTagList1.add(item49);
                ClassifyListBean item50 = new ClassifyListBean();
                item50.setTag("混合");
                item50.setUrl("https://www.fanfiction.net/crossovers/misc/");
                fanFictionTagList1.add(item50);
                ClassifyListBean item51 = new ClassifyListBean();
                item51.setTag("电影");
                item51.setUrl("https://www.fanfiction.net/crossovers/movie/");
                fanFictionTagList1.add(item51);
                ClassifyListBean item52 = new ClassifyListBean();
                item52.setTag("节目");
                item52.setUrl("https://www.fanfiction.net/crossovers/play/");
                fanFictionTagList1.add(item52);
                ClassifyListBean item53 = new ClassifyListBean();
                item53.setTag("电视剧");
                item53.setUrl("https://www.fanfiction.net/crossovers/tv/");
                fanFictionTagList1.add(item53);


                ClassifyListBean item54 = new ClassifyListBean();
                item54.setTag("小说");
                item54.setUrl("http://www.classicreader.com/browse/1/");
                classicTagList.add(item54);
                ClassifyListBean item55 = new ClassifyListBean();
                item55.setTag("非小说");
                item55.setUrl("hhttp://www.classicreader.com/browse/2/");
                classicTagList.add(item55);
                ClassifyListBean item56 = new ClassifyListBean();
                item56.setTag("年轻读者");
                item56.setUrl("http://www.classicreader.com/browse/3/");
                classicTagList.add(item56);
                ClassifyListBean item57 = new ClassifyListBean();
                item57.setTag("诗");
                item57.setUrl("http://www.classicreader.com/browse/4/");
                classicTagList.add(item57);
                ClassifyListBean item58 = new ClassifyListBean();
                item58.setTag("短故事");
                item58.setUrl("http://www.classicreader.com/browse/5/");
                classicTagList.add(item58);
                ClassifyListBean item59 = new ClassifyListBean();
                item59.setTag("戏剧");
                item59.setUrl("http://www.classicreader.com/browse/6/");
                classicTagList.add(item59);
                ClassifyListBean item60 = new ClassifyListBean();
                item60.setTag("经典");
                item60.setUrl("http://www.classicreader.com/browse/7/");
                classicTagList.add(item60);
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        addTags(fictionPressFbl, fictionPressTagList);
                        addTags(fictionPressFbl1, fictionPressTagList1);
                        addTags(fanFictionFbl, fanFictionTagList);
                        addTags(fanFictionFbl1, fanFictionTagList1);
                        addTags(classicReaderFbl, classicTagList);
                    }
                });
            }
        }).run();
    }

    private void addTags(FlexboxLayout flexboxLayout, ArrayList<ClassifyListBean> list) {
        for (int i = 0; i < list.size(); i++) {
            addOneNewTag(flexboxLayout, list.get(i));
        }
        SingleLoadBarUtil.getInstance().dismissLoadBar();
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
