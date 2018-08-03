package Generators;

import ColorScheme.ColorAdder;
import DrawTools.ToolParams;
import Estimators.LossEstimator;
import Helpers.Logger;

import java.util.ArrayList;

public class LineGeneratorV2 {
    private LossEstimator lossEstimator;
    private float[][] canvas;
    private float[][] levels;
    public BasicLineDrawerV2 drawer;

    private int cur_x, cur_y;
    private float cur_len, cur_angle;

    private float color;
    private int[] bounds;

    private float max_stroke_length;
    private float min_stroke_length;
    private float delta_stroke_length;
    private float max_angle_deviation = (float)Math.PI / 8;
    private float delta_angle = max_angle_deviation / 2;

    private int x_saved, y_saved;
    private float len_saved, angle_saved;
    private float improvement_saved;

    private int max_step_without_improvements = 3;
    private float best_improvement = 0;
    private int steps_since_last_improvement = 0;

    public LineGeneratorV2(float[][] canvas, ArrayList<float[][]> features, int[] bounds, ToolParams params, LossEstimator lossEstimator, ColorAdder colorAdder) {
        this.canvas = canvas;
        this.levels = features.get(0);
        this.bounds = bounds;
        this.lossEstimator = lossEstimator;
        this.color = params.color;
        this.max_stroke_length = params.max_stroke_len;
        this.min_stroke_length = params.min_stroke_len;
        this.delta_stroke_length = params.delta_stroke_len;
        this.drawer = new BasicLineDrawerV2(colorAdder);
        this.cur_x = bounds[0] - 1;
        this.cur_y = bounds[1];
        this.cur_len = this.max_stroke_length + 1;
        this.cur_angle = 10;
    }

    private float[] calcLine(int x, int y, float length, float angle) {
        float x1, y1, x2, y2;
        float dl = length / 2;
        float sin = (float)Math.sin(angle);
        float cos = (float)Math.cos(angle);
        x1 = (float)x + 0.5f + dl * cos;
        x2 = (float)x + 0.5f - dl * cos;
        y1 = (float)y + 0.5f + dl * sin;
        y2 = (float)y + 0.5f - dl * sin;

        // to make possible lines with (num_pixels % 2 == 0) pixels
        if ((int)Math.floor((double)length) % 2 == 1) {
            x2 += cos;
            y2 += sin;
        }

        x1 = Math.min(Math.max(0.5f, x1), this.canvas[0].length - 0.5f);
        x2 = Math.min(Math.max(0.5f, x2), this.canvas[0].length - 0.5f);
        y1 = Math.min(Math.max(0.5f, y1), this.canvas.length - 0.5f);
        y2 = Math.min(Math.max(0.5f, y2), this.canvas.length - 0.5f);
        return new float[] {x1, y1, x2, y2};
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
        float[] line = calcLine(cur_x, cur_y, cur_len, cur_angle);
        drawer.fabricateSegment(this.canvas, line[0], line[1], line[2], line[3], this.color);
        return true;
    }

    public void callback(float improvement) {
        if (improvement > this.best_improvement) {
            this.best_improvement = improvement;
            this.steps_since_last_improvement = 0;
        }
        else {
            this.steps_since_last_improvement++;
        }
    }

    public float getImprovement() {
        return lossEstimator.get_improvement(drawer.getChange());
    }

    public void saveParams() {
        x_saved = cur_x;
        y_saved = cur_y;
        len_saved = cur_len;
        angle_saved = cur_angle;
        improvement_saved = lossEstimator.get_improvement(drawer.getChange());
    }

    public void applySavedParams(LossEstimator[] estimators) {
        float[] line = calcLine(x_saved, y_saved, len_saved, angle_saved);

        drawer.drawSegment(this.canvas, line[0], line[1], line[2], line[3], this.color, this.lossEstimator);
        for (LossEstimator est: estimators) {
            drawer.updateLossEstimator(this.canvas, est);
        }
    }

    public void writeLogInfo(Logger logger) {
        logger.updateLog("Line", new float[] {x_saved, y_saved, len_saved, angle_saved, this.color, improvement_saved});
    }
}
