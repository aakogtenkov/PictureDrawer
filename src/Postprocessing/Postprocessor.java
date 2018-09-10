package Postprocessing;

import Helpers.LogReader;
import Helpers.Logger;
import ImageProcessing.ImageConverter;
import ImageProcessing.ImageLoader;

import java.util.ArrayList;
import java.util.Random;

public class Postprocessor {
    private int size_multiplier;
    private double[][] canvas;
    private double stroke_size = 4;

    public Postprocessor(int size_multiplier, int width, int height) {
        this.size_multiplier = size_multiplier;
        this.canvas = new double[height * size_multiplier][width * size_multiplier];
        for (int i = 0; i < this.canvas.length; ++i) {
            for (int j = 0; j < canvas[i].length; ++j) {
                this.canvas[i][j] = 0;
            }
        }
    }

    private void resizeInfo(ArrayList<Double> info) {
        info.set(0, (info.get(0) + 0.5) * size_multiplier);
        info.set(1, (info.get(1) + 0.5) * size_multiplier);
        info.set(2, (info.get(2) + 0.5) * size_multiplier);
    }

    private void noise(ArrayList<Double> info) {
        double angle_deviation = Math.PI / 6 / Math.pow(info.get(2), 1.0/4.0);
        double len_deviation_coeff = 0.2;
        Random random = new Random();
        info.set(0, info.get(0) - (double)size_multiplier / 2.0 + (random.nextDouble() * (size_multiplier * 1.1)));
        info.set(1, info.get(1) - (double)size_multiplier / 2.0 + (random.nextDouble() * (size_multiplier * 1.1)));
        info.set(2, info.get(2) + len_deviation_coeff * random.nextGaussian() * info.get(2));
        info.set(3, info.get(3) - angle_deviation / 2 + (random.nextDouble() * angle_deviation));
    }

    public void process(String log_filename) {
        LogReader reader = new LogReader(log_filename);
        ArrayList<Double> info = new ArrayList<>();
        String markdown = reader.readLine(info);
        int c = 0;
        while (markdown != null) {
            resizeInfo(info);
            for (int i = 0; i < 15; ++i) {
                ArrayList<Double> info_copy = new ArrayList<>(info);
                noise(info_copy);
                PostProcessDrawer.draw(canvas, info_copy.get(0), info_copy.get(1), info_copy.get(3), info_copy.get(2), stroke_size, info_copy.get(4) / 4);
            }
            c++;
            /*double x_saved = info.get(0);
            double y_saved = info.get(1);
            double len_saved = info.get(2);
            double angle_saved = info.get(3);*/

            //double impr = info.get(4);
            markdown = reader.readLine(info);
            if (c > 800000) break;
        }
        System.out.println(c);

        reader.close();

        ImageLoader.savePicture(ImageConverter.grayToPicture(canvas, true), log_filename + "_processed.png");
    }
}
