package geometry3d;

import java.util.List;

public class ObjectData<T> {
    public List<T> points;
    public List<Edge<T>> edges;

    public ObjectData(List<T> points, List<Edge<T>> edges) {
        this.points = points;
        this.edges = edges;
    }
}
