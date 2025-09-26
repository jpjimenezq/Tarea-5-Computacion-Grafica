package geometry3d;

public class Matrix3x3 {
    public double[][] m = new double[3][3];

    public Matrix3x3(double[][] values) {
        if (values.length == 3 && values[0].length == 3) {
            this.m = values;
        } else {
            throw new IllegalArgumentException("Matrix must be 3x3");
        }
    }
}
