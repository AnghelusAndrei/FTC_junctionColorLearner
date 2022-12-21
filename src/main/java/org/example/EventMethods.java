package org.example;

import basicneuralnetwork.NeuralNetwork;
import basicneuralnetwork.activationfunctions.ActivationFunction;
import org.opencv.core.*;
import org.opencv.imgproc.Imgproc;

import java.util.*;

public class EventMethods {
    private class FillCoordinates {
        public int row;
        public int col;
        FillCoordinates(int i, int j){
            this.row = i;
            this.col = j;
        }
    }

    public void FloodFill(int i, int j, Mat matToFill, Mat matToDetect)
    {
        Mat mat = new Mat();
        Core.copyMakeBorder(matToDetect, mat, 1,1,1,1,Core.BORDER_DEFAULT, new Scalar(255));
        Imgproc.floodFill(matToFill,mat,new Point(j,i), new Scalar(255));
        
    }
    private class LearningInput {
        public double[] input;
        public double expectedValue;

        @Override
        public int hashCode() {
            return Arrays.hashCode(input);
        }
    }
    public void Learn(Mat data, Mat expectedOutput, Mat ignored, NeuralNetwork network){
        System.out.println("Started learning from current training data");


        network.setLearningRate(0.04);
        network.setActivationFunction(ActivationFunction.SIGMOID);
        HashSet<LearningInput> junctionInputs = new HashSet<>();
        HashSet<LearningInput> nonJunctionInputs = new HashSet<>();

        for(int i = 0; i < data.rows(); i++){
            for(int j = 0; j < data.cols(); j++){
                double[] ignored_data = ignored.get(i,j);
                double[] inputs = data.get(i,j);
                if((inputs[0] == 0 && inputs[1] == 0 && inputs[2] == 0) || ignored_data[0] > 100) {continue;}
                inputs[0] /= 255.0;
                inputs[1] /= 255.0;
                inputs[2] /= 255.0;
                double[] expectedOutputs = expectedOutput.get(i,j);
                if(expectedOutputs[0]==255.0)
                {
                    junctionInputs.add(new LearningInput(){{input=inputs; expectedValue=1;}});
                }else nonJunctionInputs.add(new LearningInput(){{input=inputs; expectedValue=0;}});

            }
        }
        int intersectionSize = nonJunctionInputs.size();
        nonJunctionInputs.removeAll(junctionInputs);
        intersectionSize-=nonJunctionInputs.size();
        List<LearningInput> junctionInputsList = new ArrayList<>(junctionInputs.stream().toList());
        List<LearningInput> nonJunctionInputsList = new ArrayList<>(nonJunctionInputs.stream().toList());

        junctionInputsList.addAll(nonJunctionInputsList);
        Collections.shuffle(junctionInputsList);
        for(LearningInput input: junctionInputsList)
        {
            network.train(input.input, new double[]{input.expectedValue});
        }
        System.out.printf("Finished learning from current training data, learned pixels: junction: %d, non-junction: %d, intersection: %d%n", junctionInputsList.size(),nonJunctionInputsList.size(), intersectionSize);
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
