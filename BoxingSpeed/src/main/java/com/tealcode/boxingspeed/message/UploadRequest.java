package com.tealcode.boxingspeed.message;

/**
 * Created by YuBo on 2017/10/24.
 */

public class UploadRequest {
    private int userId;
    private String type;
    private String image;

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
