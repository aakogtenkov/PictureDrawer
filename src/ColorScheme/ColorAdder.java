package ColorScheme;

public interface ColorAdder {
    double addColor(double original, double add_color);
    double boundColor(double original, double add_color);
    double addColor(double original, double add_color, int precision);
    double boundColor(double original, double add_color, int precision);
}
