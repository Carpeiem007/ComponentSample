package com.gg.common.route;

import android.os.Bundle;
/**
 *
 * 启动一个路由的参数
 *
 * */
public class RouteWrapper {

    final String path;
    final Bundle data;

    private RouteWrapper(Builder builder) {
        this.path = builder.path;
        this.data = builder.data;
    }


    public static class Builder {

        private String path;

        private Bundle data;

        public Builder(String path) {

            this.path = path;
        }


        public Builder with(Bundle data) {
            if (this.data == null)
                this.data = data;
            else
                this.data.putAll(data);
            return this;
        }

        public Builder with(String key, int data) {
            if (this.data == null) {
                this.data = new Bundle();
            }
            this.data.putInt(key, data);
            return this;

        }
        public Builder with(String key, String data) {
            if (this.data == null) {
                this.data = new Bundle();
            }
            this.data.putString(key, data);
            return this;

        }

        public LaunchHandler create() {
            return new LaunchHandler(new RouteWrapper(this));
        }


    }


}
