package com.warrior.hangsu.administrator.strangerbookreader.business.filechoose

import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import com.warrior.hangsu.administrator.strangerbookreader.R
import com.warrior.hangsu.administrator.strangerbookreader.utils.BaseActivity
import kotlinx.android.synthetic.main.activity_file_chooser.*

/**
 * Created by acorn on 2020/11/4.
 */
class FileManagerActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initUI()
    }

    fun initUI() {
        file_rcv.layoutManager = LinearLayoutManager(this)
        file_rcv.isFocusableInTouchMode = false
        file_rcv.isFocusable = false
        file_rcv.setHasFixedSize(true)
        baseTopBar.title="选择书籍"
        baseTopBar.setRightText("全选")

    }

    override fun getLayoutId(): Int {
        return R.layout.activity_file_manager
    }
}