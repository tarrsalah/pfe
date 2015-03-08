/* 
 * The MIT License
 *
 * Copyright 2014 tarrsalah.org.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package com.github.tarrsalah.pfe.soap.services;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.grizzly.http.server.NetworkListener;
import org.glassfish.grizzly.http.server.StaticHttpHandler;
import org.glassfish.grizzly.jaxws.JaxwsHandler;
import org.reflections.Reflections;

/**
 * Main.java (UTF-8)
 *
 * May 21, 2014
 *
 * @author tarrsalah.org
 */
public class Main {

    public static final String HOST = "http://127.0.0.1";

    private static final Reflections reflections = new Reflections("com.github.tarrsalah.pfe.soap.services.fixtures");
    private static final Logger LOG = Logger.getLogger(Main.class.getName());

    private static final String STATIC = "/home/tarrsalah/src/github.com/tarrsalah/pfe/prototype/soap-services/src/main/resources/public";

    public List<Object> getServices() {
        List<Object> services = new ArrayList<>();

        reflections.getTypesAnnotatedWith(javax.jws.WebService.class).stream().
                forEach(service -> {
                    try {
                        services.add(service.newInstance());
                    } catch (InstantiationException | IllegalAccessException ex) {
                        Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
                    }
                });
        return services;
    }

    public static void main(String[] args) throws IOException, InterruptedException {
        HttpServer httpServer = new HttpServer();
        NetworkListener networkListener = new NetworkListener("grizzly", "0.0.0.0", 8080);
        httpServer.addListener(networkListener);

        httpServer.getServerConfiguration().addHttpHandler(new StaticHttpHandler(STATIC));

        new Main().getServices().stream().forEach(service -> {
            String serviceSimpleName = service.getClass().getSimpleName().toLowerCase();
            String serviceCompleteName = service.getClass().getName();
            LOG.log(Level.INFO, serviceCompleteName);
            
            httpServer.getServerConfiguration().addHttpHandler(
                    new JaxwsHandler(service), "/".concat(serviceSimpleName));
            LOG.log(Level.INFO, "Depploying {0} on /{1}", new String[]{serviceCompleteName, serviceSimpleName});
        });

        httpServer.start();
        System.in.read();
    }
}
