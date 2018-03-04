package com.warrior.hangsu.administrator.strangerbookreader.business.epub;

import android.content.Context;
import android.os.Handler;
import android.webkit.JavascriptInterface;

import com.warrior.hangsu.administrator.strangerbookreader.listener.TextSelectionJavascriptInterfaceListener;
import com.warrior.hangsu.administrator.strangerbookreader.listener.TextSelectionListener;

/**
 * This javascript interface allows the page to communicate that text has been
 * selected by the user.
 * <p/>
 * 这个类是java和JavaScript的桥梁,JavaScript中可以调用这个类中的方法 达到两种语言通讯的效果
 *
 * @author btate
 */
public class TextSelectionJavascriptInterface {

    /**
     * The TAG for logging.
     */
    private static final String TAG = "TextSelectionJavascriptInterface";

    /**
     * The javascript interface name for adding to web view.
     */
    private final String interfaceName = "TextSelection";

    /**
     * The webview to work with.
     */
    private TextSelectionJavascriptInterfaceListener mListener;
    private TextSelectionListener textSelectionListener;

    /**
     * The context.
     */
    Context mContext;

    // Need handler for callbacks to the UI thread
    final Handler mHandler = new Handler();

    /**
     * Constructor accepting context.
     *
     * @param c
     */
    public TextSelectionJavascriptInterface(Context c) {
        this.mContext = c;
    }

    /**
     * Constructor accepting context and mListener.
     *
     * @param c
     * @param mListener
     */
    public TextSelectionJavascriptInterface(Context c,
                                            TextSelectionJavascriptInterfaceListener mListener) {
        this.mContext = c;
        this.mListener = mListener;
    }

    /**
     * Handles javascript errors.
     * <p/>
     * 如果您在编写HTML5应用，需要在JS代码中访问Java中的函数，则您会用到WebView的addJavascriptInterface()函数。
     * 因为安全问题，在Android4.2中(如果应用的android:targetSdkVersion数值为17+)JS只能访问带有
     *
     * @param error
     * @JavascriptInterface注解的Java函数。 之前，任何Public的函数都可以在JS代码中访问，而Java对象继承关系会导致很多Public的函数都可以在JS中访问，其中一个重要的函数就是
     * getClass()。然后JS可以通过反射来访问其他一些内容。通过引入 @JavascriptInterface注解，则在JS中只能访问
     * @JavascriptInterface注解的函数。这样就可以增强安全性。 如果您的应用android:targetSdkVersion数值为17或者大于17记得添加 @JavascriptInterface 注解。
     */
    @JavascriptInterface
    public void jsError(final String error) {
        if (this.mListener != null) {

            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    mListener.tsjiJSError(error);
                }
            });
        }
    }

    /**
     * 在JavaScript中调用该方法告诉java已经获取到选择的单词
     *
     * @param msg
     */
    @JavascriptInterface
    public void seletedWord(final String msg) {
        if (this.textSelectionListener != null) {
            mHandler.post(new Runnable() {
                @Override
                public void run() {
//                    Toast.makeText(mContext, msg,
//                            Toast.LENGTH_LONG).show();
                    
                    textSelectionListener.seletedWord(msg);
                }
            });
        }
    }

    /**
     * Gets the interface name
     *
     * @return
     */
    @JavascriptInterface
    public String getInterfaceName() {
        return this.interfaceName;
    }

    public void setTextSelectionListener(TextSelectionListener textSelectionListener) {
        this.textSelectionListener = textSelectionListener;
    }
}
