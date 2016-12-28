package com.xingfabu.direct.cache;

import android.content.Context;
import android.content.SharedPreferences;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;
/**
 * 本地sharedpreference缓存
 * Created by 郭平 on 2016/3/31 0031.
 */
public class SPCache {
    private static SPCache localCache;
    private SharedPreferences sp;

    private SPCache(Context context){
        sp = context.getSharedPreferences(context.getPackageName(),Context.MODE_PRIVATE);
    }
    public static SPCache getInstance(Context context){
        if(localCache == null){
            localCache = new SPCache(context);
        }
        return localCache;
    }

    public void saveToken(String token){
        sp.edit().putString("token",token).apply();
    }
    public String getToken(){
        return  sp.getString("token","");
    }

    public void saveUid(String uid){
        sp.edit().putString("uid",uid).apply();
    }
    public String getUid(){
        return  sp.getString("uid","");
    }

    public void saveTouristToken(String token){
        sp.edit().putString("touristToken",token).apply();
    }
    public String getTouristToken(){
        return  sp.getString("touristToken","");
    }
    public void savePhoneNum(String phoneNum){
        sp.edit().putString("phoneNum",phoneNum).apply();
    }
    public String getPhoneNum(){
        return sp.getString("phoneNum","");
    }
    public void saveRongToken(String token){
        sp.edit().putString("rongToken",token).apply();
    }
    public String getRongToken(){
        return sp.getString("rongToken","");
    }

    public void savePic(String pic){
        sp.edit().putString("pic",pic).apply();
    }
    public String getPic(){
        return sp.getString("pic","");
    }

    public void saveName(String name){
        sp.edit().putString("name",name).apply();
    }
    public String getName(){
        return sp.getString("name","");
    }

    public void saveRongUid(String uid){
        sp.edit().putString("ronguid",uid).apply();
    }
    public String getRongUid(){
        return sp.getString("ronguid","");
    }
    public void saveWiFiFlag(int flag){
        sp.edit().putInt("flag",flag).apply();
    }
    public int getWiFiFlag(){
        return sp.getInt("flag",1);
    }

    /**
     * 得到保存数据的方法，我们根据默认值得到保存的数据的具体类型，然后调用相对于的方法获取值
     */
    public Object get(String key, Object defaultObject) {

        if (defaultObject instanceof String) {
            return sp.getString(key, (String) defaultObject);
        } else if (defaultObject instanceof Integer) {
            return sp.getInt(key, (Integer) defaultObject);
        } else if (defaultObject instanceof Boolean) {
            return sp.getBoolean(key, (Boolean) defaultObject);
        } else if (defaultObject instanceof Float) {
            return sp.getFloat(key, (Float) defaultObject);
        } else if (defaultObject instanceof Long) {
            return sp.getLong(key, (Long) defaultObject);
        } else {
            return null;
        }
    }

    /**
     * 保存数据的方法，我们需要拿到保存数据的具体类型，然后根据类型调用不同的保存方法
     */
    public void putAndApply(String key, Object object) {
        SharedPreferences.Editor editor = sp.edit();

        if (object instanceof String) {
            editor.putString(key, (String) object).apply();
        } else if (object instanceof Integer) {
            editor.putInt(key, (Integer) object).apply();
        } else if (object instanceof Boolean) {
            editor.putBoolean(key, (Boolean) object).apply();
        } else if (object instanceof Float) {
            editor.putFloat(key, (Float) object).apply();
        } else if (object instanceof Long) {
            editor.putLong(key, (Long) object).apply();
        } else {
            editor.putString(key, object.toString()).apply();
        }
    }

    /**
     * 移除某个key值已经对应的值
     */
    public void remove(String key) {
        SharedPreferences.Editor editor = sp.edit();
        editor.remove(key);
        SharedPreferencesCompat.apply(editor);
    }

    /**
     * 清除所有数据
     */
    public void clear() {
        SharedPreferences.Editor editor = sp.edit();
        editor.clear();
        SharedPreferencesCompat.apply(editor);
    }

    /**
     * 查询某个key是否已经存在
     */
    public boolean contains(String key) {
        return sp.contains(key);
    }

    /**
     * 返回所有的键值对
     */
    public Map<String, ?> getAll() {
        return sp.getAll();
    }

    /**
     * 创建一个解决SharedPreferencesCompat.apply方法的一个兼容类
     * @author zhy
     */
    private static class SharedPreferencesCompat {

        private static final Method sApplyMethod = findApplyMethod();

        /**
         * 反射查找apply的方法
         */
        @SuppressWarnings({"unchecked", "rawtypes"})
        private static Method findApplyMethod() {
            try {
                Class clz = SharedPreferences.Editor.class;
                return clz.getMethod("apply");
            } catch (NoSuchMethodException e) {
            }

            return null;
        }

        /**
         * 如果找到则使用apply执行，否则使用commit
         */
        public static void apply(SharedPreferences.Editor editor) {
            try {
                if (sApplyMethod != null) {
                    sApplyMethod.invoke(editor);
                    return;
                }
            } catch (IllegalArgumentException expected) {
            } catch (IllegalAccessException expected) {
            } catch (InvocationTargetException expected) {
            }
            editor.commit();
        }
    }
}
