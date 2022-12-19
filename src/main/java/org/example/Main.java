package org.example;
import org.example.MachineLearning.NeuralNetwork;
import org.opencv.core.*;
import org.opencv.highgui.HighGui;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.videoio.VideoCapture;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.desktop.UserSessionEvent;
import java.awt.event.*;
import java.io.*;
import java.util.Objects;
import java.awt.image.BufferedImage;
import java.util.Vector;

import static org.opencv.core.CvType.CV_8U;

public class Main {

    private static final int[] di = {1,0,-1,0};
    private static final int[] dj = {0,1,0,-1};

    private static void Learn(Mat data, Mat expectedOutput, NeuralNetwork network){
        for(int i = 0; i < data.rows(); i++){
            for(int j = 0; j < data.cols(); j++){
                double[] inputs = data.get(i,j);
                inputs[0] /= 255;
                inputs[1] /= 255;
                inputs[2] /= 255;
                double[] expectedOutputs = expectedOutput.get(i,j);
                expectedOutputs[0] /= 255;
                expectedOutputs[1] = 1-expectedOutputs[0];
                network.Learn(inputs, expectedOutputs, 0.2);
            }
        }
    }

    private static double[][][] lookupTable(NeuralNetwork network){
        double[][][] table = new double[255][255][255];
        for(int i = 0; i < 255; i++){
            for(int j = 0; j < 255; j++){
                for(int k = 0; k < 255; k++){
                    double[] inputs = new double[3];
                    inputs[0] = i;
                    inputs[1] = j;
                    inputs[2] = k;
                    double[] outputs = network.CalculateOutputs(inputs);
                    table[i][j][k] = outputs[0];
                }
            }
        }
        return table;
    }

    static BufferedImage Mat2BufferedImage(Mat matrix)throws Exception {
        MatOfByte mob=new MatOfByte();
        Imgcodecs.imencode(".jpg", matrix, mob);
        byte ba[]=mob.toArray();

        BufferedImage bi= ImageIO.read(new ByteArrayInputStream(ba));
        return bi;
    }

    private static void FloodFill(int i, int j, Mat matToFill, Mat matToDetect, Scalar valueToFill, Scalar valueToDetect){
        if(i>=0 && i<matToDetect.rows() && j>=0 && j<matToDetect.cols()){
            Scalar value1 = new Scalar(0);
            Scalar value2 = new Scalar(0);
            value1.val = matToDetect.get(i,j);
            value2.val = matToFill.get(i,j);

            if(value1.val[0] != valueToDetect.val[0] && value2.val[0] != valueToDetect.val[0]){
                matToFill.put(i,j, valueToFill.val);
                for(int k = 0; k < 4; k++){
                    FloodFill(i + di[k], j + dj[k], matToFill, matToDetect, valueToFill, valueToDetect);
                }
            }
        }
    }

    public static void main(String[] args) {
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


        VideoCapture capture = new VideoCapture(0);

        Mat matrix;
        Mat image = new Mat();

        int[] layerSizes = new int[2];
        layerSizes[0] = 5;
        layerSizes[1] = 2;



        NeuralNetwork neuralNetwork = new NeuralNetwork(layerSizes);

        int[] trainingIndex = new int[2];
        boolean training = false;


        capture.read(image);

        matrix = new Mat(image.rows(), image.cols(), image.type());

        Core.copyTo(image, matrix, image);

        Mat expectedImage = new Mat(image.rows(), image.cols(), CV_8U);
        Mat expectedImage_rgb = new Mat(image.rows(), image.cols(), image.type());
        Mat window_surface = new Mat(image.rows(), image.cols(), image.type());
        Mat overlay = new Mat(image.rows(), image.cols(), CV_8U);
        Mat overlay_rgb = new Mat(image.rows(), image.cols(), image.type());
        Mat mask = new Mat();

        JFrame imageJframe = HighGui.createJFrame("Vision training data", JFrame.EXIT_ON_CLOSE);
        JLabel label = new JLabel();

        label.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent arg0) {
                if (arg0.getButton() == MouseEvent.BUTTON1){
                    //left click
                } else if (arg0.getButton() == MouseEvent.BUTTON2){
                    //middle button
                    Core.copyTo(image, matrix, image);

                    overlay.setTo(new Scalar(0));
                    expectedImage.setTo(new Scalar(0));


                } else if (arg0.getButton() == MouseEvent.BUTTON3) {
                    //right click

                    FloodFill(arg0.getY(), arg0.getX(), expectedImage, overlay, new Scalar(255), new Scalar(255));//fara stack sau alte chestii de optimizare

                    //openCv method:
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
        label.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                Point point = new Point(e.getX(), e.getY());
                Imgproc.circle(overlay, point, 1, new Scalar(255),-1);
            }

            @Override
            public void mouseMoved(MouseEvent e) {

            }
        });

        try {
            Core.copyTo(matrix, window_surface, matrix);
            label.setIcon(new ImageIcon(Mat2BufferedImage(window_surface)));

            imageJframe.getContentPane().add(label);
            imageJframe.pack();
            imageJframe.setVisible(true);


            while(capture.read(image)){
                Imgproc.cvtColor(overlay, overlay_rgb, Imgproc.COLOR_GRAY2RGB);
                Imgproc.cvtColor(expectedImage, expectedImage_rgb, Imgproc.COLOR_GRAY2RGB);
                Core.copyTo(matrix, window_surface, matrix);
                Core.copyTo(overlay_rgb, window_surface, overlay);
                Core.copyTo(expectedImage_rgb, window_surface, expectedImage);

                label.setIcon(new ImageIcon(Mat2BufferedImage(window_surface)));
            }

            capture.release();

        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }
}