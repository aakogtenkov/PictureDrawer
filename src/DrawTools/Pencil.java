package DrawTools;

import ColorScheme.ColorAdder;
import Estimators.BlackLossEstimator;
import Estimators.LossEstimator;

import java.util.ArrayList;

public class Pencil extends PixelPencil{
    public Pencil(double[][] image, double[][] canvas, ArrayList<double[][]> features,
                  int maxIter, int minLoss, int area_step, int area_size,
                  ToolParams toolParams, LossEstimator mainLossEstimator, ColorAdder colorAdder) {
        super(canvas, features, maxIter, minLoss, area_step, area_size, toolParams, mainLossEstimator, colorAdder);
        this.lossEstimator = new BlackLossEstimator(image, features, canvas, 10, 10);
    }
}
