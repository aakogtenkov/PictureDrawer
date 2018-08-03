package ImageProcessing;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;

public class ImageLoader {

    public static BufferedImage loadPicture(String filename) {
        BufferedImage image;
        try {
            image = ImageIO.read(new File(filename));
        } catch (Exception e) {
            throw new RuntimeException("Cannot open image " + filename);
        }
        return image;
    }

    public static void savePicture(BufferedImage image, String filename) {
        try {
            ImageIO.write(image, "png", new File(filename));
        } catch (Exception e) {
            throw new RuntimeException("Cannot save image to " + filename);
        }
    }
}
