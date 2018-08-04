package ColorScheme;

public class BaseColorAdder implements ColorAdder {

    @Override
    public double boundColor(double original, double add_color) {
        double color = original + add_color;
        if (color < 0.0) {
            add_color = -original;
        }
        else if (color > 1.0) {
            add_color = 1 - original;
        }
        return add_color;
    }

    @Override
    public double addColor(double original, double add_color) {
        double color = original + add_color;
        if (color < 0.0) {
            color = 0;
        }
        else if (color > 1.0) {
            color = 1.0;
        }
        return color;
    }

}
