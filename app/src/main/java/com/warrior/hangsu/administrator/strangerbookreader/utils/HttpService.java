package com.warrior.hangsu.administrator.strangerbookreader.utils;

import com.youdao.sdk.ydtranslate.Translate;

import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Query;

public interface HttpService {
    @Headers({"Content-Type: application/json", "Accept: application/json"})
    @GET("/openapi.do?keyfrom=foreignnews&key=1447394905&type=data&doctype=json&version=1.1")
    Observable<Translate> translate(@Query("q") String word);
}
