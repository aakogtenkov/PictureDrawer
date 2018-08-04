package Generators;

public class GrayPoint{
    public int x;
    public int y;
    public double color;

    public GrayPoint(int x, int y, double color) {
        this.x = x;
        this.y = y;
        this.color = color;
    }

    public boolean equal(GrayPoint other) {
        return (this.x == other.x && this.y == other.y && this.color == other.color);
    }
}
