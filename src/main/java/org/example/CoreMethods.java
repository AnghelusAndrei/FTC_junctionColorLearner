package org.example;

import basicneuralnetwork.NeuralNetwork;
import basicneuralnetwork.activationfunctions.ActivationFunction;
import org.opencv.core.*;
import org.opencv.imgproc.Imgproc;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.*;

public class CoreMethods {

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
        HashSet<LearningInput> targetInputs = new HashSet<>();
        HashSet<LearningInput> nonTargetInputs = new HashSet<>();

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
                    targetInputs.add(new LearningInput(){{input=inputs; expectedValue=1;}});
                }else nonTargetInputs.add(new LearningInput(){{input=inputs; expectedValue=0;}});

            }
        }
        int intersectionSize = nonTargetInputs.size();
        nonTargetInputs.removeAll(targetInputs);
        intersectionSize-=nonTargetInputs.size();
        List<LearningInput> targetInputsList = new ArrayList<>(targetInputs.stream().toList());
        List<LearningInput> nonTargetInputsList = new ArrayList<>(nonTargetInputs.stream().toList());

        targetInputsList.addAll(nonTargetInputsList);
        Collections.shuffle(targetInputsList);
        for(LearningInput input: targetInputsList)
        {
            network.train(input.input, new double[]{input.expectedValue});
        }
        System.out.printf("Finished learning from current training data, learned pixels: junction: %d, non-junction: %d, intersection: %d%n", targetInputsList.size(),nonTargetInputsList.size(), intersectionSize);
    }

    public void SaveNeuralNetwork(NeuralNetwork network){
        network.writeToFile();
    }
    public void LoadNeuralNetwork(NeuralNetwork network){
        network = NeuralNetwork.readFromFile();
    }
    public void ExportLUT(NeuralNetwork network){
        System.out.println("Started exporting LUT");
        File file = new File("LUT.txt");
        try {
            file.createNewFile();
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }

        try {
            FileOutputStream fileOutputStream = new FileOutputStream(file);
            OutputStreamWriter streamWriter = new OutputStreamWriter(fileOutputStream);
            double[] data = new double[3];
            for(int i=0;i<256;i++)
            {
                for(int j=0;j<256;j++)
                {
                    for(int k=0;k<256;k++)
                    {

                        data[0]=i/255.0;
                        data[1]=j/255.0;
                        data[2]=k/255.0;
                        double[] guess = network.guess(data);
                        if(guess[0]>0.75)
                        {
                            streamWriter.write(1);
                        }else {
                            streamWriter.write(0);
                        }
                    }
                }
            }
            streamWriter.close();
            fileOutputStream.close();
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
        System.out.println("Finished exporting LUT");
    }
}
