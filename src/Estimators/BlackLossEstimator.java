package Estimators;

import java.util.ArrayList;

public class BlackLossEstimator extends PixelLossEstimator {
    @Override
    public float calc_loss(float original, float canvas) {
        return Math.max(0, original - canvas);
    }

    public BlackLossEstimator(float[][] image, ArrayList<float[][]> features) {
        super(image, features);
    }
}
