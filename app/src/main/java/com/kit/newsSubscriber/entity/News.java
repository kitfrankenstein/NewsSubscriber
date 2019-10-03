package com.kit.newsSubscriber.entity;

/**
 * 使用一个类封装数据
 */

public class News {
    private String newsTitle;
    private String newsUrl;
    private String desc;
    private String newsTime;
    private String newsImage;

    public News(){

    }

    public News(String newsTitle, String newsUrl, String desc, String newsTime, String newsImage) {
        this.desc = desc;
        this.newsTime = newsTime;
        this.newsTitle = newsTitle;
        this.newsUrl = newsUrl;
        this.newsImage = newsImage;
    }


    public String getNewsTitle() {
        return newsTitle;
    }

    public void setNewsTitle(String newsTitle) {
        this.newsTitle = newsTitle;
    }

    public String getNewsUrl() {
        return newsUrl;
    }

    public void setNewsUrl(String newsUrl) {
        this.newsUrl = newsUrl;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getNewsTime() {
        return newsTime;
    }

    public void setNewsTime(String newsTime) {
        this.newsTime = newsTime;
    }

    public String getNewsImage() {
        return newsImage;
    }

    public void setNewsImage(String newsImage) {
        this.newsImage = newsImage;
    }
}
