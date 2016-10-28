package de.uni_due.paluno.elephant.example.rendering;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Created by ole on 05.10.16.
 */
public abstract class DrawablePanel extends JPanel implements ActionListener {

    private int framerate=500;


    private Timer timer=new Timer(1000/framerate,this);

    public DrawablePanel() {
        this.timer.start();
    }

    @Override
    public void actionPerformed(ActionEvent actionEvent) {
        if(actionEvent.getSource()==timer){
            repaint();
        }
    }

    public void setFramerate(int framerate){
        this.framerate=framerate;
        timer.setDelay(1000/framerate);
    }
    public int getFramerate(){
        return this.framerate;
    }

}
