package com.warrior.hangsu.administrator.strangerbookreader.business.filechoose

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.content.Context
import android.os.Environment
import com.warrior.hangsu.administrator.strangerbookreader.base.BaseViewModel
import com.warrior.hangsu.administrator.strangerbookreader.bean.FileBean
import java.io.File

/**
 * Created by acorn on 2020/11/4.
 */
class FileViewModel : BaseViewModel() {
    private val fileModel = FileModel()
    private val fileList = MutableLiveData<ArrayList<FileBean>>()
    private val pathList = MutableLiveData<List<String>>()

    fun doGetFileList(path: String) {
        fileList.value = fileModel.getFileList(path)
        pathList.value = path.replace(Environment.getExternalStorageDirectory().absolutePath,"external storage")
                .split(File.separator)
    }

    fun getFileList(): LiveData<ArrayList<FileBean>> {
        return fileList
    }

    fun getPathList(): LiveData<List<String>> {
        return pathList
    }
}