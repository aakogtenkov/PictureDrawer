package Estimators;

import java.util.ArrayList;

public class StrictAbsLossEstimator extends PixelLossEstimator {
    private double multiplier = 1;

    @Override
    public double calc_loss(double original, double canvas) {
        if (original >= canvas) {
            return original - canvas;
        }
        return (canvas - original) * this.multiplier;
    }

    public StrictAbsLossEstimator(double[][] image, ArrayList<double[][]> features, double[][] canvas, int delta_width, int delta_height, double multiplier) {
        super(image, features, canvas, delta_width, delta_height);
        this.multiplier = multiplier;
    }
}
