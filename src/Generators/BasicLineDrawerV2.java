package Generators;

import ColorScheme.ColorAdder;
import Estimators.LossEstimator;

import java.util.*;

public class BasicLineDrawerV2 {

    private HashSet<GrayPoint> points;
    private ColorAdder colorAdder;
    private static float ATOL = 1e-3f;

    public BasicLineDrawerV2(ColorAdder colorAdder) {
        this.points = new HashSet<>();
        this.colorAdder = colorAdder;
    }

    public void applyToCanvas(float[][] canvas) {
        for (GrayPoint p : this.points) {
            canvas[p.y][p.x] = p.color;
        }
    }

    public Iterable<GrayPoint> getChange() {
        return this.points;
    }

    public void updateLossEstimator(float[][] canvas, LossEstimator lossEstimator) {
        lossEstimator.update_loss(canvas, this.points);
    }

    private float[] buildLine(float x1, float y1, float x2, float y2) {
        float a = y1 - y2;
        float b = x2 - x1;
        float c = x1 * y2 - x2 * y1;
        return new float[] {a, b, c};
    }

    private float[] buildLine(float x, float y, float angle) {
        return buildLine(x, y, x + (float)Math.cos((double)angle), y + (float)Math.sin((double)angle));
    }

    private boolean compareDistance(float x1, float y1, float x2, float y2, float[] line) {
        float d1 = Math.abs(line[0] * x1 + line[1] * y1 + line[2]);
        float d2 = Math.abs(line[0] * x2 + line[1] * y2 + line[2]);
        return (d1 < d2);
    }

    private float getDistance(float x, float y, float[] line) {
        return Math.abs(line[0] * x + line[1] * y + line[2]) / (float)Math.sqrt((double)(line[0] * line[0] + line[1] * line[1]));
    }

    private void fabricateVertical(float[][] canvas, int x, int start_y, int end_y, float color) {
        this.points.clear();
        if (end_y < start_y) {
            int tmp = start_y;
            start_y = end_y;
            end_y = tmp;
        }
        for (int y = start_y; y <= end_y; ++y) {
            float col = this.colorAdder.addColor(canvas[y][x], color);
            this.points.add(new GrayPoint(x, y, col));
        }
    }

    private void fabricateHorizontal(float[][] canvas, int start_x, int y, int end_x, float color) {
        this.points.clear();
        if (end_x < start_x) {
            int tmp = start_x;
            start_x = end_x;
            end_x = tmp;
        }
        for (int x = start_x; x <= end_x; ++x) {
            float col = this.colorAdder.addColor(canvas[y][x], color);
            this.points.add(new GrayPoint(x, y, col));
        }
    }

    private void fabricateAlongX(float[][] canvas, float x, float y, float length, float angle, float color) {
        float sin = (float)Math.sin((double)angle);
        float cos = (float)Math.cos((double)angle);
        float[] line = buildLine(x, y, angle);
        float dy = 1.0f / cos * sin;
        float dlen = (float)Math.sqrt(Math.pow(dy, 2) + 1);
        while (length > -ATOL) {
            int cur_x = (int)Math.floor((double)x);
            int cur_y = (int)Math.floor((double)y);
            if (cur_x < 0 || cur_x >= canvas[0].length || cur_y < 0 || cur_y >= canvas.length) {
                break;
            }
            float center_x = cur_x + 0.5f;
            float center_y = cur_y + 0.5f;
            float up_y = center_y + 1;
            float down_y = center_y - 1;
            int add_y = cur_y + 1;
            boolean flag = compareDistance(center_x, up_y, center_x, down_y, line);
            if (!flag) {
                up_y = down_y;      //BEST Kostyl
                add_y = cur_y - 1;
            }
            float dist0 = getDistance(center_x, center_y, line);
            float dist1 = getDistance(center_x, up_y, line);
            float part0 = dist0 / (dist0 + dist1);
            float part1 = dist1 / (dist0 + dist1);
            float col0 = part0 * color;
            float col1 = part1 * color;
            this.points.add(new GrayPoint(cur_x, cur_y, colorAdder.addColor(canvas[cur_y][cur_x], col0)));
            if (add_y >= 0 && add_y < canvas.length) {
                this.points.add(new GrayPoint(cur_x, add_y, colorAdder.addColor(canvas[add_y][cur_x], col1)));
            }

            x += 1;
            y += dy;
            length -= dlen;
        }
    }

    private void fabricateAlongY(float[][] canvas, float x, float y, float length, float angle, float color) {
        float sin = (float)Math.sin((double)angle);
        float cos = (float)Math.cos((double)angle);
        float[] line = buildLine(x, y, angle);
        float dx = 1.0f / sin * cos;
        float dy = 1;
        if (sin < 0) {
            dy = -1;
        }
        float dlen = (float)Math.sqrt(Math.pow(dx, 2) + 1);
        while (length > -ATOL) {
            int cur_x = (int)Math.floor((double)x);
            int cur_y = (int)Math.floor((double)y);
            if (cur_x < 0 || cur_x >= canvas[0].length || cur_y < 0 || cur_y >= canvas.length) {
                break;
            }
            float center_x = cur_x + 0.5f;
            float center_y = cur_y + 0.5f;
            float left_x = center_x - 1;
            float right_x = center_x + 1;
            int add_x = cur_x - 1;
            boolean flag = compareDistance(left_x, center_y, right_x, center_y, line);
            if (!flag) {
                left_x = right_x;      //BEST Kostyl
                add_x = cur_x + 1;
            }
            float dist0 = getDistance(center_x, center_y, line);
            float dist1 = getDistance(left_x, center_y, line);
            float part0 = dist0 / (dist0 + dist1);
            float part1 = dist1 / (dist0 + dist1);
            float col0 = part0 * color;
            float col1 = part1 * color;
            this.points.add(new GrayPoint(cur_x, cur_y, colorAdder.addColor(canvas[cur_y][cur_x], col0)));
            if (add_x >= 0 && add_x < canvas[0].length) {
                this.points.add(new GrayPoint(add_x, cur_y, colorAdder.addColor(canvas[cur_y][add_x], col1)));
            }

            x += dx;
            y += dy;
            length -= dlen;
        }
    }

    public void fabricateSegment(float[][] canvas, float x1, float y1, float x2, float y2, float color) {
        this.points.clear();
        float angle = (float)Math.atan2((double)(y2 - y1), (double)(x2 - x1));
        while (angle < -Math.PI / 2) {
            angle += Math.PI;
        }
        while (angle > Math.PI / 2) {
            angle -= Math.PI;
        }
        float length = (float)Math.sqrt(Math.pow((double)(x2 - x1), 2) + Math.pow((double)(y2 - y1), 2));
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

    public void drawSegment(float[][] canvas, float x1, float y1, float x2, float y2, float color, LossEstimator lossEstimator) {
        fabricateSegment(canvas, x1, y1, x2, y2, color);
        applyToCanvas(canvas);
        updateLossEstimator(canvas, lossEstimator);
    }
}
