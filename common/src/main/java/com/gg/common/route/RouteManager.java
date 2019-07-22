package com.gg.common.route;
/**
 *
 * 负责路由的管理，目前只有开启一个路由的方法
 *
 * */
public class RouteManager {


    public static RouteWrapper.Builder startRoute(String path) {
        return new RouteWrapper.Builder(path);
    }



}
