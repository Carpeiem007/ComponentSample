package com.gg.compiler;

public class ProcessorConstants {


    public static final String ROUTE_ANNOTATION = "com.gg.annotation.Route";
    public static final String MODULE_NAME = "module_name";
    public static final String PACKAGE_PATH = "package_path_for_APT";
    public static final String FLAG = ":";


    public static final String ACTIVITY = "android.app.Activity";

    public static final String ROUTE_PATH = "com.gg.common.route.imp.RouterLoadPath";
    //ROUTE_PATH
    public static final String ROUTE_GROUP = "com.gg.common.route.imp.RouterLoadGroup";


    // 路由组Group对应的详细Path，参数名
    public static final String PATH_PARAMETER_NAME = "pathMap";
    // 路由组Group对应的详细Path，方法名
    public static final String PATH_METHOD_NAME = "loadPath";

    // 路由组Group，参数名
    public static final String GROUP_PARAMETER_NAME = "groupMap";
    // 路由组Group，方法名
    public static final String GROUP_METHOD_NAME = "loadGroup";

    public static final String PARAMETER_ANNOTATION = "com.gg.annotation.Parameter";
}
