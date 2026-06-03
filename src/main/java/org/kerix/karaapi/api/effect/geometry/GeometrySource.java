package org.kerix.karaapi.api.effect.geometry;

@FunctionalInterface
public interface GeometrySource {

    void generate(GeometryContext context, PointSink sink);

    default GeometrySource transform(Transform3 transform) {
        return Geometry.transform(this, transform);
    }
}
