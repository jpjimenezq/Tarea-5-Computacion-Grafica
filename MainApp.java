package geometry3d;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class MainApp extends JPanel {
    private ObjectData<Point3> data;

    // Transform parameters (object-space)
    private double objTx = 0.0; // translation X (world units)
    private double objTy = 0.0; // translation Y
    private double objTz = 0.0; // translation Z
    private double objScale = 120.0; // uniform scale
    private double rotX = 0.0; // rotation around X (radians)
    private double rotY = 0.0; // rotation around Y
    private double rotZ = 0.0; // rotation around Z

    public MainApp(ObjectData<Point3> data) {
        this.data = data;
        setPreferredSize(new Dimension(800, 600));
        setFocusable(true);
        requestFocusInWindow();

        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                int kc = e.getKeyCode();
                double transStep = 0.1; // in object units
                double rotStep = Math.toRadians(5);
                double scaleFactor = 1.1;

                switch (kc) {
                    // Translations (arrow keys and PageUp/PageDown for Z)
                    case KeyEvent.VK_LEFT -> objTx -= transStep;
                    case KeyEvent.VK_RIGHT -> objTx += transStep;
                    case KeyEvent.VK_UP -> objTy -= transStep;
                    case KeyEvent.VK_DOWN -> objTy += transStep;
                    case KeyEvent.VK_PAGE_UP -> objTz += transStep;
                    case KeyEvent.VK_PAGE_DOWN -> objTz -= transStep;

                    // Rotations
                    case KeyEvent.VK_W -> rotX -= rotStep; // rotate X up
                    case KeyEvent.VK_S -> rotX += rotStep; // rotate X down
                    case KeyEvent.VK_A -> rotZ -= rotStep; // rotate Z left
                    case KeyEvent.VK_D -> rotZ += rotStep; // rotate Z right
                    case KeyEvent.VK_Q -> rotY -= rotStep; // rotate Y left
                    case KeyEvent.VK_E -> rotY += rotStep; // rotate Y right

                    // Scaling
                    case KeyEvent.VK_Z -> objScale *= scaleFactor; // scale up
                    case KeyEvent.VK_X -> objScale /= scaleFactor; // scale down

                    // Reset
                    case KeyEvent.VK_R -> resetTransforms();
                }
                repaint();
            }
        });
    }

    private void resetTransforms() {
        objTx = objTy = objTz = 0.0;
        objScale = 120.0;
        rotX = rotY = rotZ = 0.0;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setColor(Color.WHITE);
        g2.fillRect(0, 0, getWidth(), getHeight());
        g2.setColor(Color.BLACK);

        double fov = 800.0; // projection scalar
        double projectionDistance = 400.0; // distance from camera to projection plane (in same scaled units)

        int cx = getWidth() / 2;
        int cy = getHeight() / 2;

        for (Edge<Point3> e : data.edges) {
            Point3 p1 = e.point1;
            Point3 p2 = e.point2;

            double[] v1 = transformAndProject(p1, fov, projectionDistance, cx, cy);
            double[] v2 = transformAndProject(p2, fov, projectionDistance, cx, cy);

            if (v1 == null || v2 == null) continue; // point behind camera or invalid

            g2.drawLine((int) v1[0], (int) v1[1], (int) v2[0], (int) v2[1]);
        }

        // Removed on-screen UI help. Controls are printed to console only.
    }

    private double[] transformAndProject(Point3 p, double fov, double projDist, int cx, int cy) {
        // Model space -> world space
        double x = p.x * objScale;
        double y = p.y * objScale;
        double z = p.z * objScale;

        // Rotate around X
        double cosa = Math.cos(rotX), sina = Math.sin(rotX);
        double y1 = y * cosa - z * sina;
        double z1 = y * sina + z * cosa;
        y = y1; z = z1;

        // Rotate around Y
        cosa = Math.cos(rotY); sina = Math.sin(rotY);
        double x2 = x * cosa + z * sina;
        double z2 = -x * sina + z * cosa;
        x = x2; z = z2;

        // Rotate around Z
        cosa = Math.cos(rotZ); sina = Math.sin(rotZ);
        double x3 = x * cosa - y * sina;
        double y3 = x * sina + y * cosa;
        x = x3; y = y3;

        // Translate (note translations are in object units, convert to same scaled units)
        x += objTx * objScale;
        y += objTy * objScale;
        z += objTz * objScale;

        // Simple camera at origin looking toward +Z (we will project onto plane in front)
        // For perspective projection we need z > -projDist (point not exactly at -projDist)
        double denom = z + projDist;
        if (denom <= 1e-6) return null; // behind camera or too close to projection plane

        double sx = x * fov / denom;
        double sy = y * fov / denom;

        int screenX = (int) (sx + cx);
        int screenY = (int) (sy + cy);

        return new double[]{screenX, screenY, z};
    }

    public static void main(String[] args) {
        String houseData = createHouseData();

        ObjectData<Point3> data = ObjectReader3D.readFromString(houseData);
        if (data == null) {
            System.out.println("Error al procesar los datos.");
            return;
        }

        JFrame frame = new JFrame("3D Casa Viewer - Transformaciones sobre objeto");
        MainApp app = new MainApp(data);
        frame.add(app);
        frame.pack();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);

        System.out.println("Controles:");
        System.out.println("Flechas - Trasladar X/Y");
        System.out.println("PageUp/PageDown - Trasladar Z");
        System.out.println("W/S - Rotar X");
        System.out.println("Q/E - Rotar Y");
        System.out.println("A/D - Rotar Z");
        System.out.println("Z/X - Escalar +/-");
        System.out.println("R - Reset transformaciones");
    }

    private static String createHouseData() {
        // 8 puntos del cubo más pequeño (coordenadas -0.5..0.5)
        String points = "8 " +
                "-0.5 -0.5 -0.5 " +  // 0
                "0.5 -0.5 -0.5 " +   // 1
                "0.5 -0.5 0.5 " +    // 2
                "-0.5 -0.5 0.5 " +   // 3
                "-0.5 0.5 -0.5 " +   // 4
                "0.5 0.5 -0.5 " +    // 5
                "0.5 0.5 0.5 " +     // 6
                "-0.5 0.5 0.5 ";     // 7

        // 12 aristas del cubo
        String edges = "12 " +
                "0 1 " +
                "1 2 " +
                "2 3 " +
                "3 0 " +
                "4 5 " +
                "5 6 " +
                "6 7 " +
                "7 4 " +
                "0 4 " +
                "1 5 " +
                "2 6 " +
                "3 7";

        return points + edges;
    }
}
