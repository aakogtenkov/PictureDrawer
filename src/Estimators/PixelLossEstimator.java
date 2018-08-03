package Estimators;

import Generators.GrayPoint;

import java.util.ArrayList;

public abstract class PixelLossEstimator implements LossEstimator{
    protected float[][] image;
    public float[][] loss;

    @Override
    public float get_loss(int[] bounds) {
        float result = 0;
        for (int y = bounds[1]; y <= bounds[3]; ++y) {
            for (int x = bounds[0]; x <= bounds[2]; ++x) {
                result += this.loss[y][x];
            }
        }
        result /= (float)(bounds[2] - bounds[0] + 1) * (bounds[3] - bounds[1] + 1);
        return result;
    }

    @Override
    public void update_loss(float[][] canvas, int[] bounds) {
        for (int y = bounds[1]; y <= bounds[3]; ++y) {
            for (int x = bounds[0]; x <= bounds[2]; ++x) {
                this.loss[y][x] = calc_loss(this.image[y][x], canvas[y][x]);
            }
        }
    }

    @Override
    public void update_loss(float[][] canvas, int[] points_x, int[] points_y) {
        for (int i = 0; i < points_x.length; ++i) {
            int x = points_x[i];
            int y = points_y[i];
            this.loss[y][x] = calc_loss(this.image[y][x], canvas[y][x]);
        }
    }

    @Override
    public void update_loss(float[][] canvas, Iterable<GrayPoint> change) {
        for (GrayPoint p : change) {
            this.loss[p.y][p.x] = calc_loss(this.image[p.y][p.x], canvas[p.y][p.x]);
        }
    }

    @Override
    public float get_improvement(Iterable<GrayPoint> new_values) {
        float improvement = 0;
        for (GrayPoint p : new_values) {
            float loss_before = this.loss[p.y][p.x];
            float loss_after = calc_loss(this.image[p.y][p.x], p.color);
            improvement += (loss_before - loss_after);
        }
        return improvement;
    }

    protected PixelLossEstimator() {
        this.image = null;
        this.loss = null;
    }

    public PixelLossEstimator(float[][] image, ArrayList<float[][]> features) {
        this.image = image;
        this.loss = new float[image.length][image[0].length];
        for (int i = 0; i < image.length; ++i) {
            for (int j = 0; j < image[0].length; ++j) {
                this.loss[i][j] = calc_loss(image[i][j], 0);
                this.image[i][j] = image[i][j];
            }
        }
    }
}
