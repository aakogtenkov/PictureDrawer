package Generators;

import ColorScheme.ColorAdder;
import Estimators.LossEstimator;

import java.util.*;

public class BasicLineDrawerV2 {

    private HashSet<GrayPoint> points;
    private ColorAdder colorAdder;
    private static double ATOL = 1e-3;

    public BasicLineDrawerV2(ColorAdder colorAdder) {
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

    private double[] buildLine(double x, double y, double angle) {
        return buildLine(x, y, x + Math.cos(angle), y + Math.sin(angle));
    }

    private boolean compareDistance(double x1, double y1, double x2, double y2, double[] line) {
        double d1 = Math.abs(line[0] * x1 + line[1] * y1 + line[2]);
        double d2 = Math.abs(line[0] * x2 + line[1] * y2 + line[2]);
        return (d1 < d2);
    }

    private double getDistance(double x, double y, double[] line) {
        return Math.abs(line[0] * x + line[1] * y + line[2]) / Math.sqrt(line[0] * line[0] + line[1] * line[1]);
    }

    private void fabricateVertical(double[][] canvas, int x, int start_y, int end_y, double color) {
        this.points.clear();
        if (end_y < start_y) {
            int tmp = start_y;
            start_y = end_y;
            end_y = tmp;
        }
        for (int y = start_y; y <= end_y; ++y) {
            double col = this.colorAdder.addColor(canvas[y][x], color);
            this.points.add(new GrayPoint(x, y, col));
        }
    }

    private void fabricateHorizontal(double[][] canvas, int start_x, int y, int end_x, double color) {
        this.points.clear();
        if (end_x < start_x) {
            int tmp = start_x;
            start_x = end_x;
            end_x = tmp;
        }
        for (int x = start_x; x <= end_x; ++x) {
            double col = this.colorAdder.addColor(canvas[y][x], color);
            this.points.add(new GrayPoint(x, y, col));
        }
    }

    private void fabricateAlongX(double[][] canvas, double x, double y, double length, double angle, double color) {
        double[] line = buildLine(x, y, angle);
        double dy = Math.tan(angle);
        double dlen = Math.sqrt(Math.pow(dy, 2) + 1);
        while (length > -ATOL) {
            int cur_x = (int)Math.floor(x);
            int cur_y = (int)Math.floor(y);
            if (cur_x < 0 || cur_x >= canvas[0].length || cur_y < 0 || cur_y >= canvas.length) {
                break;
            }
            double center_x = cur_x + 0.5;
            double center_y = cur_y + 0.5;
            double up_y = center_y + 1;
            double down_y = center_y - 1;
            int add_y = cur_y + 1;
            boolean flag = compareDistance(center_x, up_y, center_x, down_y, line);
            if (!flag) {
                up_y = down_y;      //BEST Kostyl
                add_y = cur_y - 1;
            }
            double dist0 = getDistance(center_x, center_y, line);
            double dist1 = getDistance(center_x, up_y, line);
            double part0 = dist0 / (dist0 + dist1);
            double part1 = dist1 / (dist0 + dist1);
            double col0 = part0 * color;
            double col1 = part1 * color;
            this.points.add(new GrayPoint(cur_x, cur_y, colorAdder.addColor(canvas[cur_y][cur_x], col0)));
            if (add_y >= 0 && add_y < canvas.length) {
                this.points.add(new GrayPoint(cur_x, add_y, colorAdder.addColor(canvas[add_y][cur_x], col1)));
            }

            x += 1;
            y += dy;
            length -= dlen;
        }
    }

    private void fabricateAlongY(double[][] canvas, double x, double y, double length, double angle, double color) {
        double sin = Math.sin(angle);
        double cos = Math.cos(angle);
        double[] line = buildLine(x, y, angle);
        double dx = cos / sin;
        double dy = 1;
        if (sin < 0) {
            dy = -1;
        }
        double dlen = Math.sqrt(Math.pow(dx, 2) + 1);
        while (length > -ATOL) {
            int cur_x = (int)Math.floor(x);
            int cur_y = (int)Math.floor(y);
            if (cur_x < 0 || cur_x >= canvas[0].length || cur_y < 0 || cur_y >= canvas.length) {
                break;
            }
            double center_x = cur_x + 0.5;
            double center_y = cur_y + 0.5;
            double left_x = center_x - 1;
            double right_x = center_x + 1;
            int add_x = cur_x - 1;
            boolean flag = compareDistance(left_x, center_y, right_x, center_y, line);
            if (!flag) {
                left_x = right_x;      //BEST Kostyl
                add_x = cur_x + 1;
            }
            double dist0 = getDistance(center_x, center_y, line);
            double dist1 = getDistance(left_x, center_y, line);
            double part0 = dist0 / (dist0 + dist1);
            double part1 = dist1 / (dist0 + dist1);
            double col0 = part0 * color;
            double col1 = part1 * color;
            this.points.add(new GrayPoint(cur_x, cur_y, colorAdder.addColor(canvas[cur_y][cur_x], col0)));
            if (add_x >= 0 && add_x < canvas[0].length) {
                this.points.add(new GrayPoint(add_x, cur_y, colorAdder.addColor(canvas[cur_y][add_x], col1)));
            }

            x += dx;
            y += dy;
            length -= dlen;
        }
    }

    public void fabricateSegment(double[][] canvas, double x1, double y1, double x2, double y2, double color) {
        this.points.clear();
        double angle = Math.atan2((y2 - y1), (x2 - x1));
        while (angle < -Math.PI / 2) {
            angle += Math.PI;
        }
        while (angle > Math.PI / 2) {
            angle -= Math.PI;
        }
        double length = Math.sqrt(Math.pow((x2 - x1), 2) + Math.pow((y2 - y1), 2));
        //System.out.printf("%f %f %f %f %f %f %f\n", x1, y1, x2, y2, length, angle, Math.sin((double)angle));
        if ((int)x1 == (int)x2) {
            fabricateVertical(canvas, (int)x1, (int)y1, (int)y2, color);
        }
        else if ((int)y1 == (int)y2) {
            fabricateHorizontal(canvas, (int)x1, (int)y1, (int)x2, color);
        }
        else if (Math.abs(x2 - x1) > Math.abs(y2 - y1)) {
            fabricateAlongX(canvas, (int)x1, (int)y1, length, angle, color);
        }
        else {
            fabricateAlongY(canvas, (int)x2, (int)y2, length, angle, color);
        }
    }

    public void drawSegment(double[][] canvas, double x1, double y1, double x2, double y2, double color, LossEstimator lossEstimator) {
        fabricateSegment(canvas, x1, y1, x2, y2, color);
        applyToCanvas(canvas);
        updateLossEstimator(canvas, lossEstimator);
    }
}
