package com.warrior.hangsu.administrator.strangerbookreader.business.filechoose

import com.warrior.hangsu.administrator.strangerbookreader.bean.FileBean

/**
 * Created by acorn on 2020/11/4.
 */
interface IFileModel {
    fun getFileList(path:String):List<FileBean>
}