package com.warrior.hangsu.administrator.strangerbookreader.business.filechoose

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.os.Bundle
import android.os.Environment
import android.support.v7.widget.LinearLayoutManager
import android.text.TextUtils
import android.view.View
import com.warrior.hangsu.administrator.strangerbookreader.R
import com.warrior.hangsu.administrator.strangerbookreader.adapter.FileChooseAdapter
import com.warrior.hangsu.administrator.strangerbookreader.adapter.PathHistoryAdapter
import com.warrior.hangsu.administrator.strangerbookreader.bean.FileBean
import com.warrior.hangsu.administrator.strangerbookreader.business.filechoose.FileChooseActivity.SelectAllState
import com.warrior.hangsu.administrator.strangerbookreader.listener.OnFolderClickListener
import com.warrior.hangsu.administrator.strangerbookreader.utils.BaseActivity
import com.warrior.hangsu.administrator.strangerbookreader.widget.bar.TopBar.OnTopBarClickListener
import kotlinx.android.synthetic.main.activity_file_chooser.*
import kotlinx.android.synthetic.main.activity_file_chooser.file_rcv
import kotlinx.android.synthetic.main.activity_file_chooser.file_size_tv
import kotlinx.android.synthetic.main.activity_file_chooser.ok_btn
import kotlinx.android.synthetic.main.activity_file_manager.*
import java.io.File
import java.lang.IndexOutOfBoundsException
import java.lang.StringBuilder

/**
 * Created by acorn on 2020/11/4.
 */
class FileManagerActivity : BaseActivity(), View.OnClickListener {
    private lateinit var fileViewModel: FileViewModel
    private var mAdapter: FileChooseAdapter? = FileChooseAdapter(this)
    private var mPathHistoryAdapter = PathHistoryAdapter(this)
    private var pathList: List<String>? = null
    private var fileList: ArrayList<FileBean>? = null
    private var mSelectAllState = SelectAllState.CANCEL_ALL

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initUI()
        initVM()
        fileViewModel.doGetFileList(Environment.getExternalStorageDirectory().absolutePath)
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

    private fun initUI() {
        file_rcv.layoutManager = LinearLayoutManager(this)
        file_rcv.isFocusableInTouchMode = false
        file_rcv.isFocusable = false
        file_rcv.setHasFixedSize(true)
        file_rcv.adapter = mAdapter
        path_rcv.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        path_rcv.isFocusableInTouchMode = false
        path_rcv.isFocusable = false
        path_rcv.setHasFixedSize(true)
        path_rcv.adapter = mPathHistoryAdapter

        ok_btn.setOnClickListener(this)
        baseTopBar.title = "选择书籍"
        baseTopBar.setRightText("全选")
        baseTopBar.setOnTopBarClickListener(object : OnTopBarClickListener {
            override fun onLeftClick() {
                finish()
            }

            override fun onRightClick() {
                when (mSelectAllState) {
                    SelectAllState.CANCEL_ALL -> {
                        mAdapter!!.selectAll()
                        mSelectAllState = SelectAllState.SELECT_ALL
                        baseTopBar.setRightText("取消全选")
                    }
                    SelectAllState.SELECT_ALL -> {
                        mAdapter!!.removeAllSelected()
                        mSelectAllState = SelectAllState.CANCEL_ALL
                        baseTopBar.setRightText("全选")
                    }
                }
            }

            override fun onTitleClick() {}
        })
    }

    private fun initRec() {
        try {
            mAdapter?.list = fileList
            mAdapter?.notifyDataSetChanged()
            mAdapter?.setOnFolderClickListener(object : OnFolderClickListener {
                override fun onClick(path: String?) {
                    if (!TextUtils.isEmpty(path)) {
                        fileViewModel.doGetFileList(path!!)
                    }
                }
            })
            file_size_tv.text = mAdapter?.bookCount.toString()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun initPathRec() {
        try {
            mPathHistoryAdapter.list = pathList
            mPathHistoryAdapter.notifyDataSetChanged()
            mPathHistoryAdapter.setOnRecycleItemClickListener {
                fileViewModel.doGetFileList(assemblePath(it))
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onBackPressed() {
        if (pathList?.size!! > 1) {
            fileViewModel.doGetFileList(assemblePath(pathList?.size!! - 2))
            return
        }
        super.onBackPressed()
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

    override fun getLayoutId(): Int {
        return R.layout.activity_file_manager
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.ok_btn -> {
                val result = mAdapter!!.selectedList
                val intent = Intent()
                intent.putExtra("selectedList", result)
                setResult(RESULT_OK, intent)
                finish()
            }
        }
    }
}