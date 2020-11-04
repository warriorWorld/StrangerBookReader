package com.warrior.hangsu.administrator.strangerbookreader.business.filechoose

import android.arch.lifecycle.MutableLiveData
import android.content.Context
import com.warrior.hangsu.administrator.strangerbookreader.base.BaseViewModel
import com.warrior.hangsu.administrator.strangerbookreader.bean.FileBean

/**
 * Created by acorn on 2020/11/4.
 */
class FileViewModel : BaseViewModel() {
    private lateinit var mContext: Context
    private val fileList=MutableLiveData<FileBean>()

    fun FileViewModel(context: Context) {
        mContext = context.applicationContext
    }

    fun doGetFileList(){

    }
}