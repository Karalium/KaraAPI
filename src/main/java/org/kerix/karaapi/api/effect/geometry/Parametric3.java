package org.kerix.karaapi.api.effect.geometry;

@FunctionalInterface
public interface Parametric3 {

    Vec3 point(double t, GeometryContext context);
}
