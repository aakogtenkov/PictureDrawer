package Estimators;

import java.util.ArrayList;

public class BlackLossEstimator extends PixelLossEstimator {
    @Override
    public double calc_loss(double original, double canvas) {
        return Math.max(0, original - canvas);
    }

    public BlackLossEstimator(double[][] image, ArrayList<double[][]> features) {
        super(image, features);
    }
}
