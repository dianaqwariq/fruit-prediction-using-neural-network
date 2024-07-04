package com.example.fruit_rd;

import java.util.Arrays;

public class NeuralNetwork {

    private double[][] weightsInputHidden;
    private double[][] weightsHiddenOutput;

    private String hiddenActivation;

    public NeuralNetwork(int inputNeurons, int hiddenNeurons, int outputNeurons, String hiddenActivation) {
        this.hiddenActivation = hiddenActivation;
        initializeWeights(inputNeurons, hiddenNeurons, outputNeurons);//to set random initial weights.
    }
    public double calculateAccuracy(double[][] inputs, double[][] expectedOutputs) {
        double[][] predictedOutputs = predict(inputs);
        int correctPredictions = 0;

        for (int i = 0; i < inputs.length; i++) {
            int expectedClassIndex = getClassIndex(expectedOutputs[i]);
            int predictedClassIndex = getClassIndex(predictedOutputs[i]);

            if (expectedClassIndex == predictedClassIndex) {
                correctPredictions++;
            }
        }

        return (double) correctPredictions / inputs.length;
    }
    private int getClassIndex(double[] output) {
        int maxIndex = 0;
        for (int i = 1; i < output.length; i++) {
            if (output[i] > output[maxIndex]) {
                maxIndex = i;
            }
        }
        return maxIndex;
    }
    //[o,o,1] apple

    private void initializeWeights(int inputNeurons, int hiddenNeurons, int outputNeurons) {
        // Initialize weights with random values
        weightsInputHidden = new double[hiddenNeurons][inputNeurons];
        weightsHiddenOutput = new double[outputNeurons][hiddenNeurons];

        // Initialize weights with random values
        initializeRandomWeights(weightsInputHidden);
        initializeRandomWeights(weightsHiddenOutput);
    }

    private void initializeRandomWeights(double[][] weights) {
        for (int i = 0; i < weights.length; i++) {
            for (int j = 0; j < weights[0].length; j++) {
                weights[i][j] = Math.random();
            }
        }
    }

    private double[] applyActivationFunction(double[] input, String activation) {
        double[] result = new double[input.length];
        switch (activation) {
            case "sigmoid":
                for (int i = 0; i < input.length; i++) {
                    result[i] = sigmoid(input[i]);
                }
                break;
            case "Tanh":
                for (int i = 0; i < input.length; i++) {
                    result[i] = tanh(input[i]);
                }
                break;
            case "Relu":
                for (int i = 0; i < input.length; i++) {
                    result[i] = relu(input[i]);
                }
                break;
            default:
                throw new IllegalArgumentException("Invalid activation function: " + activation);
        }
        return result;
    }

    private double sigmoid(double x) {
        return 1 / (1 + Math.exp(-x));
    }

    private double tanh(double x) {
        return Math.tanh(x);
    }

    private double relu(double x) {
        return Math.max(0, x);
    }
   // applying activation functions to the hidden and output layers.

    private double[] feedforward(double[] input) {
        // Input layer to hidden layer
        double[] hiddenInput = multiplyMatrix(weightsInputHidden, input);
        double[] hiddenOutput = applyActivationFunction(hiddenInput, hiddenActivation);

        // Hidden layer to output layer
        double[] finalInput = multiplyMatrix(weightsHiddenOutput, hiddenOutput);
        return applyActivationFunction(finalInput, "sigmoid");
    }

    private double[] multiplyMatrix(double[][] matrix, double[] vector) {
        int rows = matrix.length;
        int cols = matrix[0].length;
        double[] result = new double[rows];
        for (int i = 0; i < rows; i++) {
            double sum = 0;
            for (int j = 0; j < cols; j++) {
                sum += matrix[i][j] * vector[j];
            }
            result[i] = sum;
        }
        return result;
    }

    public double[][] predict(double[][] inputs) {
        int numExamples = inputs.length;
        double[][] predictions = new double[numExamples][];

        for (int i = 0; i < numExamples; i++) {
            predictions[i] = feedforward(inputs[i]);
        }

        return predictions;
    }
//These derivative functions are crucial for the backpropagation algorithm during the training of neural networks.
 //They help in adjusting the weights of the network during the learning process by indicating how much the error 
  //with respect to the network's output should affect the weights.
    private double sigmoidDerivative(double x) {
        double sigmoidX = sigmoid(x);
        return sigmoidX * (1 - sigmoidX);
    }

    private double tanhDerivative(double x) {
        double tanhX = tanh(x);
        return 1 - tanhX * tanhX;
    }

    private double reluDerivative(double x) {
        return x > 0 ? 1 : 0;
    }
   // Applies the derivative of the specified activation function to each element of the input array.
    private double[] applyActivationFunctionDerivative(double[] input, String activation) {
        double[] result = new double[input.length];
        switch (activation) {
            case "sigmoid":
                for (int i = 0; i < input.length; i++) {
                    result[i] = sigmoidDerivative(input[i]);
                }
                break;
            case "Tanh":
                for (int i = 0; i < input.length; i++) {
                    result[i] = tanhDerivative(input[i]);
                }
                break;
            case "Relu":
                for (int i = 0; i < input.length; i++) {
                    result[i] = reluDerivative(input[i]);
                }
                break;
            default:
                throw new IllegalArgumentException("Invalid activation function: " + activation);
        }
        return result;
    }

    private void trainSingleExample(double[] input, double[] target, double learningRate) {
        // Forward pass
        double[] hiddenInput = multiplyMatrix(weightsInputHidden, input);
        double[] hiddenOutput = applyActivationFunction(hiddenInput, hiddenActivation);

        double[] finalInput = multiplyMatrix(weightsHiddenOutput, hiddenOutput);
        double[] finalOutput = applyActivationFunction(finalInput, "sigmoid");

        // Backward pass
        // Compute output layer error
        double[] outputError = new double[finalOutput.length];
        for (int i = 0; i < finalOutput.length; i++) {
            outputError[i] = target[i] - finalOutput[i];//yd-ya
        }

        // Compute hidden layer error
        double[] hiddenError = multiplyMatrix(transposeMatrix(weightsHiddenOutput), outputError);

        // Update weightsHiddenOutput
        updateWeights(weightsHiddenOutput, outputError, hiddenOutput, learningRate);

        // Update weightsInputHidden
        double[] hiddenDerivative = applyActivationFunctionDerivative(hiddenInput, hiddenActivation);
        updateWeights(weightsInputHidden, hiddenError, input, learningRate, hiddenDerivative);
    }

    private double[][] transposeMatrix(double[][] matrix) {
        int rows = matrix.length;
        int cols = matrix[0].length;
        double[][] result = new double[cols][rows];
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                result[j][i] = matrix[i][j];
            }
        }
        return result;
    }
//Update weights based on errors and derivatives during the training process.

    private void updateWeights(double[][] weights, double[] error, double[] input, double learningRate) {
        for (int i = 0; i < weights.length; i++) {
            for (int j = 0; j < weights[0].length; j++) {
                weights[i][j] += learningRate * error[i] * input[j];
            }
        }
    }

    private void updateWeights(double[][] weights, double[] error, double[] input, double learningRate, double[] derivative) {
        for (int i = 0; i < weights.length; i++) {
            for (int j = 0; j < weights[0].length; j++) {
                weights[i][j] += learningRate * error[i] * input[j] * derivative[i];
            }
        }
    }
//Trains the neural network for a specified number of iterations using the provided inputs, target outputs, and learning rate.

    private double calculateTotalError(double[][] inputs, double[][] targets) {
        double totalError = 0.0;

        for (int i = 0; i < inputs.length; i++) {
            double[] predictedOutput = feedforward(inputs[i]);
            totalError += calculateError(predictedOutput, targets[i]);
        }

        return totalError;
    }

    private double calculateError(double[] predictedOutput, double[] target) {
        double error = 0.0;

        for (int i = 0; i < predictedOutput.length; i++) {
            error += 0.5 * Math.pow(target[i] - predictedOutput[i], 2);
        }

        return error;
    }

    public void train(double[][] inputs, double[][] targets, int numIterations, double learningRate, double targetErrorRatio) {
        for (int iteration = 0; iteration < numIterations; iteration++) {
            for (int i = 0; i < inputs.length; i++) {
                trainSingleExample(inputs[i], targets[i], learningRate);
            }

            // Calculate the total error after each iteration
            double totalError = calculateTotalError(inputs, targets);

            // Check if the error ratio has reached the target
            double errorRatio = totalError / inputs.length; // You can adjust this calculation based on your needs
            if (errorRatio <= targetErrorRatio) {
                System.out.println("Training stopped. Target error ratio reached.");
                return;
            }
        }

        System.out.println("Training completed.");
    }
    
}
