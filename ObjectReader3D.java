package geometry3d;

import java.util.ArrayList;
import java.util.List;

public class ObjectReader3D {
    public static ObjectData<Point3> readFromString(String input) {
        String[] tokens = input.trim().split("\\s+");
        int index = 0;
        
        // Leer número de puntos
        int numPoints = Integer.parseInt(tokens[index++]);
        List<Point3> points = new ArrayList<>();
        
        // Leer puntos
        for (int i = 0; i < numPoints; i++) {
            double x = Double.parseDouble(tokens[index++]);
            double y = Double.parseDouble(tokens[index++]);
            double z = Double.parseDouble(tokens[index++]);
            points.add(new Point3(x, y, z));
        }
        
        // Leer número de aristas
        int numEdges = Integer.parseInt(tokens[index++]);
        List<Edge<Point3>> edges = new ArrayList<>();
        
        // Leer aristas (pares de índices)
        for (int i = 0; i < numEdges; i++) {
            int p1Index = Integer.parseInt(tokens[index++]);
            int p2Index = Integer.parseInt(tokens[index++]);
            edges.add(new Edge<>(points.get(p1Index), points.get(p2Index)));
        }
        
        return new ObjectData<>(points, edges);
    }
}
