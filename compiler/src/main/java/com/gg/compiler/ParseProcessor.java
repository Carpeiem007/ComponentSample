package com.gg.compiler;

import com.gg.annotation.Route;
import com.gg.model.RouterModel;
import com.google.auto.service.AutoService;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;
import com.squareup.javapoet.WildcardTypeName;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedOptions;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import javax.tools.Diagnostic;

@AutoService(Processor.class)
@SupportedAnnotationTypes(ProcessorConstants.ROUTE_ANNOTATION)
@SupportedSourceVersion(SourceVersion.RELEASE_7)
@SupportedOptions({ProcessorConstants.MODULE_NAME, ProcessorConstants.PACKAGE_PATH})
public class ParseProcessor extends AbstractProcessor {

    // 操作Element工具类 (类、函数、属性都是Element)
    private Elements elementUtils;

    // type(类信息)工具类，包含用于操作TypeMirror的工具方法
    private Types typeUtils;

    // Messager用来报告错误，警告和其他提示信息
    private Messager messager;

    // 文件生成器 类/资源，Filter用来创建新的源文件，class文件以及辅助文件
    private Filer filer;
    private String moduleName;
    private String packageName;

    private Map<String, List<RouterModel>> tempPathMap = new HashMap<>();

    private Map<String, String> tempGroupMap = new HashMap<>();

    @Override
    public synchronized void init(ProcessingEnvironment processingEnvironment) {
        super.init(processingEnvironment);
        // 父类受保护属性，可以直接拿来使用。
        // 其实就是init方法的参数ProcessingEnvironment
        // processingEnv.getMessager(); //参考源码64行
        elementUtils = processingEnvironment.getElementUtils();
        messager = processingEnvironment.getMessager();
        filer = processingEnvironment.getFiler();
        typeUtils = processingEnvironment.getTypeUtils();
        if (processingEnvironment.getOptions() != null) {
            // 通过ProcessingEnvironment去获取对应的参数
            moduleName = processingEnvironment.getOptions().get(ProcessorConstants.MODULE_NAME);
            packageName = processingEnvironment.getOptions().get(ProcessorConstants.PACKAGE_PATH);

            messager.printMessage(Diagnostic.Kind.NOTE, ProcessorConstants.MODULE_NAME + " = " + moduleName);
            messager.printMessage(Diagnostic.Kind.NOTE, ProcessorConstants.PACKAGE_PATH + " = " + packageName);
        }
    }

    @Override
    public boolean process(Set<? extends TypeElement> set, RoundEnvironment roundEnvironment) {
        if (set == null || set.isEmpty())
            return false;
        Set<? extends Element> elements = roundEnvironment.getElementsAnnotatedWith(Route.class);
        if (elements == null || set.isEmpty())
            return false;
        try {
            parseElements(elements);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        }
//        String packageName = this.packageName;
//        String className = moduleName + "$$Route";
//        try {
//            JavaFileObject sourceObject = filer.createSourceFile(packageName + "." + className);
//            Writer writer = sourceObject.openWriter();
//            writer.write("package " + packageName + ";\n");
//            writer.write("\n\n\n");
//            writer.write("public class " + className + " {\n");
//            writer.write("\n");
//            writer.write("public static void register(com.gg.common.route.RouteService  routeService) { \n");
//            messager.printMessage(Diagnostic.Kind.NOTE, "annotation preview ");
//            for (Element element : elements) {
//                Route route = element.getAnnotation(Route.class);
//                String groupName = route.value();
//                String routePackName = elementUtils.getPackageOf(element).getQualifiedName().toString();
//                String routeClassName = element.getSimpleName().toString();
//                writer.write("\t routeService.registerRoute(\"" + groupName + "\", new " + routePackName + "." + routeClassName + "());\n");
//            }
//            writer.write("}\n");
//            writer.write("}\n");
//            writer.close();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }

        return true;
    }


    private void parseElements(Set<? extends Element> elements) throws IOException {
        TypeElement activityType = elementUtils.getTypeElement(ProcessorConstants.ACTIVITY);
        TypeMirror activityMirror = activityType.asType();
        for (Element element : elements) {
            TypeMirror elementMirror = element.asType();
            messager.printMessage(Diagnostic.Kind.NOTE, "元素信息 = " + elementMirror.toString());
            //获取注解
            Route route = element.getAnnotation(Route.class);
            String value = route.value();
            if ((value == null) || (value.length() == 0)) {
                value = elementMirror.toString();
            }
            int flagIndex = value.indexOf(ProcessorConstants.FLAG);
            messager.printMessage(Diagnostic.Kind.NOTE, "group组的标志索引为" + flagIndex);
            String groupName = null;
            String pathName = null;
            if (flagIndex > 0) {
                String plit[] = value.split(ProcessorConstants.FLAG);
                groupName = plit[0];
                pathName = plit[1];
                messager.printMessage(Diagnostic.Kind.NOTE, "组的名称为" + groupName);
                messager.printMessage(Diagnostic.Kind.NOTE, "路径的名称为" + pathName);
            } else {
                groupName = moduleName;
                pathName = value.substring(flagIndex == 0 ? 1 : 0);
                messager.printMessage(Diagnostic.Kind.NOTE, "未发现设置的group名称 使用module的名称" + groupName);
                messager.printMessage(Diagnostic.Kind.NOTE, "路径名称为：" + pathName);

            }
            RouterModel model = new RouterModel.Builder().setGroup(groupName).setPath(pathName).setElement(element).build();

            if (typeUtils.isSubtype(elementMirror, activityMirror)) {
                model.setType(RouterModel.Type.ACTIVITY);
            } else {
                messager.printMessage(Diagnostic.Kind.NOTE, "find  class  is " + elementMirror.toString());
            }
            valueOfPathMap(model);

        }

        TypeElement groupLoadType = elementUtils.getTypeElement(ProcessorConstants.ROUTE_GROUP);
        TypeElement pathLoadType = elementUtils.getTypeElement(ProcessorConstants.ROUTE_PATH);

        createPathFile(pathLoadType);

        createGroupFile(groupLoadType, pathLoadType);
    }

    private void createGroupFile(TypeElement groupLoadType, TypeElement pathLoadType) throws IOException {
        if (tempGroupMap == null || tempGroupMap.isEmpty())
            return;
        if (tempPathMap == null || tempPathMap.isEmpty())
            return;
        TypeName methodReturn = ParameterizedTypeName.get(ClassName.get(Map.class),ClassName.get(String.class),
                ParameterizedTypeName.get(ClassName.get(Class.class), WildcardTypeName.subtypeOf(ClassName.get(pathLoadType))));

        MethodSpec.Builder builder = MethodSpec.methodBuilder(ProcessorConstants.GROUP_METHOD_NAME)
                .addAnnotation(Override.class).addModifiers(Modifier.PUBLIC).returns(methodReturn);

        builder.addStatement("$T<$T,$T> $N = new $T<>()",ClassName.get(Map.class),ClassName.get(String.class),ParameterizedTypeName.get(ClassName.get(Class.class),WildcardTypeName.subtypeOf(ClassName.get(pathLoadType))),ProcessorConstants.GROUP_PARAMETER_NAME,HashMap.class);

        for (Map.Entry<String,String> entry:tempGroupMap.entrySet()){
            builder.addStatement("$N.put($S,$T.class)",ProcessorConstants.GROUP_PARAMETER_NAME,entry.getKey(),ClassName.get(packageName,entry.getValue()));

        }

        builder.addStatement("return $N",ProcessorConstants.GROUP_PARAMETER_NAME);
        String className = "Route$$Group$$"+moduleName;

        JavaFile.builder(packageName,
                TypeSpec.classBuilder(className)
                        .addSuperinterface(ClassName.get(groupLoadType))
                        .addModifiers(Modifier.PUBLIC)
                        .addMethod(builder.build()).build()).build().writeTo(filer);



    }

    private void createPathFile(TypeElement groupLoadType) throws IOException {
        if (tempPathMap == null || tempPathMap.isEmpty())
            return;
        TypeName methodReturns = ParameterizedTypeName.get(ClassName.get(Map.class), ClassName.get(String.class), ClassName.get(RouterModel.class));

        for (Map.Entry<String, List<RouterModel>> entry : tempPathMap.entrySet()) {
            MethodSpec.Builder methodBuilder = MethodSpec.methodBuilder(ProcessorConstants.PATH_METHOD_NAME)
                    .addAnnotation(Override.class).addModifiers(Modifier.PUBLIC).returns(methodReturns);
            // Map<String ,RouterBean> pathMap = new HashMap<>();
            methodBuilder.addStatement("$T<$T ,$T> $N = new $T<>()", ClassName.get(Map.class), ClassName.get(String.class), ClassName.get(RouterModel.class), ProcessorConstants.PATH_PARAMETER_NAME, HashMap.class);

            List<RouterModel> pathList = entry.getValue();
            //pathMap.put(name,routerBean);
            for (RouterModel model : pathList) {
                methodBuilder.addStatement("$N.put($S,$T.create($T.$L,$T.class,$S,$S))", ProcessorConstants.PATH_PARAMETER_NAME, model.getPath()
                        , ClassName.get(RouterModel.class), ClassName.get(RouterModel.Type.class), model.getType()
                        , ClassName.get((TypeElement) model.getElement()),
                        model.getPath(), model.getGroup());
            }

            methodBuilder.addStatement("return $N", ProcessorConstants.PATH_PARAMETER_NAME);
            String className = "Router$$" + entry.getKey();

            JavaFile.builder(packageName, TypeSpec.classBuilder(className).addSuperinterface(ClassName.get(groupLoadType)).addModifiers(Modifier.PUBLIC).addMethod(methodBuilder.build()).build())
                    .build().writeTo(filer);
            tempGroupMap.put(entry.getKey(), className);
        }

    }

    private void valueOfPathMap(RouterModel model) {
        List<RouterModel> list = tempPathMap.get(model.getGroup());
        if (list == null) {
            list = new ArrayList<>();
            tempPathMap.put(model.getGroup(), list);
        }
        list.add(model);
    }


}
