package geometry3d;

import java.util.Scanner;

public class MainConsole {
    private static ObjectData<Point3> data;
    private static double translateX = 0;
    private static double translateY = 0;
    private static double translateZ = 2;
    private static double angleX = 0, angleY = 0, angleZ = 0;
    private static double scale = 1.0;
    private static final int WIDTH = 60;
    private static final int HEIGHT = 30;

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.println("=== Geometry 3D Console Viewer ===");
        System.out.println("Ingrese los datos 3D (formato: x,y,z para puntos, separados por ';' para aristas):");
        System.out.println("Ejemplo: 0,0,0;1,0,0;0,1,0;0,0,1;0,0,0-1,0,0;0,0,0-0,1,0;0,0,0-0,0,1");

        String input = scanner.nextLine();
        data = ObjectReader3D.readFromString(input);

        if (data == null) {
            System.out.println("Error al procesar los datos. Saliendo...");
            return;
        }

        System.out.println("\nControles:");
        System.out.println("W/S - Mover arriba/abajo");
        System.out.println("A/D - Mover izquierda/derecha");
        System.out.println("T/G - Mover adelante/atrás");
        System.out.println("Q/E - Rotar eje Y izquierda/derecha");
        System.out.println("R/F - Rotar eje X +/-");
        System.out.println("Z/X - Rotar eje Z +/-");
        System.out.println("+/- - Escalar");
        System.out.println("C - Limpiar transformaciones");
        System.out.println("EXIT - Salir");

        boolean running = true;
        while (running) {
            renderFrame();
            System.out.print("\nComando: ");
            String command = scanner.nextLine().toUpperCase();

            switch (command) {
                case "W" -> translateY += 1;
                case "S" -> translateY -= 1;
                case "A" -> translateX -= 1;
                case "D" -> translateX += 1;
                case "T" -> translateZ += 1;
                case "G" -> translateZ -= 1;
                case "Q" -> angleY -= Math.toRadians(15);
                case "E" -> angleY += Math.toRadians(15);
                case "R" -> angleX += Math.toRadians(15);
                case "F" -> angleX -= Math.toRadians(15);
                case "Z" -> angleZ += Math.toRadians(15);
                case "X" -> angleZ -= Math.toRadians(15);
                case "+" -> scale *= 1.2;
                case "-" -> scale *= 0.8;
                case "C" -> resetTransformations();
                case "EXIT" -> running = false;
                default -> System.out.println("Comando no reconocido");
            }
        }

        scanner.close();
        System.out.println("Programa terminado.");
    }

    private static void resetTransformations() {
        translateX = translateY = translateZ = 0;
        angleX = angleY = angleZ = 0;
        scale = 1.0;
    }

    private static void renderFrame() {
        char[][] buffer = new char[HEIGHT][WIDTH];

        // Inicializar buffer con espacios
        for (int i = 0; i < HEIGHT; i++) {
            for (int j = 0; j < WIDTH; j++) {
                buffer[i][j] = ' ';
            }
        }

        double fov = 100;
        double viewDistance = 4;

        for (Edge<Point3> e : data.edges) {
            Point3 p1 = e.point1;
            Point3 p2 = e.point2;

            double[] v1 = transform(p1);
            double[] v2 = transform(p2);

            int x1 = (int) (v1[0] * fov / (v1[2] + viewDistance) + WIDTH / 2 + translateX);
            int y1 = (int) (v1[1] * fov / (v1[2] + viewDistance) + HEIGHT / 2 + translateY);

            int x2 = (int) (v2[0] * fov / (v2[2] + viewDistance) + WIDTH / 2 + translateX);
            int y2 = (int) (v2[1] * fov / (v2[2] + viewDistance) + HEIGHT / 2 + translateY);

            // Dibujar línea en el buffer
            drawLine(buffer, x1, HEIGHT - y1 - 1, x2, HEIGHT - y2 - 1);
        }

        // Imprimir el frame
        System.out.println("\n".repeat(3));
        for (int i = 0; i < HEIGHT; i++) {
            for (int j = 0; j < WIDTH; j++) {
                System.out.print(buffer[i][j]);
            }
            System.out.println();
        }

        // Mostrar información de transformaciones
        System.out.printf("\nPosición: X=%.1f Y=%.1f Z=%.1f", translateX, translateY, translateZ);
        System.out.printf("\nRotación: X=%.1f° Y=%.1f° Z=%.1f°",
                         Math.toDegrees(angleX), Math.toDegrees(angleY), Math.toDegrees(angleZ));
        System.out.printf("\nEscala: %.2f", scale);
    }

    private static void drawLine(char[][] buffer, int x1, int y1, int x2, int y2) {
        // Algoritmo de Bresenham para dibujar líneas
        int dx = Math.abs(x2 - x1);
        int dy = Math.abs(y2 - y1);
        int sx = (x1 < x2) ? 1 : -1;
        int sy = (y1 < y2) ? 1 : -1;
        int err = dx - dy;

        while (true) {
            if (x1 >= 0 && x1 < WIDTH && y1 >= 0 && y1 < HEIGHT) {
                buffer[y1][x1] = '*';
            }

            if (x1 == x2 && y1 == y2) break;

            int e2 = 2 * err;
            if (e2 > -dy) {
                err -= dy;
                x1 += sx;
            }
            if (e2 < dx) {
                err += dx;
                y1 += sy;
            }
        }
    }

    private static double[] transform(Point3 p) {
        double x = p.x * scale;
        double y = p.y * scale;
        double z = p.z * scale + translateZ;
        
        // rotación X
        double cosX = Math.cos(angleX), sinX = Math.sin(angleX);
        double y1 = y * cosX - z * sinX;
        double z1 = y * sinX + z * cosX;
        y = y1; z = z1;

        // rotación Y
        double cosY = Math.cos(angleY), sinY = Math.sin(angleY);
        double x1 = x * cosY + z * sinY;
        double z2 = -x * sinY + z * cosY;
        x = x1; z = z2;

        // rotación Z
        double cosZ = Math.cos(angleZ), sinZ = Math.sin(angleZ);
        double x2 = x * cosZ - y * sinZ;
        double y2 = x * sinZ + y * cosZ;
        x = x2; y = y2;

        return new double[]{x, y, z};
    }
}