package org.example;

import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.imgcodecs.Imgcodecs;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;

public class Window {
    public JFrame imageJframe;
    public JLabel label;

    Window(JFrame imageJframe, JLabel label){
        this.imageJframe = imageJframe;
        this.label = label;
    }

    void init(Mat window_surface) throws Exception{
        try{
            run(window_surface);
            imageJframe.getContentPane().add(label);
            imageJframe.pack();
            imageJframe.setVisible(true);
        }catch (Exception e){
            throw new RuntimeException(e);
        }
    }

    void run(Mat window_surface)throws Exception{
        try{
            label.setIcon(new ImageIcon(Mat2BufferedImage(window_surface)));
        }catch (Exception e){
            throw new RuntimeException(e);
        }
    }



    BufferedImage Mat2BufferedImage(Mat matrix)throws Exception {
        MatOfByte mob=new MatOfByte();
        Imgcodecs.imencode(".jpg", matrix, mob);
        byte ba[]=mob.toArray();

        BufferedImage bi= ImageIO.read(new ByteArrayInputStream(ba));
        return bi;
    }
}
