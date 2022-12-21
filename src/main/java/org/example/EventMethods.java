package org.example;

import basicneuralnetwork.NeuralNetwork;
import basicneuralnetwork.activationfunctions.ActivationFunction;
import org.opencv.core.Mat;
import org.opencv.core.Scalar;

import java.util.LinkedList;
import java.util.Queue;

public class EventMethods {
    private class FillCoordinates {
        public int row;
        public int col;
        FillCoordinates(int i, int j){
            this.row = i;
            this.col = j;
        }
    }

    public void FloodFill(int i, int j, Mat matToFill, Mat matToDetect, Scalar valueToFill, Scalar valueToDetect)
    {
        Mat mat = new Mat();
        Core.copyMakeBorder(matToDetect, mat, 1,1,1,1,Core.BORDER_DEFAULT, new Scalar(255));
        Imgproc.floodFill(matToFill,mat,new Point(j,i), new Scalar(255));
        
    }

    public void Learn(Mat data, Mat expectedOutput, Mat ignored, NeuralNetwork network){
        System.out.println("Started learning from current training data");
        int q = 0;

        network.setLearningRate(0.04);
        network.setActivationFunction(ActivationFunction.SIGMOID);

        for(int i = 0; i < data.rows(); i++){
            for(int j = 0; j < data.cols(); j++){
                double[] ignored_data = ignored.get(i,j);
                double[] inputs = data.get(i,j);
                if((inputs[0] == 0 && inputs[1] == 0 && inputs[2] == 0) || ignored_data[0] > 100) {continue;}
                inputs[0] /= 255.0;
                inputs[1] /= 255.0;
                inputs[2] /= 255.0;
                double[] expectedOutputs = expectedOutput.get(i,j);
                expectedOutputs[0] /= 255.0;
                q++;
                network.train(inputs, expectedOutputs);
            }
        }
        System.out.println("Finished learning from current training data, learned pixels: " + q);
    }

    public double[][][] lookupTable(NeuralNetwork network){
        double[][][] table = new double[255][255][255];
        for(int i = 0; i < 255; i++){
            for(int j = 0; j < 255; j++){
                for(int k = 0; k < 255; k++){
                    double[] inputs = new double[3];
                    inputs[0] = i;
                    inputs[1] = j;
                    inputs[2] = k;
                    double[] outputs = network.guess(inputs);
                    table[i][j][k] = outputs[0];
                }
            }
        }
        return table;
    }
}
