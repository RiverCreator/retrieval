package com.example.retrieval2.Item;

import java.net.MalformedURLException;
import java.net.URL;

public class item {
    private Integer id;
    private String url1;
    private String name;
    private String pic_url;
    private double price;
    public item(int id , String name , String url1,String pic_url,double price) throws MalformedURLException {
        this.id=id;
        this.name=name;
        this.url1=url1;
        this.pic_url=pic_url;
        this.price=price;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public void setUrl1(String url1) {
        this.url1 = url1;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPic_url(String pic_url) {
        this.pic_url = pic_url;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public Integer getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getPic_url() {
        return pic_url;
    }

    public String getUrl1() {
        return url1;
    }

    public double getPrice() {return price; }
}
