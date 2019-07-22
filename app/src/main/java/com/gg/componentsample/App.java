package com.gg.componentsample;

import com.componment.route.Route$$Group$$My;
import com.gg.common.BaseApp;
import com.gg.common.route.RouteService;

public class App extends BaseApp {

    @Override
    public void onCreate() {
        super.onCreate();
       RouteService routeService =  RouteService.getInstance();
       routeService.register(new Route$$Group$$My());
    }
}
