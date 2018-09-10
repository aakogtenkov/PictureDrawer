package Estimators;

import Generators.GrayPoint;

import java.util.ArrayList;

public class SameColorEstimator extends PixelLossEstimator {

    private double multiplier;
    private double colordiff_multiplier;

    @Override
    public double calc_loss(double original, double canvas) {
        if (original >= canvas) {
            return original - canvas;
        }
        return (canvas - original) * this.multiplier;
    }

    @Override
    public double get_improvement(Iterable<GrayPoint> new_values) {
        double improvement = 0;
        double min_loss = 100;
        double max_loss = -100;
        for (GrayPoint p : new_values) {
            double loss_before = this.loss[p.y][p.x];
            if (loss_before * p.color < min_loss) {
                min_loss = loss_before * p.color;
            }
            if (loss_before * p.color > max_loss) {
                max_loss = loss_before * p.color;
            }
            double loss_after = calc_loss(this.image[p.y][p.x], p.color);
            improvement += (loss_before - loss_after);
        }
        if (min_loss > max_loss) {
            min_loss = max_loss;
        }
        return improvement - this.colordiff_multiplier * (max_loss - min_loss);
    }

    @Override
    public double get_improvement(ArrayList<GrayPoint> new_values, int length) {
        double improvement = 0;
        double min_loss = 100;
        double max_loss = -100;
        for (int i = 0; i < length; ++i) {
            GrayPoint p = new_values.get(i);
            double loss_before = this.loss[p.y][p.x];
            if (loss_before * p.color < min_loss) {
                min_loss = loss_before * p.color;
            }
            if (loss_before * p.color > max_loss) {
                max_loss = loss_before * p.color;
            }
            double loss_after = calc_loss(this.image[p.y][p.x], p.color);
            improvement += (loss_before - loss_after);
        }
        if (min_loss > max_loss) {
            min_loss = max_loss;
        }
        return improvement - this.colordiff_multiplier * (max_loss - min_loss);
    }

    @Override
    public int get_index_for_best_improvement(ArrayList<GrayPoint> new_values, int max_steps_without_improvement, int delta_ind) {
        double best_improvement = 0;
        double improvement = 0;
        int ind = -1;
        int cur_ind = 0;
        int steps_without_improvement = 0;
        double min_loss = 100;
        double max_loss = -100;
        for (GrayPoint p : new_values) {
            double loss_before = this.loss[p.y][p.x];
            if (loss_before * p.color < min_loss) {
                min_loss = loss_before * p.color;
            }
            if (loss_before * p.color > max_loss) {
                max_loss = loss_before * p.color;
            }
            double loss_after = calc_loss(this.image[p.y][p.x], p.color);
            improvement += (loss_before - loss_after);
            if (best_improvement < improvement - this.colordiff_multiplier * (max_loss - min_loss)) {
                steps_without_improvement = 0;
                best_improvement = improvement - this.colordiff_multiplier * (max_loss - min_loss);
                ind = cur_ind;
            }
            else {
                steps_without_improvement++;
                if (steps_without_improvement > max_steps_without_improvement) {
                    return ind;
                }
            }
            cur_ind++;
        }
        return ind;
    }

    public SameColorEstimator(double[][] image, ArrayList<double[][]> features, double[][] canvas, int delta_width, int delta_height, double multiplier, double colordiff_multiplier) {
        super(image, features, canvas, delta_width, delta_height);
        this.multiplier = multiplier;
        this.colordiff_multiplier = colordiff_multiplier;
    }
}
