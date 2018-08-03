package DrawTools;

import ColorScheme.ColorAdder;
import Estimators.BlackLossEstimator;
import Estimators.LossEstimator;

import java.util.ArrayList;

public class Pencil extends PixelPencil{
    public Pencil(float[][] image, float[][] canvas, ArrayList<float[][]> features,
                  int maxIter, int minLoss, int area_step, int area_size,
                  ToolParams toolParams, LossEstimator mainLossEstimator, ColorAdder colorAdder) {
        super(canvas, features, maxIter, minLoss, area_step, area_size, toolParams, mainLossEstimator, colorAdder);
        this.lossEstimator = new BlackLossEstimator(image, features);
    }
}
