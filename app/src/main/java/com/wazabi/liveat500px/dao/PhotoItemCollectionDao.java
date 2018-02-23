package com.wazabi.liveat500px.dao;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by MAN on 2/11/2018.
 */

public class PhotoItemCollectionDao {


        @SerializedName("success")
        @Expose
        private Boolean success;
        @SerializedName("data")
        @Expose
        private List<PhotoItemDao> data = null;

        public Boolean getSuccess() {
            return success;
        }

        public void setSuccess(Boolean success) {
            this.success = success;
        }

        public List<PhotoItemDao> getData() {
            return data;
        }

        public void setData(List<PhotoItemDao> data) {
            this.data = data;

    }
}
