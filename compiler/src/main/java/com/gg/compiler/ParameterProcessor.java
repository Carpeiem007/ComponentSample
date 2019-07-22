package com.gg.compiler;

import com.gg.annotation.Parameter;
import com.google.auto.service.AutoService;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;

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
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import javax.tools.Diagnostic;

/**
 * 处理参数的APT Processor
 */
@AutoService(Processor.class)
//指定处理的版本
@SupportedAnnotationTypes(ProcessorConstants.PARAMETER_ANNOTATION)
//指定编译的版本
@SupportedSourceVersion(SourceVersion.RELEASE_7)
public class ParameterProcessor extends AbstractProcessor {


    private Elements elements;
    private Filer filer;
    private Messager messager;
    private Types types;
    private Map<String, List<Element>> tempMaps = new HashMap<>();

    @Override
    public synchronized void init(ProcessingEnvironment processingEnvironment) {
        super.init(processingEnvironment);
        elements = processingEnvironment.getElementUtils();
        filer = processingEnvironment.getFiler();
        messager = processingEnvironment.getMessager();
        types = processingEnvironment.getTypeUtils();
    }

    @Override
    public boolean process(Set<? extends TypeElement> set, RoundEnvironment roundEnvironment) {
        if (set == null || set.isEmpty())
            return false;
        Set<? extends Element> elements = roundEnvironment.getElementsAnnotatedWith(Parameter.class);
        if (elements == null || elements.isEmpty())
            return false;
        messager.printMessage(Diagnostic.Kind.NOTE, "dispatch parameter");
        for (Element element : elements) {
            //获取className; 并按ClassName 进行插入的tempMap中
            insert(element.getEnclosingElement().getSimpleName().toString(), element);
        }
        //对tempMap进行遍历 创建java文件
        for (Map.Entry<String, List<Element>> entry : tempMaps.entrySet()) {
            createJavaFile(entry.getKey(), entry.getValue());
        }

        return false;
    }

    private void insert(String className, Element element) {
        List<Element> list = tempMaps.get(className);
        if (list == null) {
            list = new ArrayList<>();
            tempMaps.put(className, list);
        }
        list.add(element);
    }

    /**
     * public class className$$Bind{
     *       public static bindParameter(Activity activity){
     *              Bundle bundle = activity.getIntent().getBundleExtra("DATA");
     *
     *          }
     * }
     * */

    private void createJavaFile(String className, List<Element> elements) {
        if (elements.isEmpty())
            return;
        final String packageName = this.elements.getPackageOf(elements.get(0)).getQualifiedName().toString();
        String resultClassName = className + "$$Bind";
        //获取当前参数的类型字符串
        String innerParameter = packageName+"."+className;

        messager.printMessage(Diagnostic.Kind.NOTE, "packageName = " + packageName);
        TypeElement activityElement = this.elements.getTypeElement(innerParameter);
        messager.printMessage(Diagnostic.Kind.NOTE, "className = " + innerParameter);
        //先判断是否是activity的子类
        //获取activity的类型
        TypeElement  activitySup=  this.elements.getTypeElement(ProcessorConstants.ACTIVITY);
        TypeMirror supMirror = activitySup.asType();
        if(!this.types.isSubtype(activityElement.asType(),supMirror)){
            throw new IllegalArgumentException("cur class must extends Activity");
        }

        TypeName parameterName = ParameterizedTypeName.get(activityElement.asType());
        MethodSpec.Builder builder = MethodSpec.methodBuilder("bindParameter")
                .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                .addParameter(parameterName, "activity", Modifier.FINAL);

        builder.addStatement("$T $N = $N.getIntent().getBundleExtra(\"DATA\")",
                ClassName.get(this.elements.getTypeElement("android.os.Bundle")), "bundle", "activity");
        builder.addStatement("if($N==null) { return ;}", "bundle");
        for (Element element : elements) {
            Parameter parameter = element.getAnnotation(Parameter.class);
            String value = parameter.name();
            if (value == null || value.length() == 0)
                value = element.getSimpleName().toString();
            getValue(builder, element, value);
        }
        try {
            JavaFile.builder(packageName, TypeSpec.classBuilder(resultClassName).addMethod(builder.build()).build()).build().writeTo(filer);
        } catch (IOException e) {
            e.printStackTrace();
        }




    }

    private void getValue(MethodSpec.Builder builder, Element element, String value) {
        if ("int".equals(element.asType().toString())) {
            builder.addStatement("$N.$N = $N.getInt(\"$N\" ,0)", "activity", element.getSimpleName().toString(), "bundle", value);
        }else if("float".equals(element.asType().toString())){
            builder.addStatement("$N.$N = $N.getFloat(\"$N\" ,0)", "activity", element.getSimpleName().toString(), "bundle", value);
        }else if("java.lang.String".equals(element.asType().toString())){
            builder.addStatement("$N.$N = $N.getString(\"$N\" ,\"\")", "activity", element.getSimpleName().toString(), "bundle", value);
        }else{
           Element parcelElement =  this.elements.getTypeElement("android.os.Parcelable");
            if(types.isSubtype(element.asType(),parcelElement.asType())){
                builder.addStatement("$N.$N = $N.getParcelable(\"$N\" ,\"\")", "activity", element.getSimpleName().toString(), "bundle", value);
            }
            Element serizableElement = this.elements.getTypeElement("java.io.Serializable");
            if(types.isSubtype(element.asType(),serizableElement.asType())){
                builder.addStatement("$N.$N = ($T)$N.getParcelable(\"$N\" ,\"\")", "activity", element.getSimpleName().toString(), ClassName.get(element.asType()),"bundle", value);

            }

        }
    }


}
