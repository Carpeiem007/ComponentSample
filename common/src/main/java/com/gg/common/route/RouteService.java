package com.gg.common.route;

import com.gg.common.route.imp.RouterLoadGroup;
import com.gg.common.route.imp.RouterLoadPath;

import java.util.ArrayList;
import java.util.Map;
/**
 *
 * 负责路由的注册
 *
 * */
public class RouteService {

    private static RouteService service;

    public ArrayList<RouterLoadGroup> groups = new ArrayList<>();

    private RouteService() {
    }

    public static RouteService getInstance() {
        synchronized (RouteService.class) {
            if (service == null)
                service = new RouteService();
            return service;
        }
    }


    public void register(RouterLoadGroup group) {
        groups.add(group);
    }

    public void unRegister(RouterLoadGroup group) {
        groups.remove(group);
    }

    public Class<?> getGroupClass(String groupName) {
        Map<String, Class<? extends RouterLoadPath>> map;
        for (RouterLoadGroup group : groups) {
            map = group.loadGroup();
            if (map != null) {
                return map.get(groupName);
            }
        }
        return null;
    }
}
