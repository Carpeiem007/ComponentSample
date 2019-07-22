package com.gg.common.route;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v4.util.LruCache;
import android.util.Log;

import com.gg.common.route.imp.RouterLoadPath;
import com.gg.model.RouterModel;

import java.lang.ref.WeakReference;

/**
 * 负责路由的启动
 */
public class LaunchHandler {

    private static LruCache<String, Class<?>> classPaths = new LruCache<>(15);

    private RouteWrapper route;

    private WeakReference<RouteLaunchProcessCallback> callback;

    private RouteService service = RouteService.getInstance();

    public LaunchHandler setLaunchProcess(RouteLaunchProcessCallback callback) {
        this.callback = new WeakReference<>(callback);
        return this;
    }

    LaunchHandler(RouteWrapper wrapper) {
        this.route = wrapper;
    }

    public void launchActivity(Context context, int requestCode) {
        if (route == null) {
            return;
        }
        Class<?> clazz = classPaths.get(route.path);
        if (clazz == null) {
            clazz = internalGetClass(route.path);
            Log.e("info", "cache no target , search from internal");
        }
        if (clazz == null) {
            if (callback != null && callback.get() != null) {
                callback.get().onLaunchError(-1, "not find target path activity !");
            }
            return;
        }
        Intent intent = new Intent(context, clazz);
        if (route.data != null) {
            intent.putExtra("DATA", route.data);
        }
        if (requestCode != 0) {
            if ((context instanceof Activity)) {
                if (callback != null && callback.get() != null) {
                    callback.get().onLaunchStart(route.path);
                }
                ((Activity) context).startActivityForResult(intent, requestCode);
                if (callback != null && callback.get() != null) {
                    callback.get().onLaunchEnd(route.path);
                }
            }
            throw new IllegalArgumentException(" this requestCode != zero  must use activity to launch");
        } else {
            if (callback != null && callback.get() != null) {
                callback.get().onLaunchStart(route.path);
            }
            context.startActivity(intent);
            if (callback != null && callback.get() != null) {
                callback.get().onLaunchEnd(route.path);
            }
        }


    }

    private Class<?> internalGetClass(String url) {
        int flagIndex = url.indexOf(":");
        if (flagIndex <= 0) {
            throw new IllegalArgumentException(" path illegal 🙄 the format must be  groupName:path");
        }
        String groupName = url.split(":")[0];
        String path = url.split(":")[1];
        Log.e("info", "groupName = " + groupName + " path = " + path);
        Class<?> clazz = service.getGroupClass(groupName);
        if (clazz == null)
            return null;
        RouterLoadPath loadPath = null;
        try {
            loadPath = (RouterLoadPath) clazz.newInstance();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        }
        if (loadPath != null) {
            RouterModel routerModel = loadPath.loadPath().get(path);
            if (routerModel != null) {
                classPaths.put(url, routerModel.getClazz());
                return routerModel.getClazz();
            }
            return null;
        }
        return null;
    }


}
