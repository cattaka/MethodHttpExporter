
package net.cattaka.util.methodhttpexporter.apt;

import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;

import net.cattaka.util.methodhttpexporter.annotation.ExportMethodHttp;

/**
 * Annotation processing logic.
 * 
 * @author cattaka
 */
@SupportedSourceVersion(SourceVersion.RELEASE_6)
@SupportedAnnotationTypes("net.cattaka.util.methodhttpexporter.annotation.*")
public class MethodHttpExporterProcessor extends AbstractProcessor {
    @Override
    public synchronized void init(ProcessingEnvironment env) {
        super.init(env);
    }

    @Override
    public boolean process(Set<? extends TypeElement> elements, RoundEnvironment roundEnv) {
        MyProcessor processor2 = new MyProcessor(processingEnv);
        for (Element element : roundEnv.getElementsAnnotatedWith(ExportMethodHttp.class)) {
            processor2.process((TypeElement)element, roundEnv);
        }
        return false;
    }
}
