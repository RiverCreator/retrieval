package com.example.retrieval2.Adapter;

import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.retrieval2.Item.item;
import com.example.retrieval2.R;
import com.example.retrieval2.details;
import com.example.retrieval2.retrieval_out;

import java.net.URL;
import java.util.List;

public class ItemAdapter extends RecyclerView.Adapter<ItemAdapter.ViewHolder> {
    private List<item> items;
    public Context context;
    public ItemAdapter(@NonNull Context context, List<item> objects) {
        this.context=context;
        this.items=objects;
    }
    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView img1;
        public ViewHolder(View view) {
            super(view);
            img1 = view.findViewById(R.id.img1);

        }
    }
//    @Override
//    public View getView(int position, View convertView, ViewGroup parent) {
//        item items=getItem(position);
//        View view = LayoutInflater.from(getContext()).inflate(resourceId,parent,false);
//        ImageView img1=(ImageView)view.findViewById(R.id.img1);
//        ImageView img2=(ImageView)view.findViewById(R.id.img2);
//        String url1=items.getUrl1();
//        return view;
//    }

    @NonNull
    @Override
    public ItemAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view=LayoutInflater.from(parent.getContext()).inflate(R.layout.one_item,parent,false);
        final ItemAdapter.ViewHolder holder = new ItemAdapter.ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ItemAdapter.ViewHolder holder, int position) {
        final item item =items.get(position);
        //通过glide库来解析图片
        Log.i("posttag", "getpic_url: "+item.getPic_url());
        Glide.with(context).load(item.getPic_url()).into(holder.img1);
        //Glide.with(context).load(LoginActivity.HOST + item.getUrl2()).into(holder.img2);
        //添加点击监听事件
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, details.class);
                //intent.setAction("action");
                intent.putExtra("ItemId",item.getId());
                intent.putExtra("item_url",item.getUrl1());
                intent.putExtra("price", item.getPrice());
                intent.putExtra("img_url",item.getPic_url());
                intent.putExtra("name",item.getName());
                //Adapter是一个java类并不是一个Activity，普通的java类并不能正确获得上下文环境
                //因此不能直接startActivity,需要手动获取到上下文环境再进行跳转
                context.startActivity(intent);
            }
        });

    }
    @Override
    public int getItemCount() {
        return items.size();
    }
}
