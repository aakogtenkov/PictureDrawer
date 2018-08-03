package Estimators;

import Generators.GrayPoint;

public interface LossEstimator {
    float get_loss(int[] bounds);
    float calc_loss(float original, float canvas);
    void update_loss(float[][] canvas, int[] bounds);
    void update_loss(float[][] canvas, int[] points_x, int[] points_y);
    void update_loss(float[][] canvas, Iterable<GrayPoint> change);
    float get_improvement(Iterable<GrayPoint> change);
}
