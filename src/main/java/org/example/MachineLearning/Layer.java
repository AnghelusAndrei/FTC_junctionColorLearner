package org.example.MachineLearning;

import java.util.Random;

public class Layer {
    public int numNodesIn,numNodesOut;
    public double[][] weights;
    public double[] biases;


    //gradient descent
    public double[][] costGradientW;
    public double[] costGradientB;

    Layer(int numNodesIn, int numNodesOut){
        this.numNodesIn = numNodesIn;
        this.numNodesOut = numNodesOut;

        weights = new double[numNodesIn][numNodesOut];
        costGradientW = new double[numNodesIn][numNodesOut];
        biases = new double[numNodesOut];
        costGradientB = new double[numNodesOut];

        InitializeRandomWeights();
    }

    private void InitializeRandomWeights(){
        Random random = new Random();
        for(int nodeOut = 0; nodeOut < numNodesOut; nodeOut++){
            for (int nodeIn = 0; nodeIn < numNodesIn; nodeIn++){
                weights[nodeIn][nodeOut] = random.nextDouble();
                costGradientW[nodeIn][nodeOut] = random.nextDouble();
            }
            biases[nodeOut] = random.nextDouble();
            costGradientB[nodeOut] = random.nextDouble();
        }
    }

    public void ApplyGradients(double learnRate){
        for(int nodeOut = 0; nodeOut < numNodesOut; nodeOut++){
            biases[nodeOut] -= costGradientB[nodeOut] * learnRate;
            for(int nodeIn = 0; nodeIn < numNodesIn; nodeIn++){
                weights[nodeIn][nodeOut] -= costGradientW[nodeIn][nodeOut] * learnRate;
            }
        }
    }

    public double[] CalculateOutput(double[] inputs){
        double[] weightedInputs = new double[numNodesOut];

        for(int nodeOut = 0; nodeOut < numNodesOut; nodeOut++){
            weightedInputs[nodeOut] = biases[nodeOut];
            for(int nodeIn = 0; nodeIn < numNodesIn; nodeIn++){
                weightedInputs[nodeOut] += inputs[nodeIn] * weights[nodeIn][nodeOut];
            }
            weightedInputs[nodeOut] = ActivationFunction(weightedInputs[nodeOut]);
        }

        return weightedInputs;
    }

    private double ActivationFunction(double weightedInput){
        return 1/(1+Math.exp(-weightedInput));//sigmoid
    }

    public double nodeCost(double output, double expectedOutput){
        return (output-expectedOutput)*(output-expectedOutput);
    }
}
