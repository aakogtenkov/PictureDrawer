package ImageProcessing;

import java.awt.*;
import java.awt.image.BufferedImage;

public class ImageConverter {

    public static float[][] pictureToGray(BufferedImage image, boolean invert) {
        int width = image.getWidth();
        int height = image.getHeight();
        float[][] result = new float[height][width];
        for (int y = 0; y < height; ++y) {
            for (int x = 0; x < width; ++x) {
                Color color = new Color(image.getRGB(x, y));
                float R = color.getRed();
                float G = color.getGreen();
                float B = color.getBlue();

                float Y = (R + B + G) / 3.0f / 256.0f;
                float Y_inv = 1.0f * 255f / 256f - Y;

                result[y][x] = (invert ? Y_inv : Y);
            }
        }
        return result;
    }

    public static BufferedImage grayToPicture(float[][] image, boolean invert) {
        int height = image.length;
        int width = image[0].length;
        BufferedImage result = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                int col;
                if (!invert) {
                    col =  Math.round(image[i][j] * 256.0f);
                }
                else {
                    col = Math.round((1.0f * 255f / 256f -  image[i][j]) * 256.0f);
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