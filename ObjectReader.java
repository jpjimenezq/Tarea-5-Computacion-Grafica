package geometry3d;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class ObjectReader {
    public static ObjectData<Point2> readFromString(String input) {
        Scanner sc = new Scanner(input);

        int numPoints = sc.nextInt();
        List<Point2> points = new ArrayList<>();

        for (int i = 0; i < numPoints; i++) {
            int x = sc.nextInt();
            int y = sc.nextInt();
            points.add(new Point2(x, y));
        }

        int numEdges = sc.nextInt();
        List<Edge<Point2>> edges = new ArrayList<>();

        for (int i = 0; i < numEdges; i++) {
            int p1 = sc.nextInt();
            int p2 = sc.nextInt();
            edges.add(new Edge<>(points.get(p1), points.get(p2)));
        }

        return new ObjectData<>(points, edges);
    }
}
