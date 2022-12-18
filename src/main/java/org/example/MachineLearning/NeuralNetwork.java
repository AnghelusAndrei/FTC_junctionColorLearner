package org.example.MachineLearning;

public class NeuralNetwork {
    private Layer[] layers;

    public NeuralNetwork(int[] layerSizes){
        layers = new Layer[layerSizes.length-1];
        for(int i = 0; i < layers.length; i++){
            layers[i] = new Layer(layerSizes[i], layerSizes[i+1]);
        }
    }

    public double[] CalculateOutputs(double[] inputs){
        for (Layer layer : layers) {
            inputs = layer.CalculateOutput(inputs);
        }
        return inputs;
    }

    int Classify(double inputs[]){
        double[] outputs = CalculateOutputs(inputs);
        if(outputs[1] > outputs[0])return 1;
        return 0;
    }

    double Cost(double[] input, double[] expectedOutputs){
        double[] outputs = CalculateOutputs(input);
        Layer outputLayer = layers[layers.length-1];

        double cost = 0;
        for(int nodeOut = 0;nodeOut < outputs.length; nodeOut++){
            cost += outputLayer.nodeCost(outputs[nodeOut], expectedOutputs[nodeOut]);
        }
        return cost;
    }

    public void Learn(double[] input, double[] expectedOutputs, double learnRate){
        final double h = 0.0001;
        double originalCost = Cost(input, expectedOutputs);

        for (Layer layer: layers) {
            for(int nodeOut = 0; nodeOut < layer.numNodesOut; nodeOut++){
                for(int nodeIn = 0; nodeIn < layer.numNodesIn; nodeIn++){
                    layer.weights[nodeIn][nodeOut] += h;
                    double deltaCost = Cost(input, expectedOutputs) - originalCost;
                    layer.weights[nodeIn][nodeOut] -= h;
                    layer.costGradientW[nodeIn][nodeOut] = deltaCost / h;
                }
            }

            for(int nodeOut = 0; nodeOut < layer.numNodesOut; nodeOut++){
                layer.biases[nodeOut] += h;
                double deltaCost = Cost(input, expectedOutputs) - originalCost;
                layer.biases[nodeOut] -= h;
                layer.costGradientB[nodeOut] = deltaCost / h;
            }
        }

        for(Layer layer: layers){
            layer.ApplyGradients(learnRate);
        }
    }

}
