package net.cattaka.util.methodhttpexporter.apt;

import net.cattaka.util.methodhttpexporter.annotation.ExportMethodHttp;
import net.cattaka.util.methodhttpexporter.annotation.ExportMethodHttpAttr;

@ExportMethodHttp
public class SimpleMethods {
    public String myMethod1() {
        return "none";
    }
    @ExportMethodHttpAttr
    public String myMethod2(String arg1, String arg2) {
        return arg1+arg2;
    }
    @ExportMethodHttpAttr
    public String myMethod3(Integer arg1, Double arg2) {
        return String.valueOf(arg1.doubleValue()+arg2.doubleValue());
    }
    @ExportMethodHttpAttr
    public void myMethod4(Integer arg1, Double arg2) {
    }
}
