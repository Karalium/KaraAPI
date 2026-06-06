package org.kerix.karaapi.api.effect.geometry;

public final class Transform3 {

    private final double translateX;
    private final double translateY;
    private final double translateZ;

    private final double rotateX;
    private final double rotateY;
    private final double rotateZ;

    private final double scaleX;
    private final double scaleY;
    private final double scaleZ;

    private Transform3(
            double translateX,
            double translateY,
            double translateZ,
            double rotateX,
            double rotateY,
            double rotateZ,
            double scaleX,
            double scaleY,
            double scaleZ
    ) {
        this.translateX = translateX;
        this.translateY = translateY;
        this.translateZ = translateZ;
        this.rotateX = rotateX;
        this.rotateY = rotateY;
        this.rotateZ = rotateZ;
        this.scaleX = scaleX;
        this.scaleY = scaleY;
        this.scaleZ = scaleZ;
    }

    public static Transform3 identity() {
        return new Transform3(
                0, 0, 0,
                0, 0, 0,
                1, 1, 1
        );
    }

    public Transform3 translate(double x, double y, double z) {
        return new Transform3(
                translateX + x,
                translateY + y,
                translateZ + z,
                rotateX,
                rotateY,
                rotateZ,
                scaleX,
                scaleY,
                scaleZ
        );
    }

    public Transform3 rotateX(double radians) {
        return new Transform3(
                translateX,
                translateY,
                translateZ,
                rotateX + radians,
                rotateY,
                rotateZ,
                scaleX,
                scaleY,
                scaleZ
        );
    }

    public Transform3 rotateY(double radians) {
        return new Transform3(
                translateX,
                translateY,
                translateZ,
                rotateX,
                rotateY + radians,
                rotateZ,
                scaleX,
                scaleY,
                scaleZ
        );
    }

    public Transform3 rotateZ(double radians) {
        return new Transform3(
                translateX,
                translateY,
                translateZ,
                rotateX,
                rotateY,
                rotateZ + radians,
                scaleX,
                scaleY,
                scaleZ
        );
    }

    public Transform3 scale(double scale) {
        return scale(scale, scale, scale);
    }

    public Transform3 scale(double x, double y, double z) {
        return new Transform3(
                translateX,
                translateY,
                translateZ,
                rotateX,
                rotateY,
                rotateZ,
                scaleX * x,
                scaleY * y,
                scaleZ * z
        );
    }

    public Vec3 apply(Vec3 point) {
        double x = point.x() * scaleX;
        double y = point.y() * scaleY;
        double z = point.z() * scaleZ;

        double cosX = Math.cos(rotateX);
        double sinX = Math.sin(rotateX);

        double y1 = y * cosX - z * sinX;
        double z1 = y * sinX + z * cosX;
        y = y1;
        z = z1;

        double cosY = Math.cos(rotateY);
        double sinY = Math.sin(rotateY);

        double x1 = x * cosY + z * sinY;
        double z2 = -x * sinY + z * cosY;
        x = x1;
        z = z2;

        double cosZ = Math.cos(rotateZ);
        double sinZ = Math.sin(rotateZ);

        double x2 = x * cosZ - y * sinZ;
        double y2 = x * sinZ + y * cosZ;
        x = x2;
        y = y2;

        return new Vec3(
                x + translateX,
                y + translateY,
                z + translateZ
        );
    }
}
