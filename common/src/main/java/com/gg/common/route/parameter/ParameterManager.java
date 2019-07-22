package com.gg.common.route.parameter;

import java.lang.reflect.InvocationTargetException;
/**
 *
 * 负责参数的绑定
 *
 * */
public class ParameterManager {

    public static final String MethodName = "bindParameter";

    public static void bindParameter(Object o){
        if(o ==null)
            return;
       String name =  o.getClass().getName()+"$$Bind";
        try {
            Class<?> clazz= Class.forName(name);
            clazz.getMethod(MethodName, o.getClass()).invoke(null,o);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
    }
}
