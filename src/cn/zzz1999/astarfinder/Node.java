package cn.zzz1999.astarfinder;

import java.util.Objects;

public class Node implements Comparable<Node> {
    private Vector2 vector2;
    private Node parent; //指向父节点
    private int G; //移动代价
    private int H; //移动预计
    private int F;

    Node(Vector2 vector2, Node parent, int G, int H) {
        this.vector2 = vector2;
        this.parent = parent;
        this.G = G;
        this.H = H;
        this.F = G + H;
    }

    public Node getParent() {
        return parent;
    }

    public void setParent(Node parent) {
        this.parent = parent;
    }

    public int getG() {
        return G;
    }

    public void setG(int g) {
        G = g;
    }

    public int getH() {
        return H;
    }

    public void setH(int h) {
        H = h;
    }

    public int getF() {
        return F;
    }

    public void setF(int f) {
        F = f;
    }

    @Override
    public int compareTo(Node o) {
        Objects.requireNonNull(o);
        if (this.getF() != o.getF()) {
            return this.getF() - o.getF();
        }
        //附加值决断路径
        //0.1 = 10.0/100(期望不超过100步)
        double breaking;
        if ((breaking = this.getG() + (this.getH() * 0.1) - (o.getG() + (this.getH() * 0.1))) > 0) {
            return 1;
        } else if (breaking < 0) {
            return -1;
        } else {
            return 0;
        }
    }

    @Override
    public String toString() {
        return vector2.toString() + " G:" + this.G + " H:" + this.H + " F" + this.getF() + "| parent" + (this.parent != null ? String.valueOf(this.parent.getVector2()) : "");
    }

    public Vector2 getVector2() {
        return vector2;
    }

    public void setVector2(Vector2 vector2) {
        this.vector2 = vector2;
    }
}
