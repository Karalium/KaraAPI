package org.kerix.karaapi.api.effect.geometry;

public record Vec3(double x, double y, double z) {

    public Vec3 add(Vec3 other) {
        return new Vec3(x + other.x, y + other.y, z + other.z);
    }

    public Vec3 subtract(Vec3 other) {
        return new Vec3(x - other.x, y - other.y, z - other.z);
    }

    public Vec3 multiply(double scalar) {
        return new Vec3(x * scalar, y * scalar, z * scalar);
    }

    public Vec3 lerp(Vec3 other, double t) {
        return new Vec3(
                x + (other.x - x) * t,
                y + (other.y - y) * t,
                z + (other.z - z) * t
        );
    }
}
