package geometry3d;

public class Matrix4x4 {
    public double[][] m = new double[4][4];

    public Matrix4x4(double[][] values) {
        if (values.length == 4 && values[0].length == 4) {
            this.m = values;
        } else {
            throw new IllegalArgumentException("Matrix must be 4x4");
        }
    }
}
