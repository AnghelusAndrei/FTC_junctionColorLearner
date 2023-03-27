package org.example;

import basicneuralnetwork.NeuralNetwork;
import org.opencv.core.*;
import org.opencv.imgproc.Imgproc;
import org.opencv.videoio.VideoCapture;
import org.opencv.videoio.Videoio;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.*;

public class CoreMethods {

    public String filename;
    public String name = "network";
    public String dir = "networks/";

    public CoreMethods(){
        filename = "";
    }

    public void FloodFill(int i, int j, Mat matToFill, Mat matToDetect)
    {
        Mat mat = new Mat();
        Core.copyMakeBorder(matToDetect, mat, 1,1,1,1,Core.BORDER_DEFAULT, new Scalar(255));
        Imgproc.floodFill(matToFill,mat,new Point(j,i), new Scalar(255));

    }

    public NeuralNetwork LoadSpecific(String file) {
        return NeuralNetwork.readFromFile(file);
    }

    private class LearningInput {
        public double[] input;
        public double expectedValue;

        @Override
        public int hashCode() {
            return Arrays.hashCode(input);
        }
    }

    public void LearnUntil(Mat data, Mat expectedOutput, Mat ignored, NeuralNetwork network, double expectedCost){
        double cost = CalculateCost(data, ignored, expectedOutput, network);
        System.out.println(cost);
        while(cost > expectedCost){
            Learn(data, expectedOutput, ignored, network);
            cost = CalculateCost(data, ignored, expectedOutput, network);
        }
    }

    public void Learn(Mat data, Mat expectedOutput, Mat ignored, NeuralNetwork network){
        System.out.println("Started learning from current training data");

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
        int inputSize=targetInputsList.size();
        List<LearningInput> nonTargetInputsList = new ArrayList<>(nonTargetInputs.stream().toList());

        targetInputsList.addAll(nonTargetInputsList);
        Collections.shuffle(targetInputsList);
        for(LearningInput input: targetInputsList)
        {
            network.train(input.input, new double[]{input.expectedValue});
        }
        System.out.printf("Finished learning from current training data, learned pixels: junction: %d, non-junction: %d, intersection: %d%n",inputSize,nonTargetInputsList.size(), intersectionSize);
    }


    public void SaveNeuralNetwork(NeuralNetwork network){
        if(filename != ""){
            network.writeToFile(filename);
            System.out.println("Saved network");
            return;
        }

        SaveNewNeuralNetwork(network);
    }

    public void SaveNewNeuralNetwork(NeuralNetwork network){
        int i;
        for(i = 0; (new File(dir+name+String.valueOf(i)+".json")).exists(); i++){}


        network.writeToFile(dir+name+String.valueOf(i));

        System.out.println("Saved new network ( "+dir+name+String.valueOf(i)+".json)");
    }

    public NeuralNetwork LoadNeuralNetwork(Mat data, Mat ignored, Mat expecedOutput, NeuralNetwork network){
        double costMin = 1.01;
        NeuralNetwork best = network.copy();
        String bestFilename = filename;

        int i = 0;
        while(true){
            File file = new File(dir+name+String.valueOf(i)+".json");
            if(file.exists()){
                NeuralNetwork n = network.readFromFile(dir+name+String.valueOf(i)+".json");

                double cost = CalculateCost(data, ignored, expecedOutput, n);

                if(cost < costMin){
                    best = n.copy();
                    costMin = cost;
                    bestFilename = dir+name+String.valueOf(i);
                }

                i++;
            }else{
                break;
            }
        }

        System.out.println("Finished loading best network (loaded " + bestFilename + ".json)");

        filename = bestFilename;
        return best;
    }

    public void CustomAutoExposure(VideoCapture capture, int initialExposure, double treshold){

        Mat matrix = new Mat();
        matrix.setTo(new Scalar(0,0,0));
        capture.read(matrix);

        for(int i = 0; i < matrix.rows(); i++) {
            for (int j = 0; j < matrix.cols(); j++) {

                double[] pixel = matrix.get(i,j);

                double averegedLight = (pixel[0] + pixel[1] + pixel[2])/3;

                while(averegedLight > treshold){
                    System.out.println("Found pixel to correct");
                    initialExposure--;
                    capture.set(Videoio.CAP_PROP_EXPOSURE, initialExposure);
                    capture.read(matrix);

                    pixel = matrix.get(i,j);
                    averegedLight = (pixel[0] + pixel[1] + pixel[2])/3;
                }
            }
        }

        System.out.println("Finished setting exposure " + initialExposure);
    }


    public double CalculateCost(Mat data, Mat ignored, Mat expectedOutput, NeuralNetwork network){
        double cost = 0;
        int samples = 0;

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

                double[] output = network.guess(inputs);

                cost += Math.abs(expectedOutputs[0] - output[0]);
                samples++;

            }
        }

        cost /= samples;
        return cost;
    }

    public void ExportLUT(NeuralNetwork network){
        System.out.println("Started exporting LUT");
        File file = new File("LUT.dat");
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
                        fileOutputStream.write(Byte.toUnsignedInt((byte)(guess[0]*255.0)));
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
