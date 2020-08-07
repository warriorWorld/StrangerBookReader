package com.warrior.hangsu.administrator.strangerbookreader.bean;

import android.os.Parcel;
import android.os.Parcelable;

public class FileBean implements Parcelable {
    /**
     * 文件的路径
     */
    public String path;
    /**
     * 文件图片资源的id，drawable或mipmap文件中已经存放doc、xml、xls等文件的图片
     */
    public int iconId;
    public String name;
    public long modifiedDate;

    public FileBean(String path, String name, int iconId, long modifiedDate) {
        this.path = path;
        this.name = name;
        this.iconId = iconId;
        this.modifiedDate = modifiedDate;
    }

    protected FileBean(Parcel in) {
        path = in.readString();
        iconId = in.readInt();
        name = in.readString();
        modifiedDate = in.readLong();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(path);
        dest.writeInt(iconId);
        dest.writeString(name);
        dest.writeLong(modifiedDate);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<FileBean> CREATOR = new Creator<FileBean>() {
        @Override
        public FileBean createFromParcel(Parcel in) {
            return new FileBean(in);
        }

        @Override
        public FileBean[] newArray(int size) {
            return new FileBean[size];
        }
    };
}
