package Estimators;

import Generators.GrayPoint;

import java.util.ArrayList;

public abstract class PixelLossEstimator implements LossEstimator{
    protected double[][] image;
    public double[][] loss;
    private int delta_width;
    private int delta_height;
    public double[][] area_loss;

    @Override
    public double get_loss(int[] bounds) {
        double result = 0;
        for (int y = bounds[1]; y <= bounds[3]; ++y) {
            for (int x = bounds[0]; x <= bounds[2]; ++x) {
                result += this.loss[y][x];
            }
        }
        result /= (double)(bounds[2] - bounds[0] + 1) * (bounds[3] - bounds[1] + 1);
        return result;
    }

    @Override
    public void update_loss(double[][] canvas, int[] bounds) {
        for (int y = bounds[1]; y <= bounds[3]; ++y) {
            for (int x = bounds[0]; x <= bounds[2]; ++x) {
                this.loss[y][x] = calc_loss(this.image[y][x], canvas[y][x]);
            }
        }
    }

    @Override
    public void update_loss(double[][] canvas, int[] points_x, int[] points_y) {
        for (int i = 0; i < points_x.length; ++i) {
            int x = points_x[i];
            int y = points_y[i];
            this.loss[y][x] = calc_loss(this.image[y][x], canvas[y][x]);
        }
    }

    @Override
    public void update_loss(double[][] canvas, Iterable<GrayPoint> change) {
        for (GrayPoint p : change) {
            this.loss[p.y][p.x] = calc_loss(this.image[p.y][p.x], canvas[p.y][p.x]);
        }
    }

    @Override
    public double get_improvement(Iterable<GrayPoint> new_values) {
        double improvement = 0;
        for (GrayPoint p : new_values) {
            double loss_before = this.loss[p.y][p.x];
            double loss_after = calc_loss(this.image[p.y][p.x], p.color);
            improvement += (loss_before - loss_after);
        }
        return improvement;
    }

    @Override
    public double get_improvement(ArrayList<GrayPoint> new_values, int length) {
        double improvement = 0;
        for (int i = 0; i < length; ++i) {
            GrayPoint p = new_values.get(i);
            double loss_before = this.loss[p.y][p.x];
            double loss_after = calc_loss(this.image[p.y][p.x], p.color);
            improvement += (loss_before - loss_after);
        }
        return improvement;
    }

    @Override
    public int get_index_for_best_improvement(ArrayList<GrayPoint> new_values, int max_steps_without_improvement, int delta_ind) {
        double best_improvement = 0;
        double improvement = 0;
        int ind = -1;
        int cur_ind = 0;
        int steps_without_improvement = 0;
        for (GrayPoint p : new_values) {
            double loss_before = this.loss[p.y][p.x];
            double loss_after = calc_loss(this.image[p.y][p.x], p.color);
            improvement += (loss_before - loss_after);
            if (best_improvement < improvement) {
                steps_without_improvement = 0;
                best_improvement = improvement;
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

    protected PixelLossEstimator() {
        this.image = null;
        this.loss = null;
    }

    public PixelLossEstimator(double[][] image, ArrayList<double[][]> features, double[][] canvas, int delta_width, int delta_height) {
        this.delta_height = delta_height;
        this.delta_width = delta_width;
        this.image = image;
        this.loss = new double[image.length][image[0].length];
        this.area_loss = new double[image.length / delta_height][image[0].length / delta_width];
        for (int i = 0; i < image.length; ++i) {
            for (int j = 0; j < image[0].length; ++j) {
                this.image[i][j] = image[i][j];
            }
        }
        update_loss(canvas, new int[] {0, 0, image[0].length - 1, image.length - 1});
    }
}
