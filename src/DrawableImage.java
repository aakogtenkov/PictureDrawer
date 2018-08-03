import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

public class DrawableImage extends JPanel {
    private BufferedImage image;

    public DrawableImage(BufferedImage image)
    {
        this.image = image;
        setSize(image.getWidth(),image.getHeight());
        setVisible(true);
    }

    public void updateImage(BufferedImage image) {
        this.image = image;
    }

    public void paintComponent(Graphics g)
    {
        super.paintComponent(g);
        if(image == null) return;
        //int imageWidth = image.getWidth(this);
        //int imageHeight = image.getHeight(this);

        Graphics2D g2d = (Graphics2D) g.create();
        g.drawImage(image, 0, 0, null);
        g.dispose();
    }

}
