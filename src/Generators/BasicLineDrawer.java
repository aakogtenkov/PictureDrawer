package Generators;

import ColorScheme.ColorAdder;
import Estimators.LossEstimator;

import java.util.*;

public class BasicLineDrawer {

    private HashSet<GrayPoint> points;
    private ColorAdder colorAdder;

    public BasicLineDrawer(ColorAdder colorAdder) {
        this.points = new HashSet<>();
        this.colorAdder = colorAdder;
    }

    public void applyToCanvas(double[][] canvas) {
        for (GrayPoint p : this.points) {
            canvas[p.y][p.x] = p.color;
        }
    }

    public Iterable<GrayPoint> getChange() {
        return this.points;
    }

    public void updateLossEstimator(double[][] canvas, LossEstimator lossEstimator) {
        lossEstimator.update_loss(canvas, this.points);
    }

    private double[] buildLine(double x1, double y1, double x2, double y2) {
        double a = y1 - y2;
        double b = x2 - x1;
        double c = x1 * y2 - x2 * y1;
        return new double[] {a, b, c};
    }

    private boolean isOnLine(int x, int y, double x1, double y1, double x2, double y2) {
        double[] line = buildLine(x1, y1, x2, y2);
        double[] vertex_x = new double[] {x, x, x + 1, x + 1};
        double[] vertex_y = new double[] {y, y + 1, y + 1, y};
        for (int i = 0; i < vertex_x.length; ++i) {
            for (int j = i + 1; j < vertex_x.length; ++j) {
                double res1 = line[0] * vertex_x[i] + line[1] * vertex_y[i] + line[2];
                double res2 = line[0] * vertex_x[j] + line[1] * vertex_y[j] + line[2];
                if (res1 < 0 && res2 > 0 || res1 > 0 && res2 < 0) {
                    return true;
                }
            }
        }
        return false;
    }

    public void fabricateSegment(double[][] canvas, double x1, double y1, double x2, double y2, double color) {
        this.points.clear();
        if (x1 > x2) {
            double tmp_x = x1;
            double tmp_y = y1;
            x1 = x2;
            y1 = y2;
            x2 = tmp_x;
            y2 = tmp_y;
        }
        int end_x = (int)Math.floor(x2);
        int end_y = (int)Math.floor(y2);
        int cur_x = (int)Math.floor(x1);
        int cur_y = (int)Math.floor(y1);
        int start_y = cur_y;
        int prev_x = -1;
        int prev_y = -1;
        int[] try_points_x, try_points_y;

        if (y2 >= y1) {
            try_points_x = new int[] {1, 0, 1};
            try_points_y = new int[] {0, 1, 1};
        }
        else {
            try_points_x = new int[] {1, 0, 1};
            try_points_y = new int[] {0, -1, -1};
        }

        double col = this.colorAdder.addColor(canvas[cur_y][cur_x], color);
        this.points.add(new GrayPoint(cur_x, cur_y, col));

        while (cur_x != end_x || cur_y != end_y) {
            int new_point_x, new_point_y;
            for (int i = 0; i < try_points_x.length; ++i) {
                new_point_x = cur_x + try_points_x[i];
                new_point_y = cur_y + try_points_y[i];
                if ((prev_x != new_point_x || prev_y != new_point_y) && new_point_x <= end_x && new_point_y >= 0 && new_point_y <= Math.max(end_y, start_y) && isOnLine(new_point_x, new_point_y, x1, y1, x2, y2)) {
                    prev_x = cur_x;
                    prev_y = cur_y;
                    cur_x = new_point_x;
                    cur_y = new_point_y;
                    break;
                }
            }

            //System.out.printf("%d %d %d %d %d %d %f %f %f %f %b\n", cur_x, cur_y, prev_x, prev_y, end_x, end_y, x1, y1, x2, y2, isOnLine(cur_x + 1, cur_y, x1, y1, x2, y2));

            col = this.colorAdder.addColor(canvas[cur_y][cur_x], color);
            this.points.add(new GrayPoint(cur_x, cur_y, col));
        }
    }

    public void drawSegment(double[][] canvas, double x1, double y1, double x2, double y2, double color, LossEstimator lossEstimator) {
        fabricateSegment(canvas, x1, y1, x2, y2, color);
        applyToCanvas(canvas);
        updateLossEstimator(canvas, lossEstimator);
    }
}
