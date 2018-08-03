package ImageProcessing;

import java.util.ArrayList;

public class ImagePreprocessor {

    public static ArrayList<float[][]> extractFeatures(float[][] image) {
        int height = image.length;
        int width = image[0].length;
        ArrayList<float[][]> result = new ArrayList<>();
        float[][] empty_image = new float[height][width];
        for (int i = 0; i < height; ++i) {
            for (int j = 0; j < width; ++j) {
                empty_image[i][j] = 0;
            }
        }
        result.add(empty_image);

        float[][] level_angle = new float[height][width];

        GradientExtractor.calcGradientLevel(image, level_angle, 10, 0.7f);

        GradientExtractor.saveGradientLevelAsImage(level_angle, "gradients.png");

        result.add(level_angle);
        return result;
    }
}
