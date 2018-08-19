package Postprocessing;

import Helpers.LogReader;
import Helpers.Logger;

import java.util.ArrayList;

public class Postprocessor {
    private int size_multiplier;
    private double[][] canvas;

    public Postprocessor(int size_multiplier, int width, int height) {
        this.size_multiplier = size_multiplier;
        this.canvas = new double[height * size_multiplier][width * size_multiplier];
        for (int i = 0; i < this.canvas.length; ++i) {
            for (int j = 0; j < canvas[i].length; ++j) {
                this.canvas[i][j] = 0;
            }
        }
    }

    private void noise(ArrayList<Double> info) {

    }

    public void process(String log_filename) {
        LogReader reader = new LogReader(log_filename);
        ArrayList<Double> info = new ArrayList<>();
        String markdown = reader.readLine(info);
        while (markdown != null) {
            noise(info);
            double x_saved = info.get(0);
            double y_saved = info.get(1);
            double len_saved = info.get(2);
            double angle_saved = info.get(3);

            //double impr = info.get(4);
            markdown = reader.readLine(info);
        }

        reader.close();
    }
}
