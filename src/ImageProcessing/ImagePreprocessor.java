package ImageProcessing;

import java.util.ArrayList;

public class ImagePreprocessor {

    public static ArrayList<double[][]> extractFeatures(double[][] image) {
        int height = image.length;
        int width = image[0].length;
        ArrayList<double[][]> result = new ArrayList<>();
        double[][] empty_image = new double[height][width];
        for (int i = 0; i < height; ++i) {
            for (int j = 0; j < width; ++j) {
                empty_image[i][j] = 0;
            }
        }
        result.add(empty_image);

        double[][] level_angle = new double[height][width];

        GradientExtractor.calcGradientLevel(image, level_angle, 10, 0.7f);

        GradientExtractor.saveGradientLevelAsImage(level_angle, "gradients.png");

        result.add(level_angle);
        return result;
    }

    public static double[][] normalizePictureSize(double[][] image, int delta_width, int delta_height) {
        int add_w = (delta_width - image[0].length % delta_width) % delta_width;
        int add_h = (delta_height - image.length % delta_height) % delta_height;
        if (add_h == 0 && add_w == 0) {
            return image;
        }
        double[][] result = new double[image.length + add_h][image[0].length + add_w];
        for (int y = 0; y < result.length; ++y) {
            for (int x = 0; x < result[0].length; ++x) {
                result[y][x] = 0;
            }
        }
        for (int y = 0; y < image.length; ++y) {
            for (int x = 0; x < image[0].length; ++x) {
                result[y + add_h / 2][x + add_w / 2] = image[y][x];
            }
        }
        return result;
    }
}
