import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

public class GraphicWindow extends JFrame{
    private DrawableImage image_component;

    public GraphicWindow(BufferedImage image) {
        setTitle("PictureDrawer v. 1.0");
        setSize(new Dimension(image.getWidth() + 30, image.getHeight() + 50));
        setDefaultCloseOperation (WindowConstants.EXIT_ON_CLOSE);
        setVisible(true);
        this.image_component = new DrawableImage(image);
        add(this.image_component);
    }

    public void repaintImage(BufferedImage image) {
        this.image_component.updateImage(image);
        this.image_component.repaint();
    }
}
