package Estimators;

import java.util.ArrayList;

public class AbsLossEstimator extends PixelLossEstimator {
    @Override
    public float calc_loss(float original, float canvas) {
        return Math.abs(original - canvas);
    }

    public AbsLossEstimator(float[][] image, ArrayList<float[][]> features) {
        super(image, features);
    }
}
