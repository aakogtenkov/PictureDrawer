package ImageProcessing;

public class GradientExtractor {

    private static double UNDEFINED_CONSTANT = -10;
    private static double ATOL = 1e-6f;

    private static void calcTrueGradientLevel(double[][] image, double[][] level_angle) {
        double[][] gradient_x = ImageTransformer.scharr(image, 0);
        double[][] gradient_y = ImageTransformer.scharr(image, 1);
        for (int y = 0; y < image.length; ++y) {
            for (int x = 0; x < image[0].length; ++x) {
                if (Math.abs(gradient_x[y][x]) > ATOL || Math.abs(gradient_y[y][x]) > ATOL) {
                    double vx = -gradient_y[y][x];
                    double vy = gradient_x[y][x];
                    double angle = (double)Math.atan2(vy, vx);
                    while (angle < -Math.PI / 2) {
                        angle += Math.PI;
                    }
                    while (angle > Math.PI / 2) {
                        angle -= Math.PI;
                    }
                    level_angle[y][x] = angle;
                }
                else {
                    level_angle[y][x] = UNDEFINED_CONSTANT;
                }
            }
        }
    }

    private static int calcGradientLevelUsingNeighbours(double[][] level_angle, int min_neighbours) {
        int calculated = 0;
        int[] try_points_x = new int[] {1, 1, 0, -1, -1, -1, 0, 1};
        int[] try_points_y = new int[] {0, -1, -1, -1, 0, 1, 1, 1};
        for (int y = 0; y < level_angle.length; y++) {
            for (int x = 0; x < level_angle[0].length; x++) {
                if (level_angle[y][x] == UNDEFINED_CONSTANT) {
                    int cur_neighbours = 0;
                    double average_angle = 0;
                    for (int k = 0; k < try_points_x.length; k++) {
                        int px = x + try_points_x[k];
                        int py = y + try_points_y[k];
                        if (px >= 0 && px < level_angle[0].length && py >= 0 && py < level_angle.length &&
                                !(level_angle[py][px] == UNDEFINED_CONSTANT)) {
                            cur_neighbours++;
                            average_angle += level_angle[py][px];
                        }
                    }
                    if (cur_neighbours >= min_neighbours) {
                        level_angle[y][x] = average_angle / cur_neighbours;
                        calculated++;
                    }

                }
            }
        }
        return calculated;
    }

    public static void saveGradientLevelAsImage(double[][] level_angle, String name) {
        double[][] image = new double[level_angle.length][level_angle[0].length];
        for (int i = 0; i < level_angle.length; ++i) {
            for (int j = 0; j < level_angle[0].length; ++j) {
                image[i][j] = (double)Math.sin(-(double)level_angle[i][j]) / 2 + 0.5f;
            }
        }
        ImageLoader.savePicture(ImageConverter.grayToPicture(image, false), name);
    }

    public static void calcGradientLevel(double[][] image, double[][] level_angle, int kernel_size, double sigma) {
        double[][] blurred_image = ImageTransformer.gaussBlur(image, kernel_size, sigma);

        ImageLoader.savePicture(ImageConverter.grayToPicture(blurred_image, true), "blurred_image.png");

        calcTrueGradientLevel(blurred_image, level_angle);

        int min_neighbours = 8;
        while (min_neighbours > 0) {
            int calculated = calcGradientLevelUsingNeighbours(level_angle, min_neighbours);
            if (calculated == 0) {
                min_neighbours--;
            }
        }
    }
}
