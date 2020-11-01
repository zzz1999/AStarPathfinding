package cn.zzz1999.astarfinder;

import java.util.Objects;

public class Vector2 implements Cloneable {

    //从左向右递增
    private final double x;
    //从上到下递增
    private final double y;

    Vector2(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public Vector2 add(double x, double y) {
        return new Vector2(this.x + x, this.y + y);
    }

    @Override
    public String toString() {
        return "X:" + this.x + "    Y:" + this.y + "| ";
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof Vector2 && this.x == ((Vector2) o).getX() && this.y == ((Vector2) o).getY();
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.x, this.y);
    }

    @Override
    public Vector2 clone() {
        Vector2 vector2 = null;
        try {
            vector2 = (Vector2) super.clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        return vector2;
    }
}
