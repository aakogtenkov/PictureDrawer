package ImageProcessing;

import java.awt.*;
import java.awt.image.BufferedImage;

public class ImageConverter {

    public static double[][] pictureToGray(BufferedImage image, boolean invert) {
        int width = image.getWidth();
        int height = image.getHeight();
        double[][] result = new double[height][width];
        for (int y = 0; y < height; ++y) {
            for (int x = 0; x < width; ++x) {
                Color color = new Color(image.getRGB(x, y));
                double R = color.getRed();
                double G = color.getGreen();
                double B = color.getBlue();

                double Y = (R + B + G) / 3.0 / 256.0;
                double Y_inv = 255.0 / 256.0 - Y;

                result[y][x] = (invert ? Y_inv : Y);
            }
        }
        return result;
    }

    public static BufferedImage grayToPicture(double[][] image, boolean invert) {
        int height = image.length;
        int width = image[0].length;
        BufferedImage result = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                int col;
                if (!invert) {
                    col = (int)Math.round(image[i][j] * 256.0);
                }
                else {
                    col = (int)Math.round((1.0 * 255 / 256 -  image[i][j]) * 256.0);
                }
                if (col > 255) {
                    col = 255;
                }
                else if (col < 0) {
                    col = 0;
                }
                result.setRGB(j, i, (new Color(col, col, col)).getRGB());
            }
        }
        return result;
    }
}