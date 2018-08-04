package Estimators;

import Generators.GrayPoint;

import java.util.ArrayList;

public abstract class PixelLossEstimator implements LossEstimator{
    protected double[][] image;
    public double[][] loss;

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

    protected PixelLossEstimator() {
        this.image = null;
        this.loss = null;
    }

    public PixelLossEstimator(double[][] image, ArrayList<double[][]> features) {
        this.image = image;
        this.loss = new double[image.length][image[0].length];
        for (int i = 0; i < image.length; ++i) {
            for (int j = 0; j < image[0].length; ++j) {
                this.loss[i][j] = calc_loss(image[i][j], 0);
                this.image[i][j] = image[i][j];
            }
        }
    }
}
