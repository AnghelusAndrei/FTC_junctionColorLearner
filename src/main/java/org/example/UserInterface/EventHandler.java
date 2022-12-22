package org.example.UserInterface;

import basicneuralnetwork.NeuralNetwork;
import org.example.CoreMethods;
import org.opencv.core.Core;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;

import java.awt.event.*;

public class EventHandler {


    EventHandler(Window window, Surface surface, NeuralNetwork network){
        CoreMethods methods = new CoreMethods();

        window.label.setFocusable(true);

        window.label.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {
            }

            @Override
            public void keyPressed(KeyEvent e) {
                if(e.getKeyCode() == KeyEvent.VK_ENTER){
                    methods.Learn(surface.matrix, surface.expectedImage, surface.overlay, network);
                }else if(e.getKeyCode() == KeyEvent.VK_BACK_SPACE){
                    Core.copyTo(surface.image, surface.matrix, surface.image);

                    surface.overlay.setTo(new Scalar(0));
                    surface.expectedImage.setTo(new Scalar(0));
                }else if(e.getKeyCode() == KeyEvent.VK_SHIFT){
                    window.isShift = true;
                } else if(e.getKeyCode()==KeyEvent.VK_E) {
                    methods.ExportLUT(network);
                }


            }

            @Override
            public void keyReleased(KeyEvent e) {
                window.isShift = false;
            }
        });
        window.label.addMouseListener(new MouseAdapter() {

            Point lastPoint;
            @Override
            public void mousePressed(MouseEvent arg0) {
                if (arg0.getButton() == MouseEvent.BUTTON1 && window.isShift){
                    //left click
                    Point point = new Point(arg0.getX(), arg0.getY());

                    double x = point.x;
                    double y = point.y;

                    double height = (double)surface.image.rows() - ((double)window.cursorZoom * 2 * ((double)surface.image.rows()/(double)surface.image.cols()));
                    double width = (double)surface.image.cols() - (double)window.cursorZoom * 2;

                    double xn = window.cursurLocation.x - (width/2);
                    double yn = window.cursurLocation.y - (height/2);
                    xn = xn < 0 ? 0 : (xn > ((double)surface.image.cols() - width) ? ((double)surface.image.cols() - width) : xn);
                    yn = yn < 0 ? 0 : (yn > ((double)surface.image.rows() - height) ? ((double)surface.image.rows() - height) : yn);

                    point.x= (x/((double)surface.image.cols())) * width + xn;
                    point.y = (y/((double)surface.image.rows())) * height + yn;

                    if(lastPoint!=null)
                    {
                        // y = m*x+n
                        Point vector = new Point(point.x- lastPoint.x, point.y- lastPoint.y);
                        double length = Math.sqrt((vector.x* vector.x+ vector.y* vector.y));
                        vector.x/=length;
                        vector.y/=length;

                        for(double i=0;i<length;i+=0.5)
                        {
                            Imgproc.circle(surface.overlay, new Point(lastPoint.x+vector.x*i, lastPoint.y+ vector.y*i), 1, new Scalar(255));
                        }
                        lastPoint=null;
                    }else{
                        lastPoint=point;
                        Imgproc.circle(surface.overlay, point, 1, new Scalar(255));
                    }
                } else if (arg0.getButton() == MouseEvent.BUTTON2){
                    //middle button

                    double[] data = surface.matrix.get(Math.round(arg0.getY()), Math.round(arg0.getX()));
                    data[0]/=255.0;
                    data[1]/=255.0;
                    data[2]/=255.0;
                    double[] guess = network.guess(data);
                    System.out.println(data[0] + ", " + data[1] + ", " + data[2] + " : " + guess[0]);

                } else if (arg0.getButton() == MouseEvent.BUTTON3) {
                    //right click
                    Point point = new Point(arg0.getX(), arg0.getY());

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

                    methods.FloodFill((int)Math.round(y), (int)Math.round(x), surface.expectedImage, surface.overlay);
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


                Imgproc.circle(surface.overlay, new Point(x, y), 1, new Scalar(255));
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
