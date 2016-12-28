package com.xingfabu.direct.entity;

/**
 * Created by guoping on 16/4/6.
 */
public class Video {
    public String sid;
    public String subject;
    public String webinar_id;//微吼id
    public String pic;
    public String webinar_type;//1直播2预约3结束4录播
    public String time;
    public String start_time;//直播开始的时间 到秒
    public String hits;
    public String user_pic;
    public String add_name;
    public String top;
    public int count_down;//预告算好的时间

    public String share_num;

    public String getSid() {
        return sid;
    }

    public String getSubject() {
        return subject;
    }

    public String getWebinar_id() {
        return webinar_id;
    }

    public String getPic() {
        return pic;
    }

    public String getWebinar_type() {
        return webinar_type;
    }

    public String getTime() {
        return time;
    }

    public String getStart_time() {
        return start_time;
    }

    public String getHits() {
        return hits;
    }

    public String getUser_pic() {
        return user_pic;
    }

    public String getAdd_name() {
        return add_name;
    }

    public String getTop() {
        return top;
    }

    public int getCount_down() {
        return count_down;
    }

    public void setSid(String sid) {
        this.sid = sid;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public void setWebinar_id(String webinar_id) {
        this.webinar_id = webinar_id;
    }

    public void setPic(String pic) {
        this.pic = pic;
    }

    public void setWebinar_type(String webinar_type) {
        this.webinar_type = webinar_type;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public void setStart_time(String start_time) {
        this.start_time = start_time;
    }

    public void setHits(String hits) {
        this.hits = hits;
    }

    public void setUser_pic(String user_pic) {
        this.user_pic = user_pic;
    }

    public void setAdd_name(String add_name) {
        this.add_name = add_name;
    }

    public void setTop(String top) {
        this.top = top;
    }

    public void setCount_down(int count_down) {
        this.count_down = count_down;
    }
}
