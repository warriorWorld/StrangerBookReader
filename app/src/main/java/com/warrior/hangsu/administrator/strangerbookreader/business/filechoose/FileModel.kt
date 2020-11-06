package com.warrior.hangsu.administrator.strangerbookreader.business.filechoose

import com.warrior.hangsu.administrator.strangerbookreader.R
import com.warrior.hangsu.administrator.strangerbookreader.bean.FileBean
import com.warrior.hangsu.administrator.strangerbookreader.enums.FileType
import com.warrior.hangsu.administrator.strangerbookreader.utils.FileUtils
import java.io.File
import java.io.FileNotFoundException
import java.lang.Exception

/**
 * Created by acorn on 2020/11/5.
 */
class FileModel : IFileModel {
    override fun getFileList(path: String): List<FileBean> {
        val f = File(path) //第一级目录 reptile
        if (!f.exists()) {
            throw FileNotFoundException("$path not found!!!")
        }
        if (!f.isDirectory) {
            throw Exception("$path is not a directory!!!")
        }
        val result = ArrayList<FileBean>()
        val files = f.listFiles()
        for (file in files) {
            if (file.isDirectory) {
                val directory = FileBean(file.absolutePath, file.name, R.drawable.ic_directory,
                        0, FileType.FOLDER)
                result.add(directory)
                continue
            }
            if (FileUtils.isValidBookFile(file.path)){
                
            }
        }
        return result
    }
}