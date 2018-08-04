package Estimators;

import java.util.ArrayList;

public class AbsLossEstimator extends PixelLossEstimator {
    @Override
    public double calc_loss(double original, double canvas) {
        return Math.abs(original - canvas);
    }

    public AbsLossEstimator(double[][] image, ArrayList<double[][]> features) {
        super(image, features);
    }
}
