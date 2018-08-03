package Estimators;

import java.util.ArrayList;

public class WhiteLossEstimator extends PixelLossEstimator {
    @Override
    public float calc_loss(float original, float canvas) {
        return Math.max(0, canvas - original);
    }

    public WhiteLossEstimator(float[][] image, ArrayList<float[][]> features) {
        super(image, features);
    }
}
