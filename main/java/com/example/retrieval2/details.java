package com.example.retrieval2;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextClock;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;

public class details extends AppCompatActivity {
    private ImageView itemImg;
    private TextView name;
    private TextView item_url;
    private TextView price_show;
    private String item_name;
    private String pic_url;
    private String item_urls;
    private double price;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.details);
        itemImg=this.findViewById(R.id.item_img);
        name=this.findViewById(R.id.name);
        item_url=this.findViewById(R.id.url);
        price_show=findViewById(R.id.price);
        Intent intent=getIntent();
        item_name = intent.getStringExtra("name");
        pic_url=intent.getStringExtra("img_url");
        item_urls=intent.getStringExtra("item_url");
        price=intent.getDoubleExtra("price",0.0);
        Glide.with(this).load(pic_url).into(itemImg);
        name.setText(item_name);
        item_url.setText(item_urls);
        price_show.setText(String.valueOf(price));
    }
}
