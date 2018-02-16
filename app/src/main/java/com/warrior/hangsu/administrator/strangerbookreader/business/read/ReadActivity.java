package com.warrior.hangsu.administrator.strangerbookreader.business.read;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.ClipboardManager;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.warrior.hangsu.administrator.strangerbookreader.R;
import com.warrior.hangsu.administrator.strangerbookreader.animation.ReadAnimationLayout;
import com.warrior.hangsu.administrator.strangerbookreader.db.DbAdapter;
import com.warrior.hangsu.administrator.strangerbookreader.bean.YoudaoResponse;
import com.warrior.hangsu.administrator.strangerbookreader.utils.BaseActivity;
import com.warrior.hangsu.administrator.strangerbookreader.configure.Globle;
import com.warrior.hangsu.administrator.strangerbookreader.utils.ToastUtil;
import com.warrior.hangsu.administrator.strangerbookreader.volley.VolleyCallBack;
import com.warrior.hangsu.administrator.strangerbookreader.volley.VolleyTool;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

public class ReadActivity extends BaseActivity {
    private ReadAnimationLayout readAnimationLayout;
    private TextView topBarRight, topBarLeft;
    private DbAdapter db;//数据库
    private ClipboardManager clip;//复制文本用
    private ReadView readView;
    private AlertDialog dialog;
    /**
     * 时间
     */
    private SimpleDateFormat sdf;
    private Date curDate;
    private String date;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_read);
        db = new DbAdapter(this);
        clip = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        sdf = new SimpleDateFormat("yyyy-MM-dd");
        curDate = new Date(System.currentTimeMillis());//获取当前时间
        date = sdf.format(curDate);
        initUI();
        initDialog();
    }

    private void initDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        dialog = builder.create();
        dialog.setCancelable(true);
    }

    private void initUI() {
        topBarLeft = (TextView) findViewById(R.id.top_bar_left);
        topBarRight = (TextView) findViewById(R.id.top_bar_right);

        topBarLeft.setText(Globle.nowBookName);
        topBarRight.setText("");

        readAnimationLayout = (ReadAnimationLayout) findViewById(R.id.read_animation_layout);
        readView = readAnimationLayout.getReadView();
        readView.setOnWordClickListener(new ReadView.OnWordClickListener() {
            @Override
            public void onWordClick(String word) {
                translation(word);
            }
        });
        readAnimationLayout.setOnPercentChangeListener(new ReadAnimationLayout.OnPercentChangeListener() {
            @Override
            public void onPercentChange(int percent) {
                topBarRight.setText(percent + "%");
            }
        });
    }

    private void updateStatisctics() {
        //初始记录
        int queryC = db.queryStatisticsed(Globle.nowBookName);
        if (queryC > 0) {
            //如果查过这个单词 那就update 并且time+1
            queryC++;
            db.updateStatistics(date, queryC, Globle.nowBookName);
        } else {
            db.insertStatiscticsTb(date, date, "book", 1, (int) readAnimationLayout.
                    getBookLenth(), Globle.nowBookName);
        }
    }

    private void translation(final String word) {
        clip.setText(word);
        //记录查过的单词
        db.insertWordsBookTb(word);
        updateStatisctics();
        if (Globle.closeQueryWord) {
            return;
        }
        String url = Globle.YOUDAO + word;
        HashMap<String, String> params = new HashMap<String, String>();
        VolleyCallBack<YoudaoResponse> callback = new VolleyCallBack<YoudaoResponse>() {

            @Override
            public void loadSucceed(YoudaoResponse result) {
                if (null != result && result.getErrorCode() == 0) {
                    YoudaoResponse.BasicBean item = result.getBasic();
                    String t = "";
                    if (null != item) {
                        for (int i = 0; i < item.getExplains().size(); i++) {
                            t = t + item.getExplains().get(i) + ";";
                        }
                        showOnlyOkDialog(word, result.getQuery() + " [" + item.getPhonetic() +
                                "]: " + "\n" + t);
                    } else {
                        ToastUtil.tipShort(ReadActivity.this, "没查到该词");
                    }
                } else {
                    ToastUtil.tipShort(ReadActivity.this, "网络连接失败");
                }
            }

            @Override
            public void loadFailed(VolleyError error) {
                ToastUtil.tipShort(ReadActivity.this, "error" + error);
            }
        };
        VolleyTool.getInstance(this).requestData(Request.Method.GET,
                ReadActivity.this, url, params,
                YoudaoResponse.class, callback);

    }

    private void showOnlyOkDialog(String title, String msg) {
        dialog.setTitle(title);
        dialog.setMessage(msg);
        dialog.show();
    }

    @Override
    protected void onPause() {
        super.onPause();
        //事实证明如果不在这里插入历史记录的话 如果用户直接kill掉app就不会调用ondestory
        db.updateProgressTOBooksTb(Globle.nowBookName, readAnimationLayout.getNowPosition());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        db.closeDb();
    }

}
