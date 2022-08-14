package com.example.retrieval2;

import android.content.ClipData;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.retrieval2.Adapter.ItemAdapter;
import com.example.retrieval2.Item.item;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;

public class retrieval_out extends AppCompatActivity implements View.OnClickListener{
    private String results;
    public ItemAdapter adapter;
    private List<item> ItemList=new ArrayList<>();
    private RecyclerView recycle_list;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.restrieval_out);
        Intent intent=getIntent();
        results = intent.getStringExtra("results");
        Log.i("posttag", "getmsg3: "+results);
        //设置状态栏透明
//        TranStatusBar.setHalfTransparent(this);
//        TranStatusBar.setFitSystemWindow(false,this);
        recycle_list=findViewById(R.id.projects_list);
        LinearLayoutManager layout = new LinearLayoutManager(this);
        layout.setOrientation(LinearLayoutManager.VERTICAL);
        recycle_list.setLayoutManager(layout);
        adapter=new ItemAdapter(this,ItemList);
        recycle_list.setAdapter(adapter);
        //adapter=new ItemAdapter(this,ItemList);
        //recycle_list.setAdapter(adapter);

        JSONObject result_json= null;
        try {
            result_json = new JSONObject(results);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        //初始化列表
        for(int i=0;i<20;i++){
            JSONObject data= null;
            try {
                data = result_json.getJSONObject(Integer.toString(i));
                //Log.i("posttag", "getmsg: "+data.getString("id"));
                int id=data.getInt("id");
                String name=data.getString("name");
                String img_url=data.getString("img_url");
                String item_url=data.getString("item_url");
                double price=data.getDouble("price");
                item one_item=new item(id,name,item_url,img_url,price);
                ItemList.add(one_item);
            } catch (JSONException | MalformedURLException e) {
                e.printStackTrace();
            }
        }
        Log.i("posttag", "getname: "+ItemList.get(0).getPic_url());
        adapter.notifyDataSetChanged();
    }
    @Override
    public void onClick(View v) {

    }
}
