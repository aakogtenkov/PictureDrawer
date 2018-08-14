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

    @Override
    public int hashCode() {
        return this.x * this.y + (int)this.color * this.x - (int)this.color * this.y;
    }

    @Override
    public boolean equals(Object other) {
        return (this.x == ((GrayPoint)(other)).x && this.y == ((GrayPoint)(other)).y && this.color == ((GrayPoint)(other)).color);
    }
}
