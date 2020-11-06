package com.warrior.hangsu.administrator.strangerbookreader.business.filechoose

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.content.Context
import com.warrior.hangsu.administrator.strangerbookreader.base.BaseViewModel
import com.warrior.hangsu.administrator.strangerbookreader.bean.FileBean

/**
 * Created by acorn on 2020/11/4.
 */
class FileViewModel : BaseViewModel() {
    private val fileModel = FileModel()
    private val fileList = MutableLiveData<ArrayList<FileBean>>()

    fun doGetFileList(path: String) {
        fileList.value = fileModel.getFileList(path)
    }

    fun getFileList(): LiveData<ArrayList<FileBean>> {
        return fileList
    }
}