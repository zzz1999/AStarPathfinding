package cn.zzz1999.astarfinder;

import java.util.*;

public class AStarRouterFinder {
    public static final int MAP_ELEMENT_SPACE = 0;
    public static final int MAP_ELEMENT_WALL = 1;
    public static final int MAP_ELEMENT_START = 2;
    public static final int MAP_ELEMENT_DESTINATION = 3;
    public static final int MAP_ELEMENT_ROUTE = 4;

    private static int length; //横向
    private static int width; //纵向

    private static final int DIRECT_VALUE = 10; //横竖移动代价
    private static final int OBLIQUE_VALUE = 14; //斜移动代价

    private final Queue<Node> openList = new PriorityQueue<>();
    private final List<Node> closeList = new ArrayList<>();

    private static int ROUTE_FINDING_LIMIT = 512;

    private static Vector2 origin;
    private static Vector2 destination;
    private static Node originNode;
    private static Node destinationNode;

    private static int[][] map;

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("请输入地图的横向长度:");
        length = scanner.nextInt();
        System.out.println("请输入地图的纵向长度:");
        width = scanner.nextInt();
        int[][] mapInput = new int[width][length];
        System.out.println("请输入元素," + MAP_ELEMENT_SPACE + "代表空地," + MAP_ELEMENT_WALL + "代表障碍物," + MAP_ELEMENT_START + "代表起点," +
                MAP_ELEMENT_DESTINATION + "代表终点。");
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < length; j++) {
                System.out.println("请输入地图第" + (j + 1) + "行,第" + (i + 1) + "列的元素:");
                int ipe = scanner.nextInt();
                mapInput[i][j] = ipe;
                if (ipe == 2) {
                    origin = new Vector2(j + 0.5, i + 0.5);
                    originNode = new Node(origin, null, 0, 0);
                } else if (ipe == 3) {
                    destination = new Vector2(j + 0.5, i + 0.5);
                    destinationNode = new Node(destination, null, 0, 0);
                }
            }
        }
        System.out.println("起点被设置为" + origin.toString() + "\n终点被设置为" + destination);
        map = mapInput;
        if (length * width > 144) {
            System.out.println("请输入寻路节点搜索上限:");
            ROUTE_FINDING_LIMIT = scanner.nextInt();
        }
        new AStarRouterFinder().startSearching();
        //TODO 埋葬深度
    }

    private void startSearching() {
        long startTime = System.currentTimeMillis();
        Node presentNode = originNode;
        closeList.add(presentNode);

        while (!isPositionOverlap(presentNode.getVector2(), destination)) {
            putNeighborNodeIntoOpen(presentNode);
            if (openList.peek() != null && ROUTE_FINDING_LIMIT-- > 0) {
                closeList.add(presentNode = openList.poll());
            } else {
                System.err.println("无可用路径或超过检索上限");
                return;
            }
        }
        if (!presentNode.getVector2().equals(destination)) {
            closeList.add(new Node(destination, presentNode, 0, 0));
        }
        List<Node> findingPath = getPathRoute();

        findingPath = FloydSmooth(findingPath);

        generateRoute(findingPath);
        System.out.println("计算完成,路径为" + MAP_ELEMENT_ROUTE + "的连线,用时" + (System.currentTimeMillis() - startTime) / 1000F + "秒");
        printMap();
    }

    private int calManhattan(Vector2 n1, Vector2 n2) {
        return (int) (10 * (Math.abs(n1.getX() - n2.getX()) + Math.abs(n1.getY() - n2.getY())));
    }

    private boolean isWalkable(Vector2 vector2) {
        return isInBorder(vector2) && map[(int) vector2.getY()][(int) vector2.getX()] != MAP_ELEMENT_WALL;
    }

    private boolean isWalkable(int x, int y) {
        return isWalkable(new Vector2(x, y));
    }

    private void printMap() {
        for (int y = 0; y < width; y++) {
            for (int i = 0; i < length; i++) {
                System.out.print(map[y][i] + "  ");
            }
            System.out.println();
        }
    }

    private void generateRoute(List<Node> arrayList) {
        for (Node node : arrayList) {
            if (map[(int) node.getVector2().getY()][(int) node.getVector2().getX()] == 0) {
                map[(int) node.getVector2().getY()][(int) node.getVector2().getX()] = MAP_ELEMENT_ROUTE;
            }
        }
    }

    private void putNeighborNodeIntoOpen(Node node) {
        boolean N, E, S, W;
        /*    0 1 2 X
         * 0 NW N NE
         * 1  W O E
         * 2 WS S ES
         * Y
         */

        Vector2 attempt = node.getVector2();

        Vector2 vector2;
        vector2 = attempt.add(1, 0);
        if (E = (isWalkable(vector2)) && !isContainsInClose(vector2)) {
            Node nodeNear = getNodeInOpenByVector2(vector2);
            if (nodeNear == null) {
                this.openList.offer(new Node(vector2, node, DIRECT_VALUE + node.getG(), calManhattan(vector2, destination)));
            } else {
                //已经在Open列表里 检查从当前节点到那里是否更短
                //Node nodeTemp = getNodeInOpenByVector2(vector2);
                if (node.getG() + DIRECT_VALUE < nodeNear.getG()) {
                    nodeNear.setParent(node);
                    nodeNear.setG(node.getG() + DIRECT_VALUE);
                    nodeNear.setF(nodeNear.getG() + nodeNear.getH());
                }
            }
        }

        vector2 = attempt.add(0, 1);
        if (S = (isWalkable(vector2)) && !isContainsInClose(vector2)) {
            Node nodeNear = getNodeInOpenByVector2(vector2);
            if (nodeNear == null) {
                this.openList.offer(new Node(vector2, node, DIRECT_VALUE + node.getG(), calManhattan(vector2, destination)));
            } else {
                if (node.getG() + DIRECT_VALUE < nodeNear.getG()) {
                    nodeNear.setParent(node);
                    nodeNear.setG(node.getG() + DIRECT_VALUE);
                    nodeNear.setF(nodeNear.getG() + nodeNear.getH());
                }
            }
        }

        vector2 = attempt.add(-1, 0);
        if (W = (isWalkable(vector2)) && !isContainsInClose(vector2)) {
            Node nodeNear = getNodeInOpenByVector2(vector2);
            if (nodeNear == null) {
                this.openList.offer(new Node(vector2, node, DIRECT_VALUE + node.getG(), calManhattan(vector2, destination)));
            } else {
                if (node.getG() + DIRECT_VALUE < nodeNear.getG()) {
                    nodeNear.setParent(node);
                    nodeNear.setG(node.getG() + DIRECT_VALUE);
                    nodeNear.setF(nodeNear.getG() + nodeNear.getH());
                }
            }
        }

        vector2 = attempt.add(0, -1);
        if (N = (isWalkable(vector2)) && !isContainsInClose(vector2)) {
            Node nodeNear = getNodeInOpenByVector2(vector2);
            if (nodeNear == null) {
                this.openList.offer(new Node(vector2, node, DIRECT_VALUE + node.getG(), calManhattan(vector2, destination)));
            } else {
                if (node.getG() + DIRECT_VALUE < nodeNear.getG()) {
                    nodeNear.setParent(node);
                    nodeNear.setG(node.getG() + DIRECT_VALUE);
                    nodeNear.setF(nodeNear.getG() + nodeNear.getH());
                }
            }
        }

        vector2 = attempt.add(1, -1);
        if (N && E && isWalkable(vector2) && !isContainsInClose(vector2)) {
            Node nodeNear = getNodeInOpenByVector2(vector2);
            if (nodeNear == null) {
                this.openList.offer(new Node(vector2, node, OBLIQUE_VALUE + node.getG(), calManhattan(vector2, destination)));
            } else {
                if (node.getG() + OBLIQUE_VALUE < nodeNear.getG()) {
                    nodeNear.setParent(node);
                    nodeNear.setG(node.getG() + OBLIQUE_VALUE);
                    nodeNear.setF(nodeNear.getG() + nodeNear.getH());
                }
            }
        }

        vector2 = attempt.add(1, 1);
        if (E && S && isWalkable(vector2) && !isContainsInClose(vector2)) {
            Node nodeNear = getNodeInOpenByVector2(vector2);
            if (nodeNear == null) {
                this.openList.offer(new Node(vector2, node, OBLIQUE_VALUE + node.getG(), calManhattan(vector2, destination)));
            } else {
                if (node.getG() + OBLIQUE_VALUE < nodeNear.getG()) {
                    nodeNear.setParent(node);
                    nodeNear.setG(node.getG() + OBLIQUE_VALUE);
                    nodeNear.setF(nodeNear.getG() + nodeNear.getH());
                }
            }
        }

        vector2 = attempt.add(-1, 1);
        if (W && S && isWalkable(vector2) && !isContainsInClose(vector2)) {
            Node nodeNear = getNodeInOpenByVector2(vector2);
            if (nodeNear == null) {
                this.openList.offer(new Node(vector2, node, OBLIQUE_VALUE + node.getG(), calManhattan(vector2, destination)));
            } else {
                if (node.getG() + OBLIQUE_VALUE < nodeNear.getG()) {
                    nodeNear.setParent(node);
                    nodeNear.setG(node.getG() + OBLIQUE_VALUE);
                    nodeNear.setF(nodeNear.getG() + nodeNear.getH());
                }
            }
        }

        vector2 = attempt.add(-1, -1);
        if (W & N && isWalkable(vector2) && !isContainsInClose(vector2)) {
            Node nodeNear = getNodeInOpenByVector2(vector2);
            if (nodeNear == null) {
                this.openList.offer(new Node(vector2, node, OBLIQUE_VALUE + node.getG(), calManhattan(vector2, destination)));
            } else {
                if (node.getG() + OBLIQUE_VALUE < nodeNear.getG()) {
                    nodeNear.setParent(node);
                    nodeNear.setG(node.getG() + OBLIQUE_VALUE);
                    nodeNear.setF(nodeNear.getG() + nodeNear.getH());
                }
            }
        }
    }

    private boolean isInBorder(Vector2 vector2) {
        return vector2.getX() >= 0 && vector2.getX() < length && vector2.getY() >= 0 && vector2.getY() < width;
    }

    private boolean isInBorder(int x, int y) {
        return x >= 0 && x < length && y >= 0 && y < width;
    }

    private int getMapElement(int x, int y) {
        if (isInBorder(new Vector2(x, y))) {
            return map[y][x];
        }
        return -1;
    }

    private int getMapElement(Vector2 vector2) {
        if (isInBorder(vector2)) {
            return map[(int) vector2.getY()][(int) vector2.getX()];
        }
        return -1;
    }

    private Node getNodeInOpenByVector2(Vector2 vector2) {
        for (Node node : this.openList) {
            if (vector2.equals(node.getVector2())) {
                return node;
            }
        }

        return null;
    }

    private boolean isContainsInOpen(Vector2 vector2) {
        return getNodeInOpenByVector2(vector2) != null;
    }

    private Node getNodeInCloseByVector2(Vector2 vector2) {
        for (Node node : this.closeList) {
            if (vector2.equals(node.getVector2())) {
                return node;
            }
        }
        return null;
    }

    private boolean isContainsInClose(Vector2 vector2) {
        return getNodeInCloseByVector2(vector2) != null;
    }

    /**
     * 判断两个点之间有没有障碍物
     *
     * @param node1 点1
     * @param node2 点2
     * @return 返回false如果两节点之间没有障碍物
     */
    private boolean hasBarrier(Node node1, Node node2) {
        return hasBarrier(node1.getVector2(), node2.getVector2());
    }

    private boolean hasBarrier(Vector2 pos1, Vector2 pos2) {
        if (pos1.equals(pos2)) {
            return false;
        }
        boolean traverseDirection = Math.abs(pos1.getX() - pos2.getX()) > Math.abs(pos1.getY() - pos2.getY());//true为横向遍历 false为纵向遍历
        if (traverseDirection) {
            //横向遍历
            double loopStart = Math.min(pos1.getX(), pos2.getX());
            double loopEnd = Math.max(pos1.getX(), pos2.getX());
            List<Vector2> list = new ArrayList<>();
            for (double i = Math.ceil(loopStart); i <= Math.floor(loopEnd); i += 1.0) {
                list.add(new Vector2(i, Utils.calLinearFunction(pos1, pos2, i, Utils.ACCORDING_X_OBTAIN_Y)));
            }
            Set<Vector2> set = getNodesUnderPoints(list);
            for (Vector2 vector2 : set) {
                if (getMapElement(vector2) == MAP_ELEMENT_WALL) {
                    return true;
                }
            }
            return false;
        } else {
            //纵向
            double loopStart = Math.min(pos1.getY(), pos2.getY());
            double loopEnd = Math.max(pos1.getY(), pos2.getY());
            List<Vector2> list = new ArrayList<>();
            for (double i = Math.ceil(loopStart); i <= Math.floor(loopEnd); i += 1.0) {
                list.add(new Vector2(Utils.calLinearFunction(pos1, pos2, i, Utils.ACCORDING_Y_OBTAIN_X), i));
            }
            Set<Vector2> set = getNodesUnderPoints(list);
            for (Vector2 vector2 : set) {
                if (getMapElement(vector2) == MAP_ELEMENT_WALL) {
                    return true;
                }
            }
            return false;
        }

    }

    private Set<Vector2> getNodesUnderPoints(List<Vector2> list) {
        Set<Vector2> set = new HashSet<>();
        for (Vector2 vector2 : list) {
            boolean xIsInt = vector2.getX() % 1 == 0;
            boolean yIsInt = vector2.getY() % 1 == 0;
            if (xIsInt && yIsInt) {
                set.add(new Vector2(Math.floor(vector2.getX()), Math.floor(vector2.getY())));
                set.add(new Vector2(Math.floor(vector2.getX()) - 1, Math.floor(vector2.getY())));
                set.add(new Vector2(Math.floor(vector2.getX()) - 1, Math.floor(vector2.getY()) - 1));
                set.add(new Vector2(Math.floor(vector2.getX()), Math.floor(vector2.getY()) - 1));
            } else if (xIsInt /*&& !yIsInt*/) {
                set.add(new Vector2(Math.floor(vector2.getX()), Math.floor(vector2.getY())));
                set.add(new Vector2(Math.floor(vector2.getX() - 1), Math.floor(vector2.getY())));
            } else if (/*!xIsInt &&*/ yIsInt) {
                set.add(new Vector2(Math.floor(vector2.getX()), Math.floor(vector2.getY())));
                set.add(new Vector2(Math.floor(vector2.getX()), Math.floor(vector2.getY()) - 1));
            } else {
                set.add(new Vector2(Math.floor(vector2.getX()), Math.floor(vector2.getY())));
            }
        }
        return set;
    }

    /**
     * 弗洛伊德路径平滑
     *
     * @param array 路径
     * @return 平滑后的路径
     */
    private List<Node> FloydSmooth(List<Node> array) {
        int index = 0;
        int current = 1;
        if (array.size() > 2) {
            while (current < array.size()) {
                if (hasBarrier(array.get(index), array.get(current))) {
                    array.get(current - 1).setParent(array.get(index));
                    index = current - 1;
                }
                current++;
            }
            current = array.size() - 1;
            array.get(current).setParent(array.get(index));

            Node temp = array.get(array.size() - 1);
            List<Node> tempL = new ArrayList<>();
            tempL.add(temp);
            while (temp.getParent() != null) {
                tempL.add((temp = temp.getParent()));
            }
            Collections.reverse(tempL);
            return tempL;
        }
        return array;
    }

    private boolean isPositionOverlap(Vector2 vector2, Vector2 vector2_) {
        return (int) vector2.getX() == (int) vector2_.getX()
                && (int) vector2.getY() == (int) vector2_.getY();
    }

    public double distance(Vector2 vector2, Vector2 vector2_) {
        return Math.sqrt(Math.abs(vector2.getX() - vector2_.getX()) + Math.abs(vector2.getY() - vector2_.getY()));
    }

    public Vector2 parseToVector2(double x, double y) {
        return new Vector2(Math.floor(x), Math.floor(y));
    }

    private List<Node> getPathRoute() {
        List<Node> nodes = new ArrayList<>();
        Node temp = closeList.get(closeList.size() - 1);
        nodes.add(temp);
        while (!temp.getParent().getVector2().equals(origin)) {
            nodes.add(temp = temp.getParent());
        }
        nodes.add(temp.getParent());
        Collections.reverse(nodes);
        return nodes;
    }
}