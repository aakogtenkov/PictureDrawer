package DrawTools;

import ColorScheme.ColorAdder;
import Estimators.LossEstimator;
import Generators.GrayPoint;
import Generators.LineGeneratorV2;
import Helpers.Logger;

import java.util.ArrayList;

public abstract class PixelPencil implements DrawTool {
    protected LossEstimator lossEstimator;
    protected LossEstimator mainLossEstimator;
    private float[][] canvas;
    private ArrayList<float[][]> features;

    private int maxIter;
    private int minLoss;
    private int area_step;
    private int area_size;
    private ToolParams toolParams;
    private ColorAdder colorAdder;

    public PixelPencil() {

    }

    public PixelPencil(float[][] canvas, ArrayList<float[][]> features,
                       int maxIter, int minLoss, int area_step, int area_size,
                       ToolParams toolParams, LossEstimator mainLossEstimator, ColorAdder colorAdder) {
        this.maxIter = maxIter;
        this.minLoss = minLoss;
        this.canvas = canvas;
        this.area_step = area_step;
        this.area_size = area_size;
        this.toolParams = toolParams;
        this.mainLossEstimator = mainLossEstimator;
        this.features = features;
        this.colorAdder = colorAdder;
    }

    public ArrayList<int[]> find_max_loss_area(float min_loss) {
        ArrayList<int[]> result = new ArrayList<>();
        ArrayList<Float> losses = new ArrayList<>();
        int width = this.canvas[0].length;
        int height = this.canvas.length;

        int radius = this.area_size / 2;

        for (int y = 0; y < height; y += this.area_step) {
            for (int x = 0; x < width; x += this.area_step) {
                int lx = Math.max(0, x - radius);
                int rx = Math.min(width - 1, x + radius);
                int ly = Math.max(0, y - radius);
                int ry = Math.min(height - 1, y + radius);
                float loss = this.lossEstimator.get_loss(new int[]{lx, ly, rx, ry});
                if (loss > min_loss) {
                    result.add(new int[] {lx, ly, rx, ry});
                    losses.add(loss);
                    for (int i = losses.size() - 1; i > 0; --i) {
                        if (losses.get(i) > losses.get(i - 1)) {
                            float tmp_loss = losses.get(i - 1);
                            losses.set(i - 1, losses.get(i));
                            losses.set(i, tmp_loss);
                            int[] tmp_bounds = result.get(i - 1);
                            result.set(i - 1, result.get(i));
                            result.set(i, tmp_bounds);
                        }
                    }
                }
            }
        }
        return result;
    }

    /*private boolean possibleToImprove(int[] bounds) {
        for (int x = bounds[0]; x <= bounds[2]; ++x) {
            for (int y = bounds[1]; y <= bounds[3]; ++y) {
                ArrayList<GrayPoint> change = new ArrayList<>();
                float color = this.toolParams.color;
                if (canvas[y][x] + color > 1.0f) {
                    color = 1 - canvas[y][x];
                }
                else if (canvas[y][x] + color < 0) {
                    color = -canvas[y][x];
                }
                change.add(new GrayPoint(x, y, color));
                float improvement = this.lossEstimator.get_improvement(canvas, change);
                if (improvement > 0) {
                    return true;
                }
            }
        }
        return false;
    }*/

    private boolean tryDrawInArea(int[] bounds, Logger logger) {
        LineGeneratorV2 lineGenerator = new LineGeneratorV2(this.canvas, this.features, bounds, this.toolParams, this.mainLossEstimator, this.colorAdder);
        float min_loss = 0;
        while (lineGenerator.next()) {
            float improvement = lineGenerator.getImprovement();
            if (improvement > min_loss) {
                min_loss = improvement;
                lineGenerator.saveParams();
            }
            lineGenerator.callback(improvement);
        }
        if (min_loss > 0) {
            lineGenerator.applySavedParams(new LossEstimator[] {this.lossEstimator});
            if (logger != null) {
                lineGenerator.writeLogInfo(logger);
            }
        }
        return (min_loss > 0);
    }

    public boolean draw(Logger logger) {
        int width = this.canvas[0].length;
        int height = this.canvas.length;
        this.lossEstimator.update_loss(this.canvas, new int[] {0, 0, width - 1, height - 1});
        this.mainLossEstimator.update_loss(this.canvas, new int[] {0, 0, width - 1, height - 1});

        for (int i = 0; i < this.maxIter; i++) {
            ArrayList<int[]> all_bounds = find_max_loss_area(this.minLoss);
            if (all_bounds.size() == 0) {
                System.out.println("No opportunities to draw: area");
                return false;
            }
            boolean result = false;
            for (int j = 0; j < all_bounds.size(); ++j) {
                result |= tryDrawInArea(all_bounds.get(j), logger);
                if (result) {
                    break;
                }
            }
            if (!result) {
                return false;
            }
        }
        //System.out.println("Iteration done");
        return true;
    }
}
