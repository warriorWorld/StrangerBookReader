package com.warrior.hangsu.administrator.strangerbookreader.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class DbHelper extends SQLiteOpenHelper {
    private Context xcontext;

    public static final String BOOKS_TABLE = "create table if not exists BooksTable ("
            + "id integer primary key autoincrement,"
            + "path text," + "name text," + "progress float," + "format text,"
            + "bpPath text)";
    public static final String DATA_STATISTICS = "create table if not exists STATISTICS ("
            + "id integer primary key autoincrement,"
            + "date_start text," + "date_end text," + "type text," + "query_word_c integer," + "word_c integer,"
            + "book_name text)";
    public static final String WORDS_BOOK = "create table if not exists WordsBook ("
            + "id integer primary key autoincrement,"
            + "word text," + "time integer," + "createdtime TimeStamp NOT NULL DEFAULT (datetime('now','localtime')))";
    public static final String COLLECT = "create table if not exists COLLECT ("
            + "id integer primary key autoincrement,"
            + "date text," + "link text," + "title text," + "titleBpPath text,"
            + "describe text)";

    public DbHelper(Context context, String name, CursorFactory factory,
                    int version) {
        super(context, name, factory, version);
        xcontext = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(BOOKS_TABLE);
        db.execSQL(DATA_STATISTICS);
        db.execSQL(WORDS_BOOK);
        db.execSQL(COLLECT);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(BOOKS_TABLE);
        db.execSQL(DATA_STATISTICS);
    }

}
