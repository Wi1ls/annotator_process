package wills.processor;

import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.util.Elements;
import javax.tools.Diagnostic;

import com.google.auto.service.AutoService;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;
import wills.annotations.CustomFieldAnnotation;
import wills.annotations.CustomTypeAnnotation;

@AutoService(Processor.class)
public class CustomProcessor extends AbstractProcessor {
  //工具类，可以从 Element 解析数据
  private Elements elementUtils;
  //log 工具，可以在 gradle console 查看输出
  private Messager messager;
  //用来创建 java 文件的
  private Filer filer;

  @Override
  public synchronized void init(ProcessingEnvironment processingEnvironment) {
    super.init(processingEnvironment);
    elementUtils = processingEnvironment.getElementUtils();
    messager = processingEnvironment.getMessager();
    filer = processingEnvironment.getFiler();
  }

  //这里是核心代码，就是在这里处理注解的
  @Override
  public boolean process(Set<? extends TypeElement> set,
                         RoundEnvironment roundEnvironment) {
    messager.printMessage(Diagnostic.Kind.NOTE, "processor start !");

    Set<? extends Element> fieldSet = roundEnvironment
        .getElementsAnnotatedWith(CustomFieldAnnotation.class);
    Set<? extends Element> typeSet = roundEnvironment
        .getElementsAnnotatedWith(CustomTypeAnnotation.class);
    if (!checkFieldAnnotationValid(fieldSet)) {
      return false;
    }
    if (!checkTypeAnnotationValid(typeSet)) {
      return false;
    }

    processFiledAnnotation(fieldSet);
    processTypeAnnotation(typeSet);


    messager.printMessage(Diagnostic.Kind.NOTE, "processor end !");
    return true;
  }

  private void processTypeAnnotation(Set<? extends Element> typeSet) {

    TypeSpec.Builder typeSpec = TypeSpec
        .classBuilder("JavaFromProcessor")
        .addModifiers(Modifier.FINAL, Modifier.PUBLIC);
    MethodSpec.Builder methodSpec
        = MethodSpec.methodBuilder("methodFromProcessor")
        .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
        .addParameter(TypeName.OBJECT, "object")
        .returns(TypeName.VOID);
    StringBuilder sb = new StringBuilder();
    sb.append("process get:\\n");

    for (Element element : typeSet) {
      TypeElement typeElement = (TypeElement) element;
      CustomTypeAnnotation annotation = typeElement.getAnnotation(CustomTypeAnnotation.class);
      sb.append(String.format("@CustomTypeAnnotation:%s,value=%d\\n",
          typeElement.getQualifiedName(), annotation.property()));
    }
    methodSpec.addStatement(String.format("System.out.println(\"%s\")",
        sb.toString()));
    typeSpec.addMethod(methodSpec.build());
    JavaFile file = JavaFile.builder("wills.annotatorprocessdemo",
        typeSpec.build()).build();
    try {
      file.writeTo(filer);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  private Map<TypeElement, Set<VariableElement>> cache = new HashMap<>();

  private void processFiledAnnotation(Set<? extends Element> fieldSet) {
    for (Element element : fieldSet) {
      VariableElement variableElement = (VariableElement) element;
      //通过 VariableElement 可以获得 CustomFieldAnnotation注解的值
      CustomFieldAnnotation customFieldAnnotation = variableElement.getAnnotation(CustomFieldAnnotation.class);
      //获取这个 variableElement 所属的 TypeElement
      TypeElement typeElement = (TypeElement) variableElement.getEnclosingElement();
      Set<VariableElement> variableSet = cache.get(typeElement);
      if (variableSet == null) {
        variableSet = new LinkedHashSet<>();
        cache.put(typeElement, variableSet);
      }
      variableSet.add(variableElement);
    }
    //获得了一个类下的所有被CustomFieldAnnotation注解的字段
    //你就可以自定义自己想要的代码模板了
  }

  //这个方法表示你的 processor 所关心的注解类
  //只有关心的注解类，才会被本 processor 编译
  @Override
  public Set<String> getSupportedAnnotationTypes() {
    Set<String> concernSet = new LinkedHashSet<>();
    concernSet.add(CustomFieldAnnotation.class.getCanonicalName());
    concernSet.add(CustomTypeAnnotation.class.getCanonicalName());
    return concernSet;
  }

  @Override
  public SourceVersion getSupportedSourceVersion() {
    return SourceVersion.latestSupported();
  }

  private boolean checkFieldAnnotationValid(Set<? extends Element> fieldSet) {
    for (Element element : fieldSet) {
      if (element.getKind() != ElementKind.FIELD) {
        //这里说明你的注解有问题，定义的注解是在 FIELD 上的，但是得到的却不是
        return false;
      }
    }
    return true;
  }

  private boolean checkTypeAnnotationValid(Set<? extends Element> typeSet) {
    for (Element element : typeSet) {
      if (element.getKind() != ElementKind.CLASS) {
        //这里说明你的注解有问题，定义的注解是在 CLASS 上的，但是得到的却不是
        return false;
      }
    }
    return true;
  }


}
