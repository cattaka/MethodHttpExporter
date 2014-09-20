package net.cattaka.util.methodhttpexporter.util;

import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;
import static org.junit.matchers.JUnitMatchers.*;

import net.cattaka.util.methodhttpexporter.util.HttpServer;
import net.cattaka.util.methodhttpexporter.util.HttpServer.Request;

import org.junit.Before;
import org.junit.Test;

public class HttpServerTest {

    @Before
    public void setUp() throws Exception {
    }

    @Test
    public void testParseParam() {
        HttpServer sup = new HttpServer();
        String requestLine = "GET /path?action=myAction&arg1=val1 HTTP/1.1";
        Request request = sup.parseParam(requestLine);
        
        assertThat(request.path, is("/path"));
        assertThat(request.params.get("action"), is("myAction"));
        assertThat(request.params.get("arg1"), is("val1"));
    }

    @Test
    public void testTerminate() throws Exception {
        HttpServer sup = new HttpServer();
        {   // Run and Terminate
            assertThat(sup.isAlive(), is(false));
            sup.run(38090);
            assertThat(sup.isAlive(), is(true));
            sup.terminate();
            assertThat(sup.isAlive(), is(false));
        }
        {   // Run and Terminate again
            assertThat(sup.isAlive(), is(false));
            sup.run(38090);
            assertThat(sup.isAlive(), is(true));
            sup.terminate();
            assertThat(sup.isAlive(), is(false));
        }
    }
}
