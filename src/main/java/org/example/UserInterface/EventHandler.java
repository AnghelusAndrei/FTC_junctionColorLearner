package org.example.UserInterface;

import org.example.EventMethods;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;

import java.awt.event.*;
import java.util.*;

public class EventHandler {


    EventHandler(Window window, Surface surface){
        EventMethods methods = new EventMethods();
        window.label.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent arg0) {
                if (arg0.getButton() == MouseEvent.BUTTON1){
                    //left click
                } else if (arg0.getButton() == MouseEvent.BUTTON2){
                    //middle button
                    Core.copyTo(surface.image, surface.matrix, surface.image);

                    surface.overlay.setTo(new Scalar(0));
                    surface.expectedImage.setTo(new Scalar(0));


                } else if (arg0.getButton() == MouseEvent.BUTTON3) {
                    //right click

                    methods.FloodFill(arg0.getY(), arg0.getX(), surface.expectedImage, surface.overlay, new Scalar(255), new Scalar(255));
                }
            }
        });
        window.label.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                Point point = new Point(e.getX(), e.getY());

                double x = point.x;
                double y = point.y;

                double height = (double)surface.image.rows() - ((double)window.cursorZoom * 2 * ((double)surface.image.rows()/(double)surface.image.cols()));
                double width = (double)surface.image.cols() - (double)window.cursorZoom * 2;

                double xn = window.cursurLocation.x - (width/2);
                double yn = window.cursurLocation.y - (height/2);
                xn = xn < 0 ? 0 : (xn > ((double)surface.image.cols() - width) ? ((double)surface.image.cols() - width) : xn);
                yn = yn < 0 ? 0 : (yn > ((double)surface.image.rows() - height) ? ((double)surface.image.rows() - height) : yn);

                x = (x/((double)surface.image.cols())) * width + xn;
                y = (y/((double)surface.image.rows())) * height + yn;


                double[] value = {255};
                surface.overlay.put((int)Math.round(y), (int)Math.round(x), value);
            }

            @Override
            public void mouseMoved(MouseEvent e){
            }
        });

        window.label.addMouseWheelListener(new MouseWheelListener() {
            @Override
            public void mouseWheelMoved(MouseWheelEvent e) {
                Point point = new Point(e.getX(), e.getY());
                window.cursurLocation = point;
                window.cursorZoom += e.getWheelRotation() * e.getScrollAmount();
                window.cursorZoom = (window.cursorZoom < 0) ? 0 : window.cursorZoom;
                window.cursorZoom = window.cursorZoom > (surface.image.cols() / 2 - 10) ? (surface.image.cols() / 2 - 10) : window.cursorZoom;
            }
        });
    }
}
