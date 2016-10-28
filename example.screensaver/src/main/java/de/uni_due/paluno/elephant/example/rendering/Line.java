package de.uni_due.paluno.elephant.example.rendering;

import java.awt.*;
import java.util.Random;

/**
 * Created by Ole Meyer
 */
public class Line {

    private static final double M=0.2;

    private double[] position;
    private double[] speed;
    private double[] color;
    private double[] colorSpeed;


    public Line(Dimension dimension) {
        position=new double[4];
        for(int i=0;i<position.length;i+=2){
            position[i]=Math.random()*dimension.getWidth();
            position[i+1]=Math.random()*dimension.getHeight();
        }
        speed=new double[]{(Math.random()-.5)*M,(Math.random()-.5)*M,(Math.random()-.5)*M,(Math.random()-.5)*M};
        color=new double[]{Math.random()*255,Math.random()*255,Math.random()*255,Math.random()*255};
        colorSpeed=new double[]{.01,.01,.01,.01};
    }

    public void move(Dimension dimension,int framerate){
        for(int i=0;i<position.length;i+=2){
            position[i]+=speed[i]*1000/framerate;
            if(position[i]>dimension.getWidth()){
                speed[i]*=-1;
                position[i]=dimension.getWidth();
            }else if(position[i]<0){
                speed[i]*=-1;
                position[i]=0;
            }
            position[i+1]+=speed[i+1]*1000/framerate;
            if(position[i+1]>dimension.getHeight()){
                speed[i+1]*=-1;
                position[i+1]=dimension.getHeight();
            }else if(position[i+1]<0){
                speed[i+1]*=-1;
                position[i+1]=0;
            }
        }

        for(int i=0;i<color.length;++i){
            color[i]+=colorSpeed[i];
            if(color[i]>255){
                color[i]=255;
                colorSpeed[i]*=-1;
            }else if(color[i]<0){
                color[i]=0;
                colorSpeed[i]*=-1;
            }
        }

    }
    public void draw(Graphics graphics,boolean fill){
        graphics.setColor(new Color((int)color[0],(int)color[1],(int)color[2],(int)color[3]));
        if(fill){
            graphics.fillRect((int)position[0],(int)position[1],(int)position[2],(int)position[3]);
            //graphics.fillOval((int)position[0],(int)position[1],(int)position[2],(int)position[3]);

        }else{
            graphics.drawRect((int)position[0],(int)position[1],(int)position[2],(int)position[3]);
            //graphics.drawOval((int)position[0],(int)position[1],(int)position[2],(int)position[3]);

        }

    }
}
