package Estimators;

import Generators.GrayPoint;

public interface LossEstimator {
    double get_loss(int[] bounds);
    double calc_loss(double original, double canvas);
    void update_loss(double[][] canvas, int[] bounds);
    void update_loss(double[][] canvas, int[] points_x, int[] points_y);
    void update_loss(double[][] canvas, Iterable<GrayPoint> change);
    double get_improvement(Iterable<GrayPoint> change);
}
