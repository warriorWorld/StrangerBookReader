//package com.warrior.hangsu.administrator.strangerbookreader.business.main;
//
//import android.app.AlertDialog;
//import android.content.DialogInterface;
//import android.content.Intent;
//import android.net.Uri;
//import android.os.Bundle;
//import android.support.v4.app.Fragment;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.CheckBox;
//import android.widget.CompoundButton;
//import android.widget.ImageView;
//import android.widget.RelativeLayout;
//
//import com.warrior.hangsu.administrator.strangerbookreader.R;
//import com.warrior.hangsu.administrator.strangerbookreader.business.statistic.StatisticsActivity;
//import com.warrior.hangsu.administrator.strangerbookreader.configure.Globle;
//import com.warrior.hangsu.administrator.strangerbookreader.utils.ImageUtil;
//import com.warrior.hangsu.administrator.strangerbookreader.utils.SharedPreferencesUtils;
//import com.warrior.hangsu.administrator.strangerbookreader.utils.ToastUtil;
//import com.warrior.hangsu.administrator.strangerbookreader.business.wordsbook.WordsBookActivity;
//
//public class UserFragment extends Fragment implements View.OnClickListener {
//    private View mainView;
//    private RelativeLayout wordBookRL, statisticRL, collocatRL, downloadWatchMangaRL,
//            checkUpdateRL, feedbackRL, aboutRL;
//    private AlertDialog dialog;
//    private CheckBox closeQueryWordCB;
//
//    @Override
//    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
//        mainView = inflater.inflate(R.layout.activity_user, null);
//
//        initUI(mainView);
//        initDialog();
//        return mainView;
//    }
//
//
//    private void initDialog() {
//        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
//        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
//
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//            }
//        });
//        dialog = builder.create();
//        dialog.setCancelable(true);
//    }
//
//    private void initUI(View v) {
//        wordBookRL = (RelativeLayout) v.findViewById(R.id.word_book);
//        statisticRL = (RelativeLayout) v.findViewById(R.id.statistic);
//        collocatRL = (RelativeLayout) v.findViewById(R.id.collect);
//        downloadWatchMangaRL = (RelativeLayout) v.findViewById(R.id.download_manga);
//        checkUpdateRL = (RelativeLayout) v.findViewById(R.id.check_update);
//        feedbackRL = (RelativeLayout) v.findViewById(R.id.feedback);
//        aboutRL = (RelativeLayout) v.findViewById(R.id.about);
//
//        closeQueryWordCB = (CheckBox) v.findViewById(R.id.close_query_word);
//        closeQueryWordCB.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//
//            @Override
//            public void onCheckedChanged(CompoundButton buttonView,
//                                         boolean isChecked) {
//                Globle.closeQueryWord = isChecked;
//                SharedPreferencesUtils.setSharedPreferencesData(
//                        getActivity(), "closeQueryWord", isChecked + "");
//            }
//        });
//        closeQueryWordCB.setChecked(Globle.closeQueryWord);
//
//        wordBookRL.setOnClickListener(this);
//        statisticRL.setOnClickListener(this);
//        collocatRL.setOnClickListener(this);
//        downloadWatchMangaRL.setOnClickListener(this);
//        checkUpdateRL.setOnClickListener(this);
//        feedbackRL.setOnClickListener(this);
//        aboutRL.setOnClickListener(this);
//    }
//
//    private void showOnlyOkDialog(String title, String msg) {
//        dialog.setTitle(title);
//        dialog.setMessage(msg);
//        dialog.show();
//    }
//
//    private void showQrcodeDialog() {
//        ImageView imgView = new ImageView(getContext());
//        imgView.setImageBitmap(ImageUtil.drawable2Bitmap(getResources().getDrawable(R.drawable.qrcode)));
//        new AlertDialog.Builder(getContext()).setTitle("扫描二维码或浏览器下载")
//                .setView(imgView).setPositiveButton("从浏览器下载", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                Uri uri = Uri.parse("http://yun.baidu.com/share/link?shareid=2205873540&uk=1548482320");
//                Intent downloadIntent = new Intent(Intent.ACTION_VIEW, uri);
//                startActivity(downloadIntent);
//            }
//        }).setNegativeButton("取消", null).
//                show();
//    }
//
//    @Override
//    public void onClick(View v) {
//        Intent intent;
//        switch (v.getId()) {
//            case R.id.word_book:
//                intent = new Intent(getActivity(), WordsBookActivity.class);
//                startActivity(intent);
//                break;
//            case R.id.statistic:
//                intent = new Intent(getActivity(), StatisticsActivity.class);
//                startActivity(intent);
//                break;
//            case R.id.collect:
//                ToastUtil.tipShort(getActivity(), "建设中...");
//                break;
//            case R.id.download_manga:
//                showQrcodeDialog();
//                break;
//            case R.id.check_update:
//                ToastUtil.tipShort(getActivity(), "等我发布第二版再说(因为我没有服务器,所以得等第一版在应用市场上架才行.)");
//                break;
//            case R.id.feedback:
//                showOnlyOkDialog("意见反馈", "有什么建议或意见可以直接发我邮箱\n" +
//                        "邮箱:772192594@qq.com");
//                break;
//            case R.id.about:
//                showOnlyOkDialog("关于", "本应用由苏航个人开发\n" + "版本号:v" + Globle.versionName + "\n" +
//                        "邮箱:772192594@qq.com");
//                break;
//        }
//    }
//
//
//}
