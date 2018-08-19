package Generators;

import ColorScheme.ColorAdder;
import Estimators.LossEstimator;

import java.util.*;

public class BasicLineDrawerV3 {

    private ArrayList<GrayPoint> points;
    private ColorAdder colorAdder;
    private static double ATOL = 1e-3;

    public BasicLineDrawerV3(ColorAdder colorAdder) {
        this.points = new ArrayList<>();
        this.colorAdder = colorAdder;
    }

    public void applyToCanvas(double[][] canvas) {
        for (GrayPoint p : this.points) {
            canvas[p.y][p.x] = p.color;
        }
    }

    public void applyToCanvas(double[][] canvas, int number) {
        for (int i = 0; i < number; ++i) {
            GrayPoint p = this.points.get(i);
            canvas[p.y][p.x] = p.color;
        }
    }

    public ArrayList<GrayPoint> getChange() {
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

    private void fabricateVertical(double[][] canvas, int x, int y, double length, double color) {
        int up_y = y;
        int down_y = y;
        double col = this.colorAdder.addColor(canvas[y][x], color, 3);
        this.points.add(new GrayPoint(x, y, col));
        while (length > ATOL) {
            down_y--;
            if (down_y >= 0) {
                col = this.colorAdder.addColor(canvas[down_y][x], color, 3);
                this.points.add(new GrayPoint(x, down_y, col));
            }
            length--;
            up_y++;
            if (up_y < canvas.length && length > ATOL) {
                col = this.colorAdder.addColor(canvas[up_y][x], color, 3);
                this.points.add(new GrayPoint(x, up_y, col));
            }
            length--;
        }
        /*System.out.printf("%d %d %f\n", x, y, length);
        for (GrayPoint p : this.points) {
            System.out.printf("%d %d ; ", p.x, p.y);
        }
        System.out.println();*/
    }

    private void fabricateHorizontal(double[][] canvas, int x, int y, double length, double color) {
        int left_x = x;
        int right_x = x;
        double col = this.colorAdder.addColor(canvas[y][x], color, 3);
        this.points.add(new GrayPoint(x, y, col));
        while (length > ATOL) {
            left_x--;
            if (left_x >= 0) {
                col = this.colorAdder.addColor(canvas[y][left_x], color, 3);
                this.points.add(new GrayPoint(left_x, y, col));
            }
            length--;
            right_x++;
            if (right_x < canvas[0].length && length > ATOL) {
                col = this.colorAdder.addColor(canvas[y][right_x], color, 3);
                this.points.add(new GrayPoint(right_x, y, col));
            }
            length--;
        }
    }

    private void processAlongX(double[][] canvas, int cur_x, int cur_y, double[] line, double color) {
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
        double col0 = part1 * color;
        double col1 = part0 * color;
        this.points.add(new GrayPoint(cur_x, cur_y, colorAdder.addColor(canvas[cur_y][cur_x], col0, 3)));
        if (add_y >= 0 && add_y < canvas.length) {
            this.points.add(new GrayPoint(cur_x, add_y, colorAdder.addColor(canvas[add_y][cur_x], col1, 3)));
        }
    }

    private void processAlongY(double[][] canvas, int cur_x, int cur_y, double[] line, double color) {
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
        double col0 = part1 * color;
        double col1 = part0 * color;
        this.points.add(new GrayPoint(cur_x, cur_y, colorAdder.addColor(canvas[cur_y][cur_x], col0, 3)));
        if (add_x >= 0 && add_x < canvas[0].length) {
            this.points.add(new GrayPoint(add_x, cur_y, colorAdder.addColor(canvas[cur_y][add_x], col1, 3)));
        }
    }

    private void fabricateAlongX(double[][] canvas, double x, double y, double angle, double length, double color) {
        double[] line = buildLine(x, y, angle);
        double dy = Math.tan(angle);
        double dlen = Math.sqrt(Math.pow(dy, 2) + 1);
        double left_x = x;
        double left_y = y;
        double right_x = x + 1;
        double right_y = y + dy;
        //System.out.printf("%f %f %f\n", x, y, angle);
        while (length > ATOL) {
            int cur_x = (int)Math.floor(left_x);
            int cur_y = (int)Math.floor(left_y);
            if (cur_x >= 0 && cur_x < canvas[0].length && cur_y >= 0 && cur_y < canvas.length) {
                processAlongX(canvas, cur_x, cur_y, line, color * dlen);
            }
            length -= dlen;
            left_x--;
            left_y -= dy;

            cur_x = (int)Math.floor(right_x);
            cur_y = (int)Math.floor(right_y);
            if (length > ATOL && cur_x >= 0 && cur_x < canvas[0].length && cur_y >= 0 && cur_y < canvas.length) {
                processAlongX(canvas, cur_x, cur_y, line, color * dlen);
            }
            length -= dlen;
            right_x++;
            right_y += dy;
        }
    }

    private void fabricateAlongY(double[][] canvas, double x, double y, double angle, double length, double color) {
        double sin = Math.sin(angle);
        double cos = Math.cos(angle);
        double[] line = buildLine(x, y, angle);
        double dx = Math.abs(cos / sin);
        double dy = 1;
        if (sin < 0) {
            dy = -1;
        }
        double dlen = Math.sqrt(Math.pow(dx, 2) + 1);
        double left_x = x;
        double left_y = y;
        double right_x = x + dx;
        double right_y = y + dy;
        //System.out.printf("%f %f %f\n", x, y, angle);
        while (length > ATOL) {
            int cur_x = (int)Math.floor(left_x);
            int cur_y = (int)Math.floor(left_y);
            if (cur_x >= 0 && cur_x < canvas[0].length && cur_y >= 0 && cur_y < canvas.length) {
                processAlongY(canvas, cur_x, cur_y, line, color * dlen);
            }
            length -= dlen;
            left_x -= dx;
            left_y -= dy;

            cur_x = (int)Math.floor(right_x);
            cur_y = (int)Math.floor(right_y);
            if (length > ATOL && cur_x >= 0 && cur_x < canvas[0].length && cur_y >= 0 && cur_y < canvas.length) {
                processAlongY(canvas, cur_x, cur_y, line, color * dlen);
            }
            length -= dlen;
            right_x += dx;
            right_y += dy;
        }
    }

    public void fabricateSegmentFromMiddle(double[][] canvas, double x, double y, double angle, double length, double color) {
        this.points.clear();
        while (angle < -Math.PI / 2) {
            angle += Math.PI;
        }
        while (angle > Math.PI / 2) {
            angle -= Math.PI;
        }
        if (Math.abs(angle) < 0.05) {
            angle += 0.05;
        }
        else if (Math.abs(angle) > Math.PI / 2 - 0.05) {
            angle -= 0.05;
        }
        /*if ((int)x == 43 && (int)y == 15) {
            System.out.println(angle);
        }*/
        //System.out.printf("%f %f %f %f %f %f %f\n", x1, y1, x2, y2, length, angle, Math.sin((double)angle));
        /*if (Math.abs(angle) < 0.05) {
            fabricateHorizontal(canvas, (int)x, (int)y, length, color);
        }
        else if (Math.abs(angle) > Math.PI / 2 - 0.05) {
            fabricateVertical(canvas, (int)x, (int)y, length, color);
        }
        else */if (Math.abs(angle) < Math.PI / 4) {
            fabricateAlongX(canvas, x, y, angle, length, color);
        }
        else {
            fabricateAlongY(canvas, x, y, angle, length, color);
        }
    }

    public void drawSegment(double[][] canvas, double x, double y, double angle, double length, double color, LossEstimator lossEstimator) {
        fabricateSegmentFromMiddle(canvas, x, y, angle, length, color);
        applyToCanvas(canvas);
        updateLossEstimator(canvas, lossEstimator);
    }
}
