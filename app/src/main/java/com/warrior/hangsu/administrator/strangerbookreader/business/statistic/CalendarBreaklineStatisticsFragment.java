//package com.warrior.hangsu.administrator.strangerbookreader.business.statistic;
//
//import android.graphics.PointF;
//import android.support.v7.widget.LinearLayoutManager;
//import android.support.v7.widget.RecyclerView;
//import android.text.TextUtils;
//import android.view.View;
//
//import com.warrior.hangsu.administrator.strangerbookreader.R;
//import com.warrior.hangsu.administrator.strangerbookreader.adapter.CalendarStatisticsListAdapter;
//import com.warrior.hangsu.administrator.strangerbookreader.bean.CalendarStatisticsBean;
//import com.warrior.hangsu.administrator.strangerbookreader.listener.OnCalendarMonthChangeClickListener;
//import com.warrior.hangsu.administrator.strangerbookreader.utils.WeekUtil;
//import com.warrior.hangsu.administrator.strangerbookreader.widget.breakline.BrokenLineSet;
//import com.warrior.hangsu.administrator.strangerbookreader.widget.breakline.BrokenLineView;
//import com.warrior.hangsu.administrator.strangerbookreader.widget.layout.CalendarViewLayout;
//
//import java.util.ArrayList;
//import java.util.Arrays;
//
///**
// * 个人信息页
// */
//public class CalendarBreaklineStatisticsFragment extends BaseStatisticsFragment implements View.OnClickListener {
//    private ArrayList<CalendarStatisticsBean> handled_list = new ArrayList<>();
//    private CalendarViewLayout calendarCvl;
//    private BrokenLineView readBreakLine, queryRateBreakLine;
//    private View emptyView;
//
//    @Override
//    protected int getLayout() {
//        return R.layout.activity_calendar_breakline_statistics;
//    }
//
//    @Override
//    protected void initUI(View v) {
//        calendarCvl = (CalendarViewLayout) v.findViewById(R.id.calendar_cvl);
//        calendarCvl.setCurrentMonth(currentYear, currentMonth);
//        calendarCvl.setOnCalendarMonthChangeClickListener(new OnCalendarMonthChangeClickListener() {
//            @Override
//            public void onChange(int year, int month) {
//                currentYear = year;
//                currentMonth = month;
//                doGetData();
//            }
//        });
//        emptyView = v.findViewById(R.id.empty_view);
//        readBreakLine = (BrokenLineView) v.findViewById(R.id.read_count_breakline);
//        queryRateBreakLine = (BrokenLineView) v.findViewById(R.id.query_rate_breakline);
//    }
//
//    private void refreshQueryRateBreakLine() {
//        ArrayList<PointF> pointLineList = new ArrayList<PointF>();
//        BrokenLineSet item = new BrokenLineSet();
//        item.setLineColor(getResources().getColor(R.color.english_book_reader));
//        item.setMarkColor(getResources().getColor(R.color.main_text_color));
//        item.setAxleColor(getResources().getColor(R.color.main_text_color));
//        item.setAxle_type(BrokenLineView.AxleType.HalfXY);
//        item.setX_text(WeekUtil.getDateStringWithDate(handled_list.get(0).getCreate_at()) + "到"
//                + WeekUtil.getDateStringWithDate(handled_list.get(handled_list.size() - 1).getCreate_at()));
//        item.setY_text("查词率(%)");
//        float[] hpwnList = new float[handled_list.size()];
//        for (int i = 0; i < handled_list.size(); i++) {
//            hpwnList[i] = handled_list.get(i).getToday_query_word_r();
//        }
//        Arrays.sort(hpwnList);
//        float maxHPWN = hpwnList[hpwnList.length - 1];
//        float minHPWN = hpwnList[0];
//        if (maxHPWN != minHPWN) {
//            item.setOrigin_y(minHPWN);
//            item.setY_count((int) ((maxHPWN - minHPWN) * 100));
//            item.setOrigin_x(0);
//            item.setY_d_value(0.01f);
//            item.setX_count(handled_list.size() - 1);
//            pointLineList.clear();
//            for (int i = 0; i < handled_list.size(); i++) {
//                Float x = Float.valueOf(i);
//                Float y = Float.valueOf(handled_list.get(i).getToday_query_word_r());
//                pointLineList.add(new PointF(x, y));
//            }
//        } else {
//        }
//
//        readBreakLine.setBrokenLineSet(item);
//        readBreakLine.setPointLineList(pointLineList);
//        readBreakLine.invalidate();
//    }
//
//    @Override
//    protected void refreshData() {
//        int[] selecteds = new int[data_list.size()];
//        for (int i = 0; i < selecteds.length; i++) {
//            selecteds[i] = Integer.valueOf(WeekUtil.getDayStringWithDate(data_list.get(i).getCreate_at()));
//        }
//        calendarCvl.setSelecties(selecteds);
//
//        handled_list = handleList(data_list);
//        refreshQueryRateBreakLine();
//    }
//
//
//    /**
//     * 某本书的处理 纵向比对
//     *
//     * @param list 具体某本书的统计数据
//     * @return
//     */
//    private ArrayList<CalendarStatisticsBean> handleList(ArrayList<CalendarStatisticsBean> list) {
//        ArrayList<CalendarStatisticsBean> res = new ArrayList<>();
//        for (int i = 0; i < list.size(); i++) {
//            if (i > 0 && list.get(i).getProgress() > list.get(i - 1).getProgress()) {
//                //每月第一条不统计 因为这些是比对出来的数据 每月第一条没得比对  并且进度得向前才行
//                CalendarStatisticsBean itemOri = list.get(i);//未处理的当前条
//                CalendarStatisticsBean itemLast = list.get(i - 1);//未处理的前一条
//                CalendarStatisticsBean item = new CalendarStatisticsBean();
//                item.setBook_name(itemOri.getBook_name());
//                item.setProgress(itemOri.getProgress());
//                item.setCreate_at(itemOri.getCreate_at());
//                int todayQueryC = itemOri.getQuery_word_c() - itemLast.getQuery_word_c();
//                if (todayQueryC < 0) {
//                    //删除数据库后会出现这个小于之前的情况
//                    todayQueryC = itemOri.getQuery_word_c();
//                }
//                item.setToday_query_word_c(todayQueryC);
//                int todayReadC = (int) (((itemOri.getProgress() - itemLast.getProgress()) / 100) * itemOri.getWord_c()) / 5;//英文单词平均为5个字符
//                item.setToday_read_c(todayReadC);
//                if (todayReadC == 0) {
//                    item.setToday_query_word_r(0);
//                } else {
//                    item.setToday_query_word_r(((float) todayQueryC * 100 / (float) todayReadC));
//                }
//                if (res.size() > 0 &&
//                        WeekUtil.getDateStringWithDate(item.getCreate_at()).equals(WeekUtil.getDateStringWithDate(res.get(res.size() - 1).getCreate_at()))) {
//                    //同一日期相加
//                    item.setToday_query_word_r((item.getToday_query_word_r() + res.get(res.size() - 1).getToday_query_word_r()) / 2);
//                    item.setToday_read_c(item.getToday_read_c() + res.get(res.size() - 1).getToday_read_c());
//                    res.remove(res.size() - 1);
//                }
//                res.add(item);
//            }
//        }
//        return res;
//    }
//
//    @Override
//    public void onClick(View v) {
//        switch (v.getId()) {
//        }
//    }
//}
