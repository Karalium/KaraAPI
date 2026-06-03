package org.kerix.karaapi.api.effect.geometry;

@FunctionalInterface
public interface PointSink {

    void add(double x, double y, double z);

    default void add(Vec3 point) {
        add(point.x(), point.y(), point.z());
    }
}
