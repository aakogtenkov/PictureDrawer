package Estimators;

import java.util.ArrayList;

public class WhiteLossEstimator extends PixelLossEstimator {
    @Override
    public double calc_loss(double original, double canvas) {
        return Math.max(0, canvas - original);
    }

    public WhiteLossEstimator(double[][] image, ArrayList<double[][]> features, double[][] canvas, int delta_width, int delta_height) {
        super(image, features, canvas, delta_width, delta_height);
    }
}
