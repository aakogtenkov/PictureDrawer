package Estimators;

import Generators.GrayPoint;

import java.util.ArrayList;

public abstract class PixelLossEstimator implements LossEstimator{
    protected double[][] image;
    public double[][] loss;
    private int delta_width;
    private int delta_height;
    public boolean[][] possible_to_improve;
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
    public void set_possible_to_improve(int[] bounds, boolean possible_to_improve) {
        for (int i = bounds[1]; i <= bounds[3]; i += delta_height) {
            for (int j = bounds[0]; j <= bounds[2]; j += delta_width) {
                this.possible_to_improve[i / delta_height][j / delta_width] = possible_to_improve;
            }
        }
    }

    @Override
    public boolean is_possible_to_improve(int[] bounds) {
        for (int i = bounds[1]; i <= bounds[3]; i += delta_height) {
            for (int j = bounds[0]; j <= bounds[2]; j += delta_width) {
                if (this.possible_to_improve[i / delta_height][j / delta_width]) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public void update_loss(double[][] canvas, int[] bounds) {
        for (int y = bounds[1]; y <= bounds[3]; ++y) {
            for (int x = bounds[0]; x <= bounds[2]; ++x) {
                this.area_loss[y / delta_height][x / delta_width] -= this.loss[y][x];
                this.loss[y][x] = calc_loss(this.image[y][x], canvas[y][x]);
                this.area_loss[y / delta_height][x / delta_width] += this.loss[y][x];
            }
        }
    }

    @Override
    public void update_loss(double[][] canvas, int[] points_x, int[] points_y) {
        for (int i = 0; i < points_x.length; ++i) {
            int x = points_x[i];
            int y = points_y[i];
            this.area_loss[y / delta_height][x / delta_width] -= this.loss[y][x];
            this.loss[y][x] = calc_loss(this.image[y][x], canvas[y][x]);
            this.area_loss[y / delta_height][x / delta_width] += this.loss[y][x];
        }
    }

    @Override
    public void update_loss(double[][] canvas, Iterable<GrayPoint> change) {
        for (GrayPoint p : change) {
            this.area_loss[p.y / delta_height][p.x / delta_width] -= this.loss[p.y][p.x];
            this.loss[p.y][p.x] = calc_loss(this.image[p.y][p.x], canvas[p.y][p.x]);
            this.area_loss[p.y / delta_height][p.x / delta_width] += this.loss[p.y][p.x];
        }
    }

    @Override
    public int[] get_area_with_max_loss(double min_loss) {
        System.out.printf("%f %b\n", this.area_loss[1][1], possible_to_improve[1][1]);
        double best_loss = 0;
        int[] result = new int[] {-1, -1, -1, -1};
        for (int i = 0; i < this.area_loss.length; ++i) {
            for (int j = 0; j < this.area_loss[0].length; ++j) {
                if (possible_to_improve[i][j] && this.area_loss[i][j] > best_loss) {
                    best_loss = this.area_loss[i][j];
                }
            }
        }
        if (best_loss < min_loss) {
            result = new int[] {-1, -1, -1, -1};
        }
        return result;
    }



    @Override
    public double get_area_loss(int[] bounds) {
        double result = 0;
        int size = 0;
        for (int i = bounds[1]; i <= bounds[3]; i += delta_height) {
            for (int j = bounds[0]; j <= bounds[2]; j += delta_width) {
                result += this.area_loss[i / delta_height][j / delta_width];
                size++;
            }
        }
        if (size == 0) {
            return 0;
        }
        return result / (size * delta_width * delta_height);
    }

    @Override
    public void calc_area_losses() {
        for (int i = 0; i < this.area_loss.length; ++i) {
            for (int j = 0; j < this.area_loss[0].length; ++j) {
                this.area_loss[i][j] = 0;
            }
        }
        for (int i = 0; i < this.loss.length; ++i) {
            for (int j = 0; j < this.loss[0].length; ++j) {
                this.area_loss[i / delta_height][j / delta_width] += loss[i][j];
            }
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
        this.possible_to_improve = new boolean[image.length / delta_height][image[0].length / delta_width];
        for (int i = 0; i < image.length; ++i) {
            for (int j = 0; j < image[0].length; ++j) {
                this.image[i][j] = image[i][j];
            }
        }
        for (int i = 0; i < possible_to_improve.length; ++i) {
            for (int j = 0; j < possible_to_improve[0].length; ++j) {
                this.possible_to_improve[i][j] = true;
                this.area_loss[i][j] = 0;
            }
        }
        update_loss(canvas, new int[] {0, 0, image[0].length - 1, image.length - 1});
    }
}
