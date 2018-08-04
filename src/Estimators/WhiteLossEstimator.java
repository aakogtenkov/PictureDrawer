package Estimators;

import java.util.ArrayList;

public class WhiteLossEstimator extends PixelLossEstimator {
    @Override
    public double calc_loss(double original, double canvas) {
        return Math.max(0, canvas - original);
    }

    public WhiteLossEstimator(double[][] image, ArrayList<double[][]> features) {
        super(image, features);
    }
}
