import ColorScheme.AtanColorAdder;
import ColorScheme.BaseColorAdder;
import ColorScheme.ColorAdder;
import DrawTools.DrawTool;
import DrawTools.Pencil;
import DrawTools.ToolParams;
import DrawTools.WhitePencil;
import Estimators.AbsLossEstimator;
import Estimators.SameColorEstimator;
import Estimators.StrictAbsLossEstimator;
import Helpers.Logger;
import ImageProcessing.ImageConverter;
import ImageProcessing.ImageLoader;
import ImageProcessing.ImagePreprocessor;
import Postprocessing.Postprocessor;

import java.awt.image.BufferedImage;
import java.util.ArrayList;

public class Main {

    public void run_pipeline(final boolean invoke_window, String[] args) {
        BufferedImage _image = ImageLoader.loadPicture("armine2.jpg");
        double[][] image = ImageConverter.pictureToGray(_image, true);
        image = ImagePreprocessor.normalizePictureSize(image, 10, 10);

        ImageLoader.savePicture(ImageConverter.grayToPicture(image, true), "gray_image.png");

        GraphicWindow graphicWindow = null;

        ArrayList<double[][]> features = ImagePreprocessor.extractFeatures(image);
        double[][] canvas = features.remove(0);

        //AbsLossEstimator lossEstimator = new AbsLossEstimator(image, features, canvas, 10, 10);
        //StrictAbsLossEstimator lossEstimator = new StrictAbsLossEstimator(image, features, canvas, 10, 10, 2.5);
        SameColorEstimator lossEstimator = new SameColorEstimator(image, features, canvas, 10, 10, 2.5, 3);

        //ColorAdder colorAdder = new BaseColorAdder();
        ColorAdder colorAdder = new AtanColorAdder();

        Logger logger = new Logger("armine_log.txt");

        ToolParams toolParams = new ToolParams();
        toolParams.color = 1.0 / 16.0;
        toolParams.max_stroke_len = 30;
        toolParams.min_stroke_len = 1;
        toolParams.delta_stroke_len = 1;
        DrawTool pencil = new Pencil(image, canvas, features, 50, 0, 10, 40, toolParams, lossEstimator, colorAdder);

        toolParams = new ToolParams();
        toolParams.color = -0.03f;
        toolParams.max_stroke_len = 16;
        toolParams.min_stroke_len = 2;
        toolParams.delta_stroke_len = 2;
        DrawTool whitePencil = new WhitePencil(image, canvas, features, 20, 0, 10, 40, toolParams, lossEstimator, colorAdder);

        if (invoke_window) {
            graphicWindow = new GraphicWindow(ImageConverter.grayToPicture(canvas, true));
        }

        //ImageProcessing.ImageLoader.savePicture(ImageProcessing.ImageConverter.grayToPicture(lossEstimator.loss, true),
        //        "example_picture_loss_iter_0.png");
        for (int iter = 0; iter < 100000; ++iter) {
            boolean updated = false;
            updated |= pencil.draw(logger);
            //updated |= whitePencil.draw(logger);

            System.out.println(iter);
            /*lossEstimator.update_loss(canvas, new int[] {0, 0, width - 1, height - 1});
            System.out.println(lossEstimator.get_loss(new int[] {0, 0, width - 1, height - 1}));

            ImageProcessing.ImageLoader.savePicture(ImageProcessing.ImageConverter.grayToPicture(lossEstimator.loss, true),
                    "example_picture_loss_iter_" + String.valueOf(iter + 1) + ".png");
            */
            if (invoke_window) {
                graphicWindow.repaintImage(ImageConverter.grayToPicture(canvas, true));
            }
            if (!updated) {
                break;
            }
        }

        _image = ImageConverter.grayToPicture(canvas, true);
        ImageLoader.savePicture(_image, "armine_result.png");

        logger.close();

        System.out.println("Done");
    }

    public void postprocess(String[] args) {
        BufferedImage _image = ImageLoader.loadPicture("example_picture6.jpg");
        double[][] image = ImageConverter.pictureToGray(_image, true);
        image = ImagePreprocessor.normalizePictureSize(image, 10, 10);

        Postprocessor postprocessor = new Postprocessor(4, image[0].length, image.length);
        postprocessor.process("log.txt");
    }

    public static void main(String[] args) {
        Main main = new Main();
        //main.run_pipeline(true, args);
        main.postprocess(args);
    }
}
