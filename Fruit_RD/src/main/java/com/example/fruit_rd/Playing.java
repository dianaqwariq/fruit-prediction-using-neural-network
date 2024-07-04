package com.example.fruit_rd;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;

import java.net.URL;
import java.util.Arrays;
import java.util.ResourceBundle;

public class Playing implements Initializable {

    @FXML
    private ComboBox<String> actCheckBox;

    @FXML
    private TextField learningrate;

//    @FXML
//    private TextField firstinput;
    @FXML
    private ComboBox<String> fruitComboBox;

    @FXML
    private Label goall;

    @FXML
    private TextField epoch;

    @FXML
    private ComboBox<String> goalCheckBox;

    @FXML
    private ImageView image2;

    @FXML
    private ComboBox<String> neuronCheckBox;

    @FXML
    private Button showTheResult;

    @FXML
    private ComboBox<String> sweetnessCheckBox;

    @FXML
    private TextField output;

    @FXML
    private TextField accuracy;
    @FXML
    private TextField goal;

    private NeuralNetwork neuralNetwork;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Initialize ComboBox items
    	fruitComboBox.getItems().addAll("Yellow", "Orange", "Red");
        neuronCheckBox.getItems().addAll("1", "2", "3", "4", "5", "6", "7", "8", "9", "10");
        sweetnessCheckBox.getItems().addAll("1", "2", "3", "4", "5", "6", "7", "8", "9", "10");
       // goalCheckBox.getItems().addAll("Apple", "Orange", "Banana");
        actCheckBox.getItems().addAll("sigmoid", "Tanh", "Relu");
    }

    @FXML
    private void showResultAction() {
        try {
            // Validate input values
            if (learningrate.getText().isEmpty() || epoch.getText().isEmpty() || goal.getText().isEmpty()) {
                showAlert("Please enter learning rate, epochs, and goal.");
                return;
            }

            double learningRateValue = Double.parseDouble(learningrate.getText());
            int trainingIterations = Integer.parseInt(epoch.getText());
            double targetErrorRatio = Double.parseDouble(goal.getText());

            // Validate learning rate, epochs, and target error ratio
            if (learningRateValue <= 0 || trainingIterations <= 0 || targetErrorRatio <= 0) {
                showAlert("Learning rate, epochs, and goal must be positive values.");
                return;
            }

            // Instantiate the neural network
            int numInputNeurons = 2;
            int numHiddenNeurons = Integer.parseInt(neuronCheckBox.getValue()); // Take from the combo box
            int numOutputNeurons = 3;
            String hiddenActivation = actCheckBox.getValue(); // Use the selected activation function

            neuralNetwork = new NeuralNetwork(numInputNeurons, numHiddenNeurons, numOutputNeurons, hiddenActivation);

            // Training data and parameters (you can customize based on your needs)
            double[][] trainingInputs = {
                    {2, 4}, {3, 7}, {1, 7}, {2, 2}, {1, 5}, {3, 2},
                    {1, 9}, {1, 2}, {3, 8}, {2, 6}, {3, 4}, {1, 10},
                    {3, 6}, {1, 6}, {1, 3}, {2, 5}, {3, 9}, {2, 1},
                    {2, 3}, {2, 7}, {2, 8}, {3, 5}, {3, 10},
                    {1, 1}, {2, 10}, {1, 4}
            };

            double[][] trainingOutputs = {
                    {0, 1, 0}, {0, 0, 1}, {1, 0, 0}, {0, 1, 0}, {1, 0, 0},
                    {0, 0, 1}, {1, 0, 0}, {1, 0, 0}, {0, 0, 1}, {0, 1, 0},
                    {0, 0, 1}, {1, 0, 0}, {0, 0, 1}, {1, 0, 0}, {1, 0, 0},
                    {0, 1, 0}, {0, 0, 1}, {0, 1, 0}, {0, 1, 0}, {0, 1, 0},
                    {0, 1, 0}, {0, 0, 1}, {0, 0, 1},
                    {1, 0, 0}, {0, 1, 0}, {1, 0, 0}
            };

            // Train the neural network
            neuralNetwork.train(trainingInputs, trainingOutputs, trainingIterations, learningRateValue, targetErrorRatio);

            // Get the selected fruit value from the ComboBox
            int selectedFruitValue;
            switch (fruitComboBox.getValue()) {
                case "Red":
                    selectedFruitValue = 3;
                    break;
                case "Yellow":
                    selectedFruitValue = 1;
                    break;
                case "Orange":
                    selectedFruitValue = 2;
                    break;
                default:
                    selectedFruitValue = 1; // Default
            }

            // Testing data (you can customize based on your needs)
            double[] testData = {
                    selectedFruitValue,
                    Double.parseDouble(sweetnessCheckBox.getValue())
            };

            // Make predictions
            double[][] predictedOutput = neuralNetwork.predict(new double[][]{testData});

            // Determine the fruit label based on the output
            String fruitLabel = getFruitLabel(predictedOutput);
            output.setText(fruitLabel);

            // Calculate accuracy (you can customize based on your needs)
            double[][] testingInputs = {
                    {2, 4}, {3, 7}, {1, 7}, {2, 2}, {1, 5}, {3, 2},
                    {1, 9}, {1, 2}, {3, 8}, {2, 6}, {3, 4}, {1, 10},
                    {3, 6}, {1, 6}, {1, 3}, {2, 5}, {3, 9}, {2, 1},
                    {2, 3}, {2, 7}, {2, 8}, {3, 5}, {3, 10},
                    {1, 1}, {2, 10}, {1, 4}
            };

            double[][] testingOutputs = {
                    {0, 1, 0}, {0, 0, 1}, {1, 0, 0}, {0, 1, 0}, {1, 0, 0},
                    {0, 0, 1}, {1, 0, 0}, {1, 0, 0}, {0, 0, 1}, {0, 1, 0},
                    {0, 0, 1}, {1, 0, 0}, {0, 0, 1}, {1, 0, 0}, {1, 0, 0},
                    {0, 1, 0}, {0, 0, 1}, {0, 1, 0}, {0, 1, 0}, {0, 1, 0},
                    {0, 1, 0}, {0, 0, 1}, {0, 0, 1},
                    {1, 0, 0}, {0, 1, 0}, {1, 0, 0}
            };

            double testingAccuracy = neuralNetwork.calculateAccuracy(testingInputs, testingOutputs);
            accuracy.setText(String.valueOf(testingAccuracy));

        } catch (NumberFormatException ex) {
            showAlert("Invalid input. Please enter valid numeric values.");
            ex.printStackTrace();
        }
    }


    private String getFruitLabel(double[][] output) {
        double[] outputArray = output[0];

        // Check conditions and return the corresponding label
        if (outputArray[0] > outputArray[1] && outputArray[0] > outputArray[2]) {
            return "Banana";
        } else if (outputArray[1] > outputArray[0] && outputArray[1] > outputArray[2]) {
            return "Orange";
        } else {
            return "Apple";
        }
    }

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }



    private void displayResult(double[] outputValues) {
        // Assuming outputValues is in the format [banana, orange, apple]
        if (outputValues[0] > outputValues[1] && outputValues[0] > outputValues[2]) {
            output.setText("Banana");
        } else if (outputValues[1] > outputValues[0] && outputValues[1] > outputValues[2]) {
            output.setText("Orange");
        } else {
            output.setText("Apple");
        }
    }


    private void displayResult(double[][] predictedOutput) {
        double firstOutput = predictedOutput[0][0];
        double secondOutput = predictedOutput[0][1];
        double thirdOutput = predictedOutput[0][2];

        if (firstOutput > secondOutput && firstOutput > thirdOutput) {
            output.setText("Banana");
        } else if (secondOutput > firstOutput && secondOutput > thirdOutput) {
            output.setText("Orange");
        } else if (thirdOutput > firstOutput && thirdOutput > secondOutput) {
            output.setText("Apple");
        } else {
            // Handle other cases or set a default label
            output.setText("Unknown");
        }
    }
}
