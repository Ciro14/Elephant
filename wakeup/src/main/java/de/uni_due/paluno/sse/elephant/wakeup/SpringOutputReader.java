package de.uni_due.paluno.sse.elephant.wakeup;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.concurrent.Semaphore;

/**
 * Created by Ole on 23.10.2016.
 */
public class SpringOutputReader extends Thread {
    private String prefix;
    private Semaphore semaphore;
    private BufferedReader brOut,brErr;
    private boolean print=true;

    public SpringOutputReader(String prefix, Process process, Semaphore semaphore) {
        this.prefix = prefix;
        this.semaphore = semaphore;
        brOut=new BufferedReader(new InputStreamReader(process.getInputStream()));
        brErr=new BufferedReader(new InputStreamReader(process.getErrorStream()));
    }

    @Override
    public void run() {
        new Thread(){
            @Override
            public void run() {
                while(true){
                    try {
                        String line=brErr.readLine();
                        System.out.println(prefix+"\t => \t"+line);
                        if(line==null)System.exit(1);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }.start();
        while(true){
            try {
                String line=brOut.readLine();
                if(print)System.out.println(prefix+"\t => \t"+line);
                if(line!=null&&line.contains("Tomcat started on port"))semaphore.release();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void setPrint(boolean print) {
        this.print = print;
    }
}
