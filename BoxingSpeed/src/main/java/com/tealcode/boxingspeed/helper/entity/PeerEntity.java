package com.tealcode.boxingspeed.helper.entity;

/**
 * Created by YuBo on 2017/9/29.
 */

// 基本用户数据，包括：用户名，昵称，性别，年龄，头像链接
public class PeerEntity {
    protected int userid;
    protected String username;
    protected String nickname;
    protected String gender;
    protected int age;
    protected String avatar_url;

    public int getUserId() {
        return userid;
    }

    public void setUserId(int id) {
        this.userid = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getAvatarUrl() {
        return avatar_url;
    }

    public void setAvatarUrl(String url) {
        this.avatar_url = url;
    }
}
