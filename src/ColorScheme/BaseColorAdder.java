package ColorScheme;

public class BaseColorAdder implements ColorAdder {

    @Override
    public float boundColor(float original, float add_color) {
        float color = original + add_color;
        if (color < 0.0f) {
            add_color = -original;
        }
        else if (color > 1.0f) {
            add_color = 1 - original;
        }
        return add_color;
    }

    @Override
    public float addColor(float original, float add_color) {
        float color = original + add_color;
        if (color < 0.0f) {
            color = 0;
        }
        else if (color > 1.0f) {
            color = 1.0f;
        }
        return color;
    }

}
