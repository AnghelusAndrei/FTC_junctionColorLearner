package org.example;
import org.example.MachineLearning.NeuralNetwork;
import org.example.UserInterface.UI;
import org.opencv.core.*;
import org.opencv.highgui.HighGui;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.videoio.VideoCapture;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.event.*;
import java.io.*;
import java.util.Objects;
import java.awt.image.BufferedImage;

import static org.opencv.core.CvType.CV_8U;

public class Main {



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
                            //the input layer is not an actual layer
        layerSizes[0] = 5;  //first hidden layer
        layerSizes[1] = 2;  //the output layer


        NeuralNetwork neuralNetwork = new NeuralNetwork(layerSizes);


        VideoCapture capture = new VideoCapture(0);

        UI userInterface = new UI(capture);
        userInterface.run();

        capture.release();
    }
}