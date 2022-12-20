package org.example.UserInterface;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.util.*;

public class EventHandler {


    private class fillCoordonates{
        public int row;
        public int col;
        fillCoordonates(int i, int j){
            this.row = i;
            this.col = j;
        }
    }

    void FloodFill(int i, int j, Mat matToFill, Mat matToDetect, Scalar valueToFill, Scalar valueToDetect)
    {
        final int[] di = {1,0,-1,0};
        final int[] dj = {0,1,0,-1};
        int st = 0;
        int dr = 0;
        Queue<fillCoordonates> Q = new LinkedList<>();
        Q.add(new fillCoordonates(i,j));
        matToFill.put(i,j, valueToFill.val);

        while(!Q.isEmpty())
        {
            int y = Q.peek().row;
            int x = Q.peek().col;
            for(int k = 0 ; k < 4 ; k ++)
            {
                int iv = y + di[k];
                int jv = x + dj[k];
                Scalar value1 = new Scalar(0);
                Scalar value2 = new Scalar(0);
                value1.val = matToDetect.get(iv,jv);
                value2.val = matToFill.get(iv,jv);
                if(
                        iv >= 0 && iv < matToDetect.rows() &&
                        jv >= 1 && jv <= matToDetect.cols() &&
                        value2.val[0] != valueToFill.val[0] &&
                        value1.val[0] != valueToDetect.val[0]
                ){
                    matToFill.put(iv,jv, valueToFill.val);
                    Q.add(new fillCoordonates(iv,jv));
                }
            }
            Q.poll();
        }
    }

    EventHandler(Window window, Surface surface){
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

                    FloodFill(arg0.getY(), arg0.getX(), surface.expectedImage, surface.overlay, new Scalar(255), new Scalar(255));//fara stack sau alte chestii de optimizare

                    //openCv method: (not working)
                    /*Core.copyTo(overlay,mask,overlay);
                    Core.copyTo(overlay,expectedImage,overlay);
                    Core.copyMakeBorder(mask, mask, 1, 1, 1, 1, Core.BORDER_REPLICATE);
                    System.out.println("pos: " + arg0.getX() + " & " + arg0.getY());
                    Imgproc.floodFill(expectedImage, mask, new Point(arg0.getX(), arg0.getY()), new Scalar(255), new Rect(), new Scalar(0), new Scalar(0), 4 | Imgproc.FLOODFILL_MASK_ONLY | (255 << 8));
                    Core.subtract(expectedImage, overlay, expectedImage);
                     */
                }
            }
        });
        window.label.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                Point point = new Point(e.getX(), e.getY());
                Imgproc.circle(surface.overlay, point, 1, new Scalar(255),-1);
            }

            @Override
            public void mouseMoved(MouseEvent e) {

            }
        });
    }
}
