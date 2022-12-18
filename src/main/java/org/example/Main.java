package org.example;
import org.example.MachineLearning.NeuralNetwork;
import org.example.components.MultiLayerImageView;
import org.opencv.core.*;
import org.opencv.highgui.HighGui;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.video.Video;
import org.opencv.videoio.VideoCapture;
import org.opencv.videoio.Videoio;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.io.*;
import java.util.Objects;
import java.awt.image.BufferedImage;

public class Main {
    private static final int FRAME_WIDTH=800;
    public static final int FRAME_HEIGHT = 448;


    static BufferedImage Mat2BufferedImage(Mat matrix)throws Exception {
        MatOfByte mob=new MatOfByte();
        Imgcodecs.imencode(".png", matrix, mob);
        byte ba[]=mob.toArray();

        BufferedImage bi= ImageIO.read(new ByteArrayInputStream(ba));

        return bi;
    }
    public static void main(String[] args) throws Exception {
        try {
                System.loadLibrary(Core.NATIVE_LIBRARY_NAME);

        }catch (UnsatisfiedLinkError e){
                System.out.println("Native OpenCV not found");
                OSUtil.OS os = OSUtil.getOS();
                try(InputStream libraryInputStream=Main.class.getClassLoader().getResourceAsStream(os.getLibraryName()+os.getLibrarySuffix()))
                {
                    File libraryTempFile = File.createTempFile(os.getLibraryName(), os.getLibrarySuffix());
                    try(FileOutputStream libraryTempFileOutputStream = new FileOutputStream(libraryTempFile))
                    {

                        Objects.requireNonNull(libraryInputStream).transferTo(libraryTempFileOutputStream);
                    }
                    libraryTempFile.deleteOnExit();
                    System.load(libraryTempFile.getAbsolutePath());
                }catch (IOException ex)
                {
                    ex.printStackTrace();
                }

        }

        System.out.println("Loaded OpenCV");

        int[] layerSizes = new int[2];
        layerSizes[0] = 5;
        layerSizes[1] = 2;

        NeuralNetwork neuralNetwork = new NeuralNetwork(layerSizes);

        VideoCapture capture = new VideoCapture(0);
        capture.set(Videoio.CAP_PROP_FRAME_WIDTH, 800);
        capture.set(Videoio.CAP_PROP_FRAME_HEIGHT, 448);
        Mat matrix = new Mat();

        capture.read(matrix);
        capture.release();
        JFrame imageJframe = HighGui.createJFrame("Vision training data", JFrame.EXIT_ON_CLOSE);

        Imgproc.cvtColor(matrix,matrix, Imgproc.COLOR_BGR2RGBA);
        MultiLayerImageView layerImageView = new MultiLayerImageView();

        layerImageView.layers.add(Mat2BufferedImage(matrix));
        Mat matrix2 = new Mat(matrix.size(), CvType.CV_8UC4, new Scalar(0,0,0,0));
        //layerImageView.layers.add(Mat2BufferedImage(matrix2));
        layerImageView.addMouseMotionListener(new MouseMotionListener() {
            @Override
            public void mouseDragged(MouseEvent e) {
                Point point = new Point(e.getX(), e.getY());
                Imgproc.circle(matrix2, point, 1, new Scalar(255,255,255),-1);
            }

            @Override
            public void mouseMoved(MouseEvent e) {

            }
        });
        imageJframe.getContentPane().add(layerImageView);
        imageJframe.pack();
        imageJframe.setVisible(true);


        while(true){
//            layerImageView.layers.set(1, Mat2BufferedImage(matrix2));
        }


    }
}