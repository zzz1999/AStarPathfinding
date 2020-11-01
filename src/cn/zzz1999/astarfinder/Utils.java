package cn.zzz1999.astarfinder;

public class Utils {
    public static final int ACCORDING_X_OBTAIN_Y = 0;
    public static final int ACCORDING_Y_OBTAIN_X = 1;

    /**
     * 一次函数求解 根据X求Y或根据Y求X
     *
     * @param pos1    坐标1
     * @param pos2    坐标2
     * @param element 输入的X或者Y
     * @param type    {@link Utils#ACCORDING_X_OBTAIN_Y} 或者 {@link Utils#ACCORDING_Y_OBTAIN_X}
     * @return 结果 如果无法得出结果则返回{@link Double#MAX_VALUE}
     */
    public static double calLinearFunction(Vector2 pos1, Vector2 pos2, double element, int type) {
        if (pos1.getX() == pos2.getX()) {
            if (type == ACCORDING_Y_OBTAIN_X) {
                return pos1.getX();
            } else {
                return Double.MAX_VALUE;
            }
        } else if (pos1.getY() == pos2.getY()) {
            if (type == ACCORDING_X_OBTAIN_Y) {
                return pos1.getY();
            } else {
                return Double.MAX_VALUE;
            }
        } else {
            if (type == ACCORDING_X_OBTAIN_Y) {
                //Y = [(x-x1)(y1-y2)/(x1-x2) ] + y1
                return (((element - pos1.getX()) * (pos1.getY() - pos2.getY())) / (pos1.getX() - pos2.getX())) + pos1.getY();
            } else {//ACCORDING_Y_OBTAIN_X
                //X = [(y-y1)(x1-x2)]/(y1-y2) + x1
                return (((element - pos1.getY()) * (pos1.getX() - pos2.getX())) / (pos1.getY() - pos2.getY())) + pos1.getX();
            }
        }
    }
}
