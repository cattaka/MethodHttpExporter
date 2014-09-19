
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

    	public List<String> argNames;

        public MethodInfo(String methodName, List<String> argNames) {
            super();
            this.methodName = methodName;
            this.argNames = argNames;
        }

        @Override
        public String toString() {
            return "MethodInfo [methodName=" + methodName + ", argNames=" + argNames + "]";
        }

        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + ((argNames == null) ? 0 : argNames.hashCode());
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
            if (argNames == null) {
                if (other.argNames != null)
                    return false;
            } else if (!argNames.equals(other.argNames))
                return false;
            if (methodName == null) {
                if (other.methodName != null)
                    return false;
            } else if (!methodName.equals(other.methodName))
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

    public static List<MethodInfo> pullMethodInfos(TypeElement rootElement) {
        List<MethodInfo> methodInfos = new ArrayList<MethodInfo>();
        List<TypeElement> interfaces = pullInterfaces(rootElement);
        Set<MethodInfo> existMethodInfos = new HashSet<MethodInfo>();
        for (TypeElement element : interfaces) {
            for (ExecutableElement method : ElementFilter.methodsIn(element.getEnclosedElements())) {
                ExportMethodHttpAttr attr = method.getAnnotation(ExportMethodHttpAttr.class);
                if (attr != null && attr.ignore()) {
                    continue;
                }

                List<String> argNames = new ArrayList<String>();
                for (VariableElement arg : method.getParameters()) {
                    argNames.add(String.valueOf(arg.getSimpleName()));
                }

                String methodName = String.valueOf(method.getSimpleName());
                MethodInfo methodInfo = new MethodInfo(methodName, argNames);
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
