/*
 * Copyright (C) 2012 Brandon Tate
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.warrior.hangsu.administrator.strangerbookreader.business.epub;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnLongClickListener;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.warrior.hangsu.administrator.strangerbookreader.listener.TextSelectionJavascriptInterfaceListener;
import com.warrior.hangsu.administrator.strangerbookreader.listener.TextSelectionListener;


/**
 * Webview subclass that hijacks web content selection.
 *
 * @author Brandon Tate
 */
public class TranslateWebView extends WebView implements OnLongClickListener, TextSelectionJavascriptInterfaceListener, View.OnTouchListener {
    /**
     * Context.
     */
    protected Context mContext;
    private String TAG = "TranslateWebView";
    /**
     * Javascript interface for catching text selection.
     */
    protected TextSelectionJavascriptInterface mTextSelectionJSInterface = null;
    private String lastURL = "";//用于判断是否已经注入
    private String lastURL1 = "";//用于只设置一遍颜色
    private OnWebViewLongClickListener onWebViewLongClickListener;
    private String urlTitle;

    public TranslateWebView(Context context) {
        super(context);
        mContext = context;
        init(context);
    }

    public TranslateWebView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        mContext = context;
        init(context);
    }

    public TranslateWebView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        init(context);
    }


    @Override
    public boolean onLongClick(View v) {
        WebView.HitTestResult result = ((WebView) v).getHitTestResult();
        int type = result.getType();
        if (type == WebView.HitTestResult.IMAGE_TYPE) {
            onWebViewLongClickListener.onImgLongClick(result.getExtra());
            return true;
        }
        Handler handler = new Handler();
        Runnable updateThread = new Runnable() {
            public void run() {
                //其实直接调用就好了
                loadUrl("javascript:longTouchSelected();");
            }

        };
        handler.postDelayed(updateThread, 0);
        // Tell the javascript to handle this if not in selection mode
//        loadUrl("javascript:android.selection.longTouch();");

        // Don't let the webview handle it
//        return true;
        //let the webview handle it
        return false;
    }


    /**
     * Setups up the web view.
     *
     * @param context
     */
    protected void init(Context context) {

        // On Touch Listener
        setOnLongClickListener(this);
        setOnTouchListener(this);

        // Webview init
        WebSettings webSettings = getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setJavaScriptCanOpenWindowsAutomatically(true);
        //插件状态
        webSettings.setPluginState(WebSettings.PluginState.ON);
        webSettings.setAllowFileAccess(true);// 允许通过网页上传文件
        webSettings.setBuiltInZoomControls(true);// 可缩放
        webSettings.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);// 优先使用缓存


        buildDrawingCache(true);
        setDrawingCacheEnabled(true);


        // Webview client.
        setWebViewClient(new WebViewClient() {
            // This is how it is supposed to work, so I'll leave it in, but this
            // doesn't get called on pinch
            // So for now I have to use deprecated getScale method.
            @Override
            public void onScaleChanged(WebView view, float oldScale,
                                       float newScale) {
                //这样会阻止缩放
                super.onScaleChanged(view, oldScale, newScale);
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                // 这样写是为了可以在webview中点击链接还继续在webview中显示,而不是打开浏览器
                loadUrl(url);
                return true;
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                // 一般实现以下这个方法,这个方法是这个网页结束后调用的
                super.onPageFinished(view, url);
                // 全部完成后注入效率太低 但是这是epub
                JSinject();
            }
        });

        //JavaScript回调接口
        mTextSelectionJSInterface = new TextSelectionJavascriptInterface(mContext, this);
        addJavascriptInterface(mTextSelectionJSInterface,
                mTextSelectionJSInterface.getInterfaceName());


        //隐藏缩放按钮
        getSettings().setDisplayZoomControls(false);
    }

    private void JSinject() {
        //以下是JavaScript注入的代码 目前是以直接注入text的方式注入的 也就是说是直接把方法以字符串的方式注入进去的,所以assets里的文件没用
        String js = "var newscript = document.createElement(\"script\");";
//                js += "newscript.src=\"file:///android_asset/android.selection.js\";";
//                js += "newscript.onload=function(){android.selection.longTouch();};";  //xxx()代表js中某方法
        js += "newscript.text =  function longTouchSelected(){           if(window.getSelection) {\n" +
                "        \t\t window.TextSelection.seletedWord(window.getSelection().toString());\n" +
                "            } else if(document.selection && document.selection.createRange) {\n" +
                "           \t\t  window.TextSelection.seletedWord(document.selection.createRange().text());\n" +
                "            }};";
        js += "document.body.appendChild(newscript);";
        loadUrl("javascript:" + js);
        //TODO 提示用户注入完成
//        ToastUtil.tipShort(mContext, "注入完成");
    }

    @Override
    public void tsjiJSError(String error) {
        Log.e(TAG, "JSError: " + error);
    }

    private void refresh() {
        lastURL = "";
        lastURL1 = "";
        reload();
    }


    public void setTextSelectionListener(TextSelectionListener textSelectionListener) {
        mTextSelectionJSInterface.setTextSelectionListener(textSelectionListener);
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        return false;
    }

    public void setOnWebViewLongClickListener(OnWebViewLongClickListener onWebViewLongClickListener) {
        this.onWebViewLongClickListener = onWebViewLongClickListener;
    }

    public interface OnWebViewLongClickListener {
        void onImgLongClick(String imgUrl);
    }
}
