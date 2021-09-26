package com.warrior.hangsu.administrator.strangerbookreader.business.main

import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.os.Bundle
import android.os.Environment
import android.support.v7.widget.LinearLayoutManager
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.warrior.hangsu.administrator.strangerbookreader.R
import com.warrior.hangsu.administrator.strangerbookreader.adapter.FileAdapter
import com.warrior.hangsu.administrator.strangerbookreader.adapter.PathHistoryAdapter
import com.warrior.hangsu.administrator.strangerbookreader.base.BaseFragment
import com.warrior.hangsu.administrator.strangerbookreader.bean.FileBean
import com.warrior.hangsu.administrator.strangerbookreader.business.epub.EpubActivity
import com.warrior.hangsu.administrator.strangerbookreader.business.filechoose.FileViewModel
import com.warrior.hangsu.administrator.strangerbookreader.business.pdf.PdfActivity
import com.warrior.hangsu.administrator.strangerbookreader.business.read.NewReadActivity
import com.warrior.hangsu.administrator.strangerbookreader.configure.ShareKeys
import com.warrior.hangsu.administrator.strangerbookreader.listener.OnFolderClickListener
import com.warrior.hangsu.administrator.strangerbookreader.utils.SharedPreferencesUtils
import kotlinx.android.synthetic.main.activity_file_chooser.file_rcv
import kotlinx.android.synthetic.main.activity_file_chooser.file_size_tv
import kotlinx.android.synthetic.main.activity_file_manager.*
import java.io.File

/**
 * Created by acorn on 2020/11/4.
 */
class FileManagerFragment : BaseFragment() {
    private lateinit var fileViewModel: FileViewModel
    private var mAdapter: FileAdapter? = null
    private var mPathHistoryAdapter: PathHistoryAdapter? = null
    private var pathList: List<String>? = null
    private var fileList: ArrayList<FileBean>? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        return inflater.inflate(R.layout.fragment_file_manager, container, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initUI()
        initVM()
        var path = SharedPreferencesUtils.getSharedPreferencesData(activity, ShareKeys.LAST_PATH_KEY)
        if (TextUtils.isEmpty(path)) {
            path = Environment.getExternalStorageDirectory().absolutePath
        }
        fileViewModel.doGetFileList(path)
    }

    private fun initVM() {
        fileViewModel = ViewModelProviders.of(this).get(FileViewModel::class.java)
        fileViewModel.getFileList().observe(this, object : android.arch.lifecycle.Observer<ArrayList<FileBean>> {

            override fun onChanged(t: ArrayList<FileBean>?) {
                fileList = t
                initRec()
            }
        })
        fileViewModel.getPathList().observe(this, object : android.arch.lifecycle.Observer<List<String>> {
            override fun onChanged(t: List<String>?) {
                pathList = t
                initPathRec()
            }
        })
    }

    override fun onResume() {
        super.onResume()
        mAdapter?.setLastReadPosition(SharedPreferencesUtils.getIntSharedPreferencesData(context, ShareKeys.LAST_READ_KEY))
        initRec()
    }

    private fun initUI() {
        mAdapter = FileAdapter(activity)
        mPathHistoryAdapter = PathHistoryAdapter(activity)
        file_rcv.layoutManager = LinearLayoutManager(activity)
        file_rcv.isFocusableInTouchMode = false
        file_rcv.isFocusable = false
        file_rcv.setHasFixedSize(true)
        file_rcv.adapter = mAdapter
        path_rcv.layoutManager = LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)
        path_rcv.isFocusableInTouchMode = false
        path_rcv.isFocusable = false
        path_rcv.setHasFixedSize(true)
        path_rcv.adapter = mPathHistoryAdapter
    }

    private fun initRec() {
        try {
            mAdapter?.list = fileList
            mAdapter?.notifyDataSetChanged()
            mAdapter?.setOnFolderClickListener(object : OnFolderClickListener {
                override fun onClick(path: String?) {
                    if (!TextUtils.isEmpty(path)) {
                        SharedPreferencesUtils.setSharedPreferencesData(activity, ShareKeys.LAST_PATH_KEY, path)
                        fileViewModel.doGetFileList(path!!)
                    }
                }
            })
            mAdapter?.setOnRecycleItemClickListener {
                val path = fileList?.get(it)?.path
                if (TextUtils.isEmpty(path)) {
                    return@setOnRecycleItemClickListener
                }
                SharedPreferencesUtils.setSharedPreferencesData(activity, ShareKeys.LAST_READ_KEY, it)
                var format = ""
                if (path!!.endsWith(".txt") || path.endsWith(".TXT")) {
                    format = "TXT"
                } else if (path.endsWith(".pdf") || path.endsWith(".PDF")) {
                    format = "PDF"
                } else if (path.endsWith(".epub") || path.endsWith(".EPUB")) {
                    format = "EPUB"
                }
                var intent = Intent(activity, NewReadActivity::class.java)
                if (format == "EPUB") {
                    intent = Intent(activity, EpubActivity::class.java)
                } else if (format == "PDF") {
                    intent = Intent(activity, PdfActivity::class.java)
                }
                intent.putExtra("bookName", fileList?.get(it)?.name)
                intent.putExtra("bookPath", fileList?.get(it)?.path)
                intent.putExtra("bookFormat", format)
                startActivity(intent)
            }
            file_size_tv.text = "${SharedPreferencesUtils.getIntSharedPreferencesData(context, ShareKeys.LAST_READ_KEY)}/${mAdapter?.bookCount}"
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun initPathRec() {
        try {
            mPathHistoryAdapter?.list = pathList
            mPathHistoryAdapter?.notifyDataSetChanged()
            mPathHistoryAdapter?.setOnRecycleItemClickListener {
                SharedPreferencesUtils.setSharedPreferencesData(activity, ShareKeys.LAST_PATH_KEY, assemblePath(it))
                fileViewModel.doGetFileList(assemblePath(it))
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun assemblePath(index: Int): String {
        if (index < 0) {
            throw IndexOutOfBoundsException()
        }
        val result = StringBuilder()
        result.append(Environment.getExternalStorageDirectory().absolutePath)
        if (index == 0) {
            return result.toString()
        }
        result.append(File.separator)
        for (i in 1..index) {
            result.append(pathList?.get(i))
            if (i != index) {
                result.append(File.separator)
            }
        }
        return result.toString()
    }
}