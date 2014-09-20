package net.cattaka.util.methodhttpexporter.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HttpServer {
    public interface IAction {
        public String getActionNames();
        public String[] getParamNames();
        public ActionResult action(String... params);
    }
    public static class ActionResult {
        int code;
        String body;
        public ActionResult(int code, String result) {
            super();
            this.code = code;
            this.body = result;
        }
    }
    class AcceptThread extends Thread {
        volatile boolean stopFlag = false;
        ServerSocket serverSocket;
        public AcceptThread(ServerSocket serverSocket) {
            this.serverSocket = serverSocket;
        }
        public void run() {
            while (!stopFlag) {
                try {
                    Socket socket = serverSocket.accept();
                    ClientThread clientThread = new ClientThread(socket);
                    clientThreads.add(clientThread);
                    clientThread.start();
                } catch (IOException e) {
                    continue;
                }
            }
        };
        public void requestStop() {
            stopFlag = true;
            try {
                serverSocket.close();
            } catch (IOException e) {
                // ignore
            }
        }
    }
    class ClientThread extends Thread {
        Socket socket;
        public ClientThread(Socket socket) {
            this.socket = socket;
        }
        public void run() {
            try {
                BufferedReader in =  new BufferedReader(new InputStreamReader(socket.getInputStream(), "UTF8"));
                BufferedWriter writer =  new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), "UTF8"));
                String requestLine = in.readLine();
                {
                    String nextLine;
                    while ((nextLine = in.readLine()) != null) {
                        if (nextLine.length() == 0) {
                            break;
                        }
                    }
                }
                Request request = (requestLine != null) ? parseParam(requestLine) : null; 
                if (request != null) {
                    ActionResult result = runAction(request);
                    if (result != null) {
                        writer.write("HTTP/1.1 " + result.code + " OK\r\n" +
                        		"Connection: close\r\n" +
                        		"Content-Type: text/html; charset=UTF-8\r\n" +
                        		"\r\n");
                        writer.write(result.body);
                    } else {
                        writer.write("HTTP/1.1 404 Not found\r\n" +
                                "Connection: close\r\n" +
                                "Content-Type: text/html; charset=UTF-8\r\n" +
                                "\r\n");
                        writer.write("Not found");
                    }
                    writer.flush();
                }
            } catch (IOException e) {
                // ignore
            } finally {
                try {
                    socket.close();
                } catch (IOException e) {
                    // ignore
                }
            }
            clientThreads.remove(this);
        };
    }
    public static class Request {
        public String path;
        public Map<String, String> params;
        public Request(String path, Map<String, String> params) {
            super();
            this.path = path;
            this.params = params;
        }
        @Override
        public String toString() {
            return "Request [path=" + path + ", params=" + params + "]";
        }
        
    }
    
    AcceptThread acceptThread;
    List<ClientThread> clientThreads = Collections.synchronizedList(new ArrayList<HttpServer.ClientThread>());
    Map<String, IAction> actionMap = new HashMap<String, HttpServer.IAction>();

    public static void main(String[] args) throws IOException {
        HttpServer httpServer = new HttpServer();
        httpServer.run(8080);
    }
    public void run(int port) throws IOException {
        ServerSocket serverSocket = new ServerSocket(port);
        acceptThread = new AcceptThread(serverSocket);
        acceptThread.start();
    }
    
    public void addAction(IAction action) {
        actionMap.put("/"+action.getActionNames(), action);
    }
    
    ActionResult runAction(Request request) {
        IAction action = actionMap.get(request.path);
        if (action == null) {
            return null;
        }
        String[] params = new String[action.getParamNames().length];
        for (int i=0;i<params.length;i++) {
            params[i] = request.params.get(action.getParamNames()[i]);
        }
        return action.action(params);
    }
    
    Request parseParam(String requestLine) {
        String[] tmps = requestLine.split(" +");
        if (tmps.length < 3) {
            return null;
        }
        String path;
        String[] paramsStr;
        Map<String, String> params = new HashMap<String, String>();
        {
            int n = tmps[1].indexOf('?');
            if (n >= 0) {
                path = tmps[1].substring(0, n);
                String paramsBlock = tmps[1].substring(n + 1, tmps[1].length());
                paramsStr = paramsBlock.split("&");;
                
                for (String paramStr : paramsStr) {
                    String[] ts = paramStr.split("=", 2);
                    params.put(ts[0], (ts.length >= 2 ? ts[1] : ""));
                }
            } else {
                path = tmps[1];
            }
        }
        
        return new Request(path, params);
    }
    
    public boolean isAlive() {
        return (acceptThread != null && acceptThread.isAlive());
    }
    
    public void terminate() throws InterruptedException {
        if (isAlive()) {
            acceptThread.requestStop();
            acceptThread.join();
            acceptThread = null;
        }
    }
}
