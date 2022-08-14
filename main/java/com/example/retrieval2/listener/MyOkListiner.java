package com.example.retrieval2.listener;

import org.json.JSONException;

public interface MyOkListiner {
    void onOK(String json) throws JSONException;//成功
    void onError(String message);//错误
}
