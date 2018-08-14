package Estimators;

import Generators.GrayPoint;

import java.util.ArrayList;

public interface LossEstimator {
    double get_loss(int[] bounds);
    double calc_loss(double original, double canvas);
    void update_loss(double[][] canvas, int[] bounds);
    void update_loss(double[][] canvas, int[] points_x, int[] points_y);
    void update_loss(double[][] canvas, Iterable<GrayPoint> change);
    double get_improvement(Iterable<GrayPoint> change);
    int get_index_for_best_improvement(ArrayList<GrayPoint> change, int max_steps_without_improvement, int delta_ind);
    double get_improvement(ArrayList<GrayPoint> change, int length);
}
