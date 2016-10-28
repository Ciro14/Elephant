package de.uni_due.paluno.elephant.example.rendering;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import javax.swing.*;
import java.awt.*;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.management.ManagementFactory;
import com.sun.management.OperatingSystemMXBean;
import java.net.InetSocketAddress;
import java.util.*;
import java.util.List;
import java.util.Timer;

/**
 * Created by Ole Meyer
 */
public class MainWindow {

    public static final int LINE_COUNT=200;

    public static void main(String[] args){
        JFrame jFrame=new JFrame("Elephant Demo");
        jFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        final List<Line> lines=new LinkedList<>();

        final int[] iterations = {0,100};
        final double[] fillPercentage=new double[]{100};
        final double[] parameters=new double[]{0,0,0};


        final DrawablePanel panel=new DrawablePanel() {
            private Graphics old=null;
            @Override
            public void paint(Graphics graphics) {
                long start=System.nanoTime();
                super.paint(graphics);
                for(int i=0;i<lines.size();++i){
                    Line l=lines.get(i);
                    l.draw(graphics,i<lines.size()*fillPercentage[0]/100);
                    l.move(this.getSize(),this.getFramerate());

                }
                long calcTime=System.nanoTime()-start;
                long availableTime=1000000000/getFramerate();
                double percentage=(double)availableTime/calcTime;
                percentage=Math.min(100,percentage*100);

                synchronized (parameters){
                    parameters[2]++;
                    parameters[1]+=percentage;
                    parameters[0]=parameters[1]/parameters[2];
                }

            }
        };
        final Dimension dimension=new Dimension(500,500);
        panel.setPreferredSize(dimension);

        for(int i=0;i<LINE_COUNT;++i)lines.add(new Line(dimension));

        jFrame.getContentPane().add(panel);
        jFrame.pack();
        jFrame.setVisible(true);


        try {
            HttpServer httpServer=HttpServer.create(new InetSocketAddress(54782),0);
            httpServer.setExecutor(null);
            httpServer.createContext("/sensor/completeness", new HttpHandler() {
                @Override
                public void handle(HttpExchange httpExchange) throws IOException {
                    double completeness;
                    synchronized (parameters){
                        completeness=parameters[0];
                        parameters[0]=0;
                        parameters[1]=0;
                        parameters[2]=0;

                    }
                    String response=String.valueOf(completeness);
                    httpExchange.sendResponseHeaders(200,response.length());
                    httpExchange.getResponseBody().write(response.getBytes());
                    httpExchange.getResponseBody().close();
                }
            });
            final OperatingSystemMXBean bean = (com.sun.management.OperatingSystemMXBean) ManagementFactory
                    .getOperatingSystemMXBean();
            httpServer.createContext("/sensor/cpu", new HttpHandler() {
                @Override
                public void handle(HttpExchange httpExchange) throws IOException {


                    String response=String.valueOf(bean.getSystemCpuLoad()*100);
                    httpExchange.sendResponseHeaders(200,response.length());
                    httpExchange.getResponseBody().write(response.getBytes());
                    httpExchange.getResponseBody().close();
                }
            });
            httpServer.createContext("/actuator/fill", new HttpHandler() {
                @Override
                public void handle(HttpExchange httpExchange) throws IOException {
                    Map<String,String> params=queryToMap(httpExchange.getRequestURI().getQuery());
                    double percentage=Double.valueOf(params.get("percentage"));
                    fillPercentage[0]=percentage;
                    panel.setFramerate(Integer.valueOf(params.get("framerate")));
                    int count=Integer.valueOf(params.get("count"));
                    while(count<lines.size())lines.remove(0);
                    while(count>lines.size())lines.add(new Line(dimension));

                    System.out.println("Reconfigure: fill="+percentage+", framerate="+Integer.valueOf(params.get("framerate"))+", count="+Integer.valueOf(params.get("count")));

                    String response="ok";
                    httpExchange.sendResponseHeaders(200,response.length());
                    httpExchange.getResponseBody().write(response.getBytes());
                    httpExchange.getResponseBody().close();
                }
            });
            httpServer.start();
        } catch (IOException e) {
            e.printStackTrace();
        }

        while(true){
            BufferedReader br=new BufferedReader(new InputStreamReader(System.in));
            try {
                fillPercentage[0]=Double.valueOf(br.readLine());
                //while(i<lines.size())lines.remove(0);
                //while(i>lines.size())lines.add(new Line(panel.getSize()));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    public static Map<String, String> queryToMap(String query){
        Map<String, String> result = new HashMap<String, String>();
        for (String param : query.split("&")) {
            String pair[] = param.split("=");
            if (pair.length>1) {
                result.put(pair[0], pair[1]);
            }else{
                result.put(pair[0], "");
            }
        }
        return result;
    }
}
