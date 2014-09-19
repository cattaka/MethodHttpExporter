package net.cattaka.util.methodhttpexporter.apt;

import net.cattaka.util.methodhttpexporter.annotation.ExportMethodHttp;

@ExportMethodHttp
public class SimpleMethods {
    public String myMethod1() {
        return "none";
    }
    public String myMethod2(String arg1, String arg2) {
        return arg1+arg2;
    }
    public String myMethod3(Integer arg1, Double arg2) {
        return String.valueOf(arg1.doubleValue()+arg2.doubleValue());
    }
}
