package Generators;

import ColorScheme.ColorAdder;
import DrawTools.ToolParams;
import Estimators.LossEstimator;
import Helpers.Logger;

import java.util.ArrayList;

public class LineGeneratorV2 {
    private LossEstimator lossEstimator;
    private double[][] canvas;
    private double[][] levels;
    public BasicLineDrawerV3 drawer;

    private int cur_x, cur_y;
    private double cur_len, cur_angle;

    private double color;
    private int[] bounds;

    private double max_stroke_length;
    private double min_stroke_length;
    private double delta_stroke_length;
    private double max_angle_deviation = Math.PI / 8;
    private double delta_angle = max_angle_deviation / 2;

    private int x_saved, y_saved;
    private double len_saved, angle_saved;
    private double improvement_saved;

    private int max_step_without_improvements = 3;
    private double best_improvement = 0;
    private int steps_since_last_improvement = 0;

    public LineGeneratorV2(double[][] canvas, ArrayList<double[][]> features, int[] bounds, ToolParams params, LossEstimator lossEstimator, ColorAdder colorAdder) {
        this.canvas = canvas;
        this.levels = features.get(0);
        this.bounds = bounds;
        this.lossEstimator = lossEstimator;
        this.color = params.color;
        this.max_stroke_length = params.max_stroke_len;
        this.min_stroke_length = params.min_stroke_len;
        this.delta_stroke_length = params.delta_stroke_len;
        this.drawer = new BasicLineDrawerV3(colorAdder);
        this.cur_x = bounds[0] - 1;
        this.cur_y = bounds[1];
        this.cur_len = this.max_stroke_length + 1;
        this.cur_angle = 10;
    }

    public boolean next() {
        this.cur_len += this.delta_stroke_length;
        if (this.steps_since_last_improvement > this.max_step_without_improvements) {
            this.cur_len = this.max_stroke_length + 1;
        }
        if (this.cur_len > this.max_stroke_length) {
            this.steps_since_last_improvement = 0;
            this.best_improvement = -100;

            this.cur_angle += this.delta_angle;
            this.cur_len = this.min_stroke_length;
            if (this.cur_angle > 8 || this.cur_angle > this.levels[cur_y][cur_x] + this.max_angle_deviation) {
                this.cur_x++;
                if (this.cur_x > bounds[2]) {
                    this.cur_y++;
                    this.cur_x = bounds[0];
                    if (this.cur_y > bounds[3]) {
                        return false;
                    }
                }
                this.cur_angle = this.levels[cur_y][cur_x] - this.max_angle_deviation;
            }
        }
        drawer.fabricateSegmentFromMiddle(this.canvas, (double)cur_x + 0.5, (double)cur_y + 0.5, this.cur_angle, this.cur_len, this.color);
        return true;
    }

    public void callback(double improvement) {
        //System.out.println(improvement);
        if (improvement > this.best_improvement) {
            this.best_improvement = improvement;
            this.steps_since_last_improvement = 0;
        }
        else {
            this.steps_since_last_improvement++;
        }
    }

    public double getImprovement() {
        return lossEstimator.get_improvement(drawer.getChange());
    }

    public void saveParams() {
        x_saved = cur_x;
        y_saved = cur_y;
        len_saved = cur_len;
        angle_saved = cur_angle;
        /*int c = 0;
        for (GrayPoint p : drawer.getChange()) {
            c++;
            System.out.printf("%d %d %f ", p.x, p.y, p.color);
            for (GrayPoint p1 : drawer.getChange()) {
                if (p1.x == p.x && p1.y == p.y && p1.color != p.color) {
                    System.out.println("AA");
                }
            }
        }
        System.out.println(c);*/
        improvement_saved = 0;//lossEstimator.get_improvement(drawer.getChange());
    }

    public void applySavedParams(LossEstimator[] estimators) {
        //System.out.printf("%f %f %f %f %d %d\n", line[0], line[1], line[2], line[3], x_saved, y_saved);
        drawer.drawSegment(canvas, (double)x_saved + 0.5, (double)y_saved + 0.5, angle_saved, len_saved, this.color, this.lossEstimator);
        //drawer.drawSegment(this.canvas, line[0], line[1], line[2], line[3], this.color, this.lossEstimator);
        for (LossEstimator est: estimators) {
            drawer.updateLossEstimator(this.canvas, est);
        }
    }

    public void writeLogInfo(Logger logger) {
        logger.updateLog("Line", new double[] {x_saved, y_saved, len_saved, angle_saved, this.color, improvement_saved});
    }
}
