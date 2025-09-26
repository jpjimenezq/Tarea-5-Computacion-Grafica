package geometry3d;

public class Point3 {
    public double x, y, z;

    public Point3(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Point3 point3 = (Point3) obj;
        return Double.compare(point3.x, x) == 0 &&
               Double.compare(point3.y, y) == 0 &&
               Double.compare(point3.z, z) == 0;
    }

    @Override
    public int hashCode() {
        return Double.hashCode(x) + Double.hashCode(y) + Double.hashCode(z);
    }
}
