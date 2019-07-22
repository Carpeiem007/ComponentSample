package com.gg.model;

import javax.lang.model.element.Element;

public class RouterModel {

    public enum Type {
        ACTIVITY
    }

    private Type type;

    private Element element;

    private Class<?> clazz;

    private String path;

    private String group;

    private RouterModel(Builder builder){
        this.type = builder.type;
        this.element = builder.element;
        this.clazz = builder.clazz;
        this.path = builder.path;
        this.group = builder.group;
    }
    private RouterModel(Type type, Class<?> clazz, String path, String group) {
        this.type = type;
        this.path = path;
        this.group = group;
        this.clazz = clazz;
    }

    public static RouterModel create(Type type,Class<?> clazz ,String path ,String group ){
        return new RouterModel(type, clazz, path, group);
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public Element getElement() {
        return element;
    }

    public void setElement(Element element) {
        this.element = element;
    }

    public Class<?> getClazz() {
        return clazz;
    }

    public void setClazz(Class<?> clazz) {
        this.clazz = clazz;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }
    public static class Builder {

        // 枚举类型：Activity
        private Type type;
        // 类节点
        private Element element;
        // 注解使用的类对象
        private Class<?> clazz;
        // 路由地址
        private String path;
        // 路由组
        private String group;

        public Builder setType(Type type) {
            this.type = type;
            return this;
        }

        public Builder setElement(Element element) {
            this.element = element;
            return this;
        }

        public Builder setClazz(Class<?> clazz) {
            this.clazz = clazz;
            return this;
        }

        public Builder setPath(String path) {
            this.path = path;
            return this;
        }

        public Builder setGroup(String group) {
            this.group = group;
            return this;
        }

        // 最后的build或者create，往往是做参数的校验或者初始化赋值工作
        public RouterModel build() {
            if (path == null || path.length() == 0) {
                throw new IllegalArgumentException("path必填项为空，如：/app/MainActivity");
            }
            return new RouterModel(this);
        }
    }

    @Override
    public String toString() {
        return "RouterModel{" +
                "path='" + path + '\'' +
                ", group='" + group + '\'' +
                '}';
    }
}
