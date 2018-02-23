package com.wazabi.liveat500px.manager.http;

import com.wazabi.liveat500px.dao.PhotoItemCollectionDao;

import retrofit2.Call;
import retrofit2.http.POST;

/**
 * Created by MAN on 2/10/2018.
 */

public interface ApiService  {

    @POST("list")
    Call<PhotoItemCollectionDao> loadPhotoList();
}
