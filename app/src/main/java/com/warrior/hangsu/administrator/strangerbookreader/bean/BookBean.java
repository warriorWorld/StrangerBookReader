package com.warrior.hangsu.administrator.strangerbookreader.bean;

import android.text.TextUtils;

import java.util.ArrayList;

/**
 * Created by Administrator on 2016/4/3.
 */
public class BookBean extends BaseBean{
    private String path;
    private String bpPath;
    private String name;
    private float progress;
    private String format;
    private String author;
    private String rate;
    private String introduction;
    private String language;
    private String chapters;//章节数量
    private String words;//字数
    private String updateDate;
    private String publishDate;
    private ArrayList<ChapterListBean> chapterList;
    private String spider;

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getRate() {
        return rate;
    }

    public void setRate(String rate) {
        this.rate = rate;
    }

    public String getIntroduction() {
        return introduction;
    }

    public void setIntroduction(String introduction) {
        this.introduction = introduction;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getChapters() {
        return chapters;
    }

    public void setChapters(String chapters) {
        this.chapters = chapters;
    }

    public String getWords() {
        return words;
    }

    public void setWords(String words) {
        this.words = words;
    }

    public String getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(String updateDate) {
        this.updateDate = updateDate;
    }

    public String getPublishDate() {
        return publishDate;
    }

    public void setPublishDate(String publishDate) {
        this.publishDate = publishDate;
    }

    public String getBpPath() {
        if (TextUtils.isEmpty(bpPath)) {
            return "";
        } else {
            return bpPath;
        }
    }

    public void setBpPath(String bpPath) {
        this.bpPath = bpPath;
    }


    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public float getProgress() {
        return progress;
    }

    public void setProgress(float progress) {
        this.progress = progress;
    }

    public String getFormat() {
        return format;
    }

    public void setFormat(String format) {
        this.format = format;
    }

    public ArrayList<ChapterListBean> getChapterList() {
        return chapterList;
    }

    public void setChapterList(ArrayList<ChapterListBean> chapterList) {
        this.chapterList = chapterList;
    }

    public String getSpider() {
        return spider;
    }

    public void setSpider(String spider) {
        this.spider = spider;
    }
}
