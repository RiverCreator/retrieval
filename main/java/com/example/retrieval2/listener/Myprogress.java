package com.example.retrieval2.listener;

public interface Myprogress {
    void onError(String message);//失败
    void onFinish();//成功
    void onProgress(int progress);//进度
}
