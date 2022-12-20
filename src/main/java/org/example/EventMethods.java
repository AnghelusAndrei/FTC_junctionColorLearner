package org.example;

import org.example.MachineLearning.NeuralNetwork;
import org.opencv.core.Mat;
import org.opencv.core.Scalar;

import java.util.LinkedList;
import java.util.Queue;

public class EventMethods {
    private class fillCoordonates{
        public int row;
        public int col;
        fillCoordonates(int i, int j){
            this.row = i;
            this.col = j;
        }
    }

    public void FloodFill(int i, int j, Mat matToFill, Mat matToDetect, Scalar valueToFill, Scalar valueToDetect)
    {
        final int[] di = {1,0,-1,0};
        final int[] dj = {0,1,0,-1};
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
}
