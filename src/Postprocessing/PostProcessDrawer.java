package Postprocessing;

import ColorScheme.AtanColorAdder;
import ColorScheme.ColorAdder;
import Generators.BasicLineDrawerV3;

public class PostProcessDrawer {

    private static double ATOL = 0.001;

    private static double[] buildLineEnds(double x, double y, double angle, double length) {
        double sin = Math.sin(angle);
        double cos = Math.cos(angle);
        double[] result = new double[4];
        result[0] = cos * length + x;
        result[1] = sin * length + y;
        result[2] = x - cos * length;
        result[3] = y - sin * length;
        return result;
    }

    private static double[] buildLine(double x1, double y1, double x2, double y2) {
        double a = y1 - y2;
        double b = x2 - x1;
        double c = x1 * y2 - x2 * y1;
        return new double[] {a, b, c};
    }

    private static double getDistance(double x, double y, double[] line) {
        return Math.abs(line[0] * x + line[1] * y + line[2]) / Math.sqrt(line[0] * line[0] + line[1] * line[1]);
    }

    private static void drawInArea(double[][] canvas, double[] line, int[] bounds, double stroke_size, double color) {
        ColorAdder colorAdder = new AtanColorAdder();
        for (int y = bounds[1]; y <= bounds[3]; ++y) {
            for (int x = bounds[0]; x < bounds[2]; ++x) {
                double col = color;
                double dist1 = getDistance(x, y, line);
                double dist2 = getDistance(x + 1, y, line);
                double dist3 = getDistance(x, y + 1, line);
                double dist4 = getDistance(x + 1, y + 1, line);
                double min_dist = Math.min(Math.min(dist3, dist4), Math.min(dist1, dist2));
                double max_dist = Math.max(Math.max(dist3, dist4), Math.max(dist1, dist2));
                if (min_dist > stroke_size / 2 - ATOL) {
                    continue;
                }
                if (max_dist > stroke_size / 2 + ATOL && min_dist < stroke_size / 2) {
                    double part1 = max_dist - stroke_size / 2;
                    double part2 = -min_dist + stroke_size / 2;
                    col = color * part1 / (part1 + part2);
                }
                if (canvas[y][x] > colorAdder.addColor(canvas[y][x], col)) {
                    System.out.printf("%f %f\n", canvas[y][x], col);
                }
                canvas[y][x] = colorAdder.addColor(canvas[y][x], col);
            }
        }
    }

    public static void draw(double[][] canvas, double x, double y, double angle, double length, double stroke_size, double color) {
        //BE CAREFUL, SEQUENCE OF STEPS IS IMPORTANT!!!
        double[] bounds = buildLineEnds(x, y, angle, length);    //build ends of line
        double[] line = buildLine(bounds[0], bounds[1], bounds[2], bounds[3]);     //build ax + by + c = 0 line

        //fixing bounds
        if (bounds[0] > bounds[2]) {
            double tmp = bounds[0];
            bounds[0] = bounds[2];
            bounds[2] = tmp;
        }
        if (bounds[1] > bounds[3]) {
            double tmp = bounds[1];
            bounds[1] = bounds[3];
            bounds[3] = tmp;
        }
        if (bounds[0] < 0) bounds[0] = 0;
        else if (bounds[0] >= canvas[0].length) bounds[0] = canvas[0].length - 1;
        if (bounds[1] < 0) bounds[1] = 0;
        else if (bounds[1] >= canvas.length) bounds[1] = canvas.length - 1;
        if (bounds[2] < 0) bounds[2] = 0;
        else if (bounds[2] >= canvas[0].length) bounds[2] = canvas[0].length - 1;
        if (bounds[3] < 0) bounds[3] = 0;
        else if (bounds[3] >= canvas.length) bounds[3] = canvas.length - 1;

        BasicLineDrawerV3 lineDrawer = new BasicLineDrawerV3(new AtanColorAdder());
        lineDrawer.setRandom(0.2);
        lineDrawer.fabricateSegmentFromMiddle(canvas, x, y, angle, length, color);
        lineDrawer.applyToCanvas(canvas);


        //drawInArea(canvas, line, new int[] {(int)bounds[0], (int)bounds[1], (int)bounds[2], (int)bounds[3]}, stroke_size, color);
    }
}
