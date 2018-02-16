package com.warrior.hangsu.administrator.strangerbookreader.db;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.warrior.hangsu.administrator.strangerbookreader.bean.BookBean;
import com.warrior.hangsu.administrator.strangerbookreader.business.statistic.StatisticsBean;
import com.warrior.hangsu.administrator.strangerbookreader.configure.Globle;
import com.warrior.hangsu.administrator.strangerbookreader.business.wordsbook.WordsBookBean;

import java.util.ArrayList;


public class DbAdapter {
    public static final String DB_NAME = "books.db";
    private DbHelper dbHelper;
    private SQLiteDatabase db;

    public DbAdapter(Context context) {
        dbHelper = new DbHelper(context, DB_NAME, null, Globle.DB_VERSION);
        db = dbHelper.getWritableDatabase();
    }

    /**
     * 插入一条书籍信息
     */
    public void insertBooksTableTb(String path, String bookName, int progress,
                                   String bpPath) {
        if (queryadded(bookName)) {
            return;
        }
        db.execSQL(
                "insert into BooksTable (path,name,progress,bpPath) values (?,?,?,?)",
                new Object[]{path, bookName, progress, bpPath});
    }

    /**
     * 插入一条生词信息
     */
    public void insertWordsBookTb(String word) {
        int time = queryQueryedTime(word);
        if (time > 0) {
            //如果查过这个单词 那就update 并且time+1
            time++;
            updateTimeTOWordsBook(word, time);
        } else {
            db.execSQL(
                    "insert into WordsBook (word,time) values (?,?)",
                    new Object[]{word, 1});
        }
    }

    /**
     * 插入一条统计信息
     */
    public void insertStatiscticsTb(String dateStart, String dateEnd, String type, int query_word_c, int word_c, String book_name) {
//        if (queryStatisticsed(book_name)) {
//            updateStatistics(dateEnd, query_word_c, book_name);
//        } else {
        db.execSQL(
                "insert into STATISTICS (date_start,date_end,type,query_word_c,word_c,book_name) values (?,?,?,?,?,?)",
                new Object[]{dateStart, dateEnd, type, query_word_c, word_c, book_name});
//        }
    }

    /**
     * 更新进度信息
     *
     * @param name
     */
    public void updateProgressTOBooksTb(String name, int progress) {
        db.execSQL("update BooksTable set progress=? where name=?",
                new Object[]{progress, name});
    }

    /**
     * 更新生词信息
     */
    public void updateTimeTOWordsBook(String word, int time) {
        db.execSQL("update WordsBook set time=? where word=?",
                new Object[]{time, word});
    }

    /**
     * 更新生词信息
     */
    public void updateStatistics(String date_end, int query_word_c, String book_name) {
        db.execSQL("update STATISTICS set date_end=?,query_word_c=? where book_name=?",
                new Object[]{date_end, query_word_c, book_name});
    }


    /**
     * 查询所有书籍
     *
     * @return
     */
    public ArrayList<BookBean> queryAllBooks() {
        ArrayList<BookBean> resBeans = new ArrayList<BookBean>();
        Cursor cursor = db
                .query("BooksTable", null, null, null, null, null, null);

        while (cursor.moveToNext()) {
            String name = cursor.getString(cursor.getColumnIndex("name"));
            String bpPath = cursor
                    .getString(cursor.getColumnIndex("bpPath"));
            String path = cursor
                    .getString(cursor.getColumnIndex("path"));
            int progress = cursor
                    .getInt(cursor.getColumnIndex("progress"));
            BookBean item = new BookBean();
            item.setPath(path);
            item.setBpPath(bpPath);
            item.setName(name);
            item.setProgress(progress);
            resBeans.add(item);
        }
        cursor.close();
        return resBeans;
    }

    /**
     * 查询所有生词
     *
     * @return
     */
    public ArrayList<WordsBookBean> queryAllWordsBook() {
        ArrayList<WordsBookBean> resBeans = new ArrayList<WordsBookBean>();
        Cursor cursor = db
                .query("WordsBook", null, null, null, null, null, "createdtime desc");

        while (cursor.moveToNext()) {
            String word = cursor.getString(cursor.getColumnIndex("word"));
            int time = cursor
                    .getInt(cursor.getColumnIndex("time"));
            WordsBookBean item = new WordsBookBean();
            item.setWord(word);
            item.setTime(time);
            resBeans.add(item);
        }
        cursor.close();
        return resBeans;
    }

    /**
     * 查询是否已经添加过
     */
    public boolean queryadded(String bookName) {
        Cursor cursor = db.rawQuery(
                "select name from BooksTable where name=?",
                new String[]{bookName});
        int count = cursor.getCount();
        cursor.close();
        if (count > 0) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 查询统计数据是否已经添加过
     */
    public int queryStatisticsed(String bookName) {
        int res = 0;
        Cursor cursor = db.rawQuery(
                "select * from STATISTICS where book_name=?",
                new String[]{bookName});
        int count = cursor.getCount();
        if (count > 0) {
            while (cursor.moveToNext()) {
                res = cursor.getInt(cursor.getColumnIndex("query_word_c"));
            }
        }
        cursor.close();
        return res;
    }

    /**
     * 查询是否查询过
     */
    public boolean queryQueryed(String word) {
        Cursor cursor = db.rawQuery(
                "select word from WordsBook where word=?",
                new String[]{word});
        int count = cursor.getCount();
        cursor.close();
        if (count > 0) {
            return true;
        } else {
            return false;
        }
    }

    public int queryQueryedTime(String word) {
        int res = 0;
        Cursor cursor = db.rawQuery(
                "select time from WordsBook where word=?",
                new String[]{word});
        int count = cursor.getCount();
        if (count > 0) {
            while (cursor.moveToNext()) {
                res = cursor.getInt(cursor.getColumnIndex("time"));
            }
        }
        cursor.close();
        return res;
    }

    /**
     * 查询统计数据
     */
    public ArrayList<StatisticsBean> queryAllStatistic() {
        ArrayList<StatisticsBean> list = new ArrayList<StatisticsBean>();
        Cursor cursor = db
                .query("STATISTICS", null, null, null, null, null, null);
        while (cursor.moveToNext()) {
            StatisticsBean item = new StatisticsBean();
            String bookName = cursor.getString(cursor
                    .getColumnIndex("book_name"));
            String date_start = cursor.getString(cursor
                    .getColumnIndex("date_start"));
            String date_end = cursor.getString(cursor
                    .getColumnIndex("date_end"));
            String type = cursor.getString(cursor
                    .getColumnIndex("type"));
            int query_word_c = cursor.getInt(cursor.getColumnIndex("query_word_c"));
            int word_c = cursor.getInt(cursor.getColumnIndex("word_c"));
            item.setBook_name(bookName);
            item.setType(type);
            item.setDateStart(date_start);
            item.setDateEnd(date_end);
            item.setQuery_word_c(query_word_c);
            item.setWord_c(word_c);
            list.add(item);
        }
        cursor.close();
        return list;
    }


    /**
     * 删除书籍
     */
    public void deleteBookByBookName(String bookName) {

        db.execSQL("delete from BooksTable where name=?",
                new Object[]{bookName});

    }

    /**
     * 删除生词
     */
    public void deleteWordByWord(String word) {
        db.execSQL("delete from WordsBook where word=?",
                new Object[]{word});
    }

    /**
     * 根据书籍名称删除统计数据
     */
    public void deleteStatiscticsByBookName(String bookName) {
        db.execSQL("delete from STATISTICS where book_name=?",
                new Object[]{bookName});
    }

    public void closeDb() {
        if (null != db) {
            db.close();
        }
    }
}
