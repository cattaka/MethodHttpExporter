
package net.cattaka.util.methodhttpexporter.apt;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.ElementFilter;
import javax.tools.Diagnostic.Kind;
import javax.tools.JavaFileObject;

import org.mvel2.templates.TemplateRuntime;

import net.cattaka.util.methodhttpexporter.annotation.ExportMethodHttp;
import net.cattaka.util.methodhttpexporter.annotation.ExportMethodHttpAttr;
import net.cattaka.util.methodhttpexporter.apt.util.ResourceUtil;

@SupportedSourceVersion(SourceVersion.RELEASE_6)
@SupportedAnnotationTypes("net.cattaka.util.genasyncif.*")
public class MyProcessor {
    public static class MethodInfo {
        public String methodName;
        public String returnType;

        public List<ArgInfo> argInfos;

        public MethodInfo(String methodName, String returnType, List<ArgInfo> argInfos) {
            super();
            this.methodName = methodName;
            this.returnType = returnType;
            this.argInfos = argInfos;
        }

        @Override
        public String toString() {
            return "MethodInfo [methodName=" + methodName + ", argInfos=" + argInfos + "]";
        }

        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + ((argInfos == null) ? 0 : argInfos.hashCode());
            result = prime * result + ((methodName == null) ? 0 : methodName.hashCode());
            return result;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj)
                return true;
            if (obj == null)
                return false;
            if (getClass() != obj.getClass())
                return false;
            MethodInfo other = (MethodInfo)obj;
            if (argInfos == null) {
                if (other.argInfos != null)
                    return false;
            } else if (!argInfos.equals(other.argInfos))
                return false;
            if (methodName == null) {
                if (other.methodName != null)
                    return false;
            } else if (!methodName.equals(other.methodName))
                return false;
            return true;
        }
    }
    
    public static class ArgInfo {
        public String name;
        public String type;
        public String converter;
        public ArgInfo() {
            super();
        }
        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + ((converter == null) ? 0 : converter.hashCode());
            result = prime * result + ((name == null) ? 0 : name.hashCode());
            result = prime * result + ((type == null) ? 0 : type.hashCode());
            return result;
        }
        @Override
        public boolean equals(Object obj) {
            if (this == obj)
                return true;
            if (obj == null)
                return false;
            if (getClass() != obj.getClass())
                return false;
            ArgInfo other = (ArgInfo)obj;
            if (converter == null) {
                if (other.converter != null)
                    return false;
            } else if (!converter.equals(other.converter))
                return false;
            if (name == null) {
                if (other.name != null)
                    return false;
            } else if (!name.equals(other.name))
                return false;
            if (type == null) {
                if (other.type != null)
                    return false;
            } else if (!type.equals(other.type))
                return false;
            return true;
        }
        
    }

    
    private ProcessingEnvironment processingEnv;
    private String mTemplate;

    public MyProcessor(ProcessingEnvironment processingEnv) {
        this.processingEnv = processingEnv;

        String templateResource = getClass().getPackage().getName().replace('.', '/')
                + "/ExportMethodHttpTemplate.java.mvel";
        try {
            mTemplate = ResourceUtil.getResourceAsString(templateResource);
        } catch (IOException e) {
            processingEnv.getMessager().printMessage(Kind.ERROR, e.getMessage());
            throw new RuntimeException(e);
        }
    }

    public void process(TypeElement element, RoundEnvironment roundEnv) {
        ExportMethodHttp gai = element.getAnnotation(ExportMethodHttp.class);

        String packageName;
        {
            String t = String.valueOf(element.getQualifiedName());
            int n = t.lastIndexOf('.');
            packageName = (n >= 0) ? t.substring(0, n) : "";
        }
        String origClassName = String.valueOf(element.getSimpleName());
        String className = origClassName + "HttpServer";
        List<MethodInfo> methodInfos = pullMethodInfos(element);

        Map<String, Object> map = new HashMap<String, Object>();
        map.put("annotation", gai);
        map.put("methodInfos", methodInfos);
        map.put("packageName", packageName);
        map.put("className", className);
        map.put("origClassName", origClassName);

        String generated = (String)TemplateRuntime.eval(mTemplate, map);
        {
            String qualifiedName = ((packageName.length() > 0) ? packageName + "." : "")
                    + className;
            Filer filer = processingEnv.getFiler();
            Writer writer = null;
            try {
                JavaFileObject fileObject = filer.createSourceFile(qualifiedName, element);
                writer = fileObject.openWriter();
                writer.write(generated);
            } catch (IOException e) {
                Messager messager = processingEnv.getMessager();
                messager.printMessage(Kind.ERROR, e.getMessage(), element);
            } finally {
                if (writer != null) {
                    try {
                        writer.close();
                    } catch (IOException e2) {
                        Messager messager = processingEnv.getMessager();
                        messager.printMessage(Kind.ERROR, e2.getMessage(), element);
                    }
                }
            }
        }
    }

    public List<MethodInfo> pullMethodInfos(TypeElement rootElement) {
        List<MethodInfo> methodInfos = new ArrayList<MethodInfo>();
        List<TypeElement> interfaces = pullInterfaces(rootElement);
        Set<MethodInfo> existMethodInfos = new HashSet<MethodInfo>();
        
        Map<String, String> converterMap = new HashMap<String, String>();
        converterMap.put("java.lang.String", "");
        converterMap.put("java.lang.Character", "Character.valueOf");
        converterMap.put("java.lang.Short", "Short.valueOf");
        converterMap.put("java.lang.Integer", "Integer.valueOf");
        converterMap.put("java.lang.Long", "Long.valueOf");
        converterMap.put("java.lang.Float", "Float.valueOf");
        converterMap.put("java.lang.Double", "Double.valueOf");
        
        for (TypeElement element : interfaces) {
            for (ExecutableElement method : ElementFilter.methodsIn(element.getEnclosedElements())) {
                ExportMethodHttpAttr attr = method.getAnnotation(ExportMethodHttpAttr.class);
                if (attr == null || !attr.enable()) {
                    continue;
                }
                
                String returnType = String.valueOf(method.getReturnType());

                List<ArgInfo> argInfos = new ArrayList<ArgInfo>();
                for (VariableElement arg : method.getParameters()) {
                    ArgInfo argInfo = new ArgInfo();
                    argInfo.name = String.valueOf(arg.getSimpleName());
                    argInfo.type = String.valueOf(arg.asType());
                    argInfo.converter = converterMap.get(argInfo.type);
                    if (argInfo.converter == null) {
                        argInfo.converter = "";
                        processingEnv.getMessager().printMessage(Kind.WARNING, "Type : " + argInfo.type + " is not supported. Use @ExportMethodHttpAttr(ignore=true) for this method.");
                        continue;
                    }
                    argInfos.add(argInfo);
                }

                String methodName = String.valueOf(method.getSimpleName());
                MethodInfo methodInfo = new MethodInfo(methodName, returnType, argInfos);
                if (existMethodInfos.add(methodInfo)) {
                    methodInfos.add(methodInfo);
                }
            }
        }
        return methodInfos;
    }

    public static List<TypeElement> pullInterfaces(TypeElement root) {
        List<TypeElement> dts = new ArrayList<TypeElement>();
        List<TypeElement> tmp = new LinkedList<TypeElement>();
        tmp.add(root);
        while (tmp.size() > 0) {
            TypeElement dt = tmp.remove(0);
            dts.add(dt);
            List<? extends TypeMirror> tms = dt.getInterfaces();
            for (TypeMirror tm : tms) {
                if (tm instanceof DeclaredType) {
                    Element ele = ((DeclaredType)tm).asElement();
                    if (ele instanceof TypeElement) {
                        tmp.add((TypeElement)ele);
                    }
                }
            }
        }
        return dts;
    }

}
