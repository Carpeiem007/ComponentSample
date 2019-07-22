package com.gg.common.route;
/**
 *
 * 路由回调
 *
 * */
public interface RouteLaunchProcessCallback {

    void onLaunchStart(String path);

    void onLaunchEnd(String path);

    void onLaunchError(int code ,String message);
}
