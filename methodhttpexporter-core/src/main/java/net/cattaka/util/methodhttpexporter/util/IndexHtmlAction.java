package net.cattaka.util.methodhttpexporter.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import net.cattaka.util.methodhttpexporter.util.HttpServer.ActionResult;
import net.cattaka.util.methodhttpexporter.util.HttpServer.IAction;

public abstract class IndexHtmlAction implements IAction {
    
    public IndexHtmlAction() {
        super();
    }
    
    public abstract Collection<IAction> getActions();
    
    @Override
    public String[] getParamNames() {
        return new String[] {};
    }
    @Override
    public ActionResult action(String... params) {
        return new ActionResult(200, toHtml(getActions()));
    }
    @Override
    public String getActionName() {
        return "";
    }
    
    private String toHtml(Collection<IAction> actions) {
        List<IAction> t = new ArrayList<HttpServer.IAction>(actions);
        Collections.sort(t, new Comparator<IAction>() {
            @Override
            public int compare(IAction o1, IAction o2) {
                return o1.getActionName().compareTo(o2.getActionName());
            }
        });
        StringBuilder sb = new StringBuilder();
        sb.append("<!DOCTYPE html>");
        sb.append("<html>");
        sb.append("<head>");
        sb.append("<title>MethodHttpExporter</title>");
        sb.append("</head>");
        sb.append("<body>");
        sb.append("<h1>MethodHttpExporter</h1>");
        for (IAction action : t) {
            if (action == this) {
                continue;
            }
            toHtml(action, sb);
        }
        sb.append("</body>");
        sb.append("</html>");
        return sb.toString();
    }
    private void toHtml(IAction action, StringBuilder sb) {
        sb.append("<h2>"+action.getActionName()+"</h2>");
        sb.append("<form action='/"+action.getActionName()+"'>");
        for (String paramName : action.getParamNames()) {
            sb.append(paramName + "<input type='text' name='"+paramName+"'/><br/>");
        }
        sb.append("<input type='submit' name='Submit'/>");
        sb.append("</form>");
    }
}
