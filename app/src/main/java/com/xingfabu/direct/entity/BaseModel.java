package com.xingfabu.direct.entity;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.Gson;

import java.lang.reflect.Type;

/**
 * Created by 郭平 on 2016/3/29 0029.
 */
public class BaseModel implements Parcelable{

    public static final Creator<BaseModel> CREATOR = new Creator<BaseModel>() {
        @Override
        public BaseModel createFromParcel(Parcel in) {
            String classname = in.readString();
            String json = in.readString();
            BaseModel data = null;
            Class<?> forName = null;
            try {
                forName = Class.forName(classname);
                data = (BaseModel)fromJson(json, forName);
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }

            return data;
        }

        @Override
        public BaseModel[] newArray(int size) {
            return new BaseModel[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.getClass().getName());
        dest.writeString(this.toJson());
    }
    public String toJson(){
        return new Gson().toJson(this);
    }
    public static BaseModel fromJson(String json, Type typeOfT){
        return new Gson().fromJson(json, typeOfT);
    }

    public static <T> T fromJson(String json, Class<T> classOfT){
        return new Gson().fromJson(json, classOfT);
    }
}
