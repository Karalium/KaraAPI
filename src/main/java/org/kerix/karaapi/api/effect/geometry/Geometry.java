package org.kerix.karaapi.api.effect.geometry;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;

public final class Geometry {

    private static final double TAU = Math.PI * 2.0;

    private Geometry() {
    }

    public static GeometrySource parametric(Parametric3 function) {
        Objects.requireNonNull(function, "function");

        return (context, sink) -> {
            if (context.amount() <= 0) {
                return;
            }

            if (context.amount() == 1) {
                sink.add(function.point(0.0, context));
                return;
            }

            for (int i = 0; i < context.amount(); i++) {
                double t = (double) i / (context.amount() - 1);
                sink.add(function.point(t, context));
            }
        };
    }

    public static GeometrySource circle(double radius) {
        return parametric((t, context) -> {
            double angle = TAU * t;

            return new Vec3(
                    Math.cos(angle) * radius,
                    0,
                    Math.sin(angle) * radius
            );
        });
    }

    public static GeometrySource ellipse(double radiusX, double radiusZ) {
        return parametric((t, context) -> {
            double angle = TAU * t;

            return new Vec3(
                    Math.cos(angle) * radiusX,
                    0,
                    Math.sin(angle) * radiusZ
            );
        });
    }

    public static GeometrySource line(Vec3 from, Vec3 to) {
        Objects.requireNonNull(from, "from");
        Objects.requireNonNull(to, "to");

        return parametric((t, context) -> from.lerp(to, t));
    }

    public static GeometrySource polyline(List<Vec3> vertices, boolean closed) {
        Objects.requireNonNull(vertices, "vertices");

        if (vertices.size() < 2) {
            throw new IllegalArgumentException("Polyline requires at least 2 vertices.");
        }

        return (context, sink) -> {
            int segmentCount = closed ? vertices.size() : vertices.size() - 1;

            if (segmentCount <= 0) {
                return;
            }

            int pointsPerSegment = Math.max(1, context.amount() / segmentCount);

            for (int segment = 0; segment < segmentCount; segment++) {
                Vec3 from = vertices.get(segment);
                Vec3 to = vertices.get((segment + 1) % vertices.size());

                line(from, to).generate(context.withAmount(pointsPerSegment), sink);
            }
        };
    }

    public static GeometrySource regularPolygon(int sides, double radius) {
        if (sides < 3) {
            throw new IllegalArgumentException("Polygon must have at least 3 sides.");
        }

        List<Vec3> vertices = new ArrayList<>();

        for (int i = 0; i < sides; i++) {
            double angle = TAU * i / sides;

            vertices.add(new Vec3(
                    Math.cos(angle) * radius,
                    0,
                    Math.sin(angle) * radius
            ));
        }

        return polyline(vertices, true);
    }

    public static GeometrySource star(int points, double outerRadius, double innerRadius) {
        if (points < 2) {
            throw new IllegalArgumentException("Star must have at least 2 points.");
        }

        List<Vec3> vertices = new ArrayList<>();

        for (int i = 0; i < points * 2; i++) {
            double radius = i % 2 == 0 ? outerRadius : innerRadius;
            double angle = TAU * i / (points * 2);

            vertices.add(new Vec3(
                    Math.cos(angle) * radius,
                    0,
                    Math.sin(angle) * radius
            ));
        }

        return polyline(vertices, true);
    }

    public static GeometrySource spiral(double startRadius, double endRadius, double turns) {
        return parametric((t, context) -> {
            double radius = startRadius + (endRadius - startRadius) * t;
            double angle = TAU * turns * t;

            return new Vec3(
                    Math.cos(angle) * radius,
                    0,
                    Math.sin(angle) * radius
            );
        });
    }

    public static GeometrySource scatter(double radius) {
        return (context, sink) -> {
            Random random = new Random(context.seed() + context.tick());

            for (int i = 0; i < context.amount(); i++) {
                double angle = random.nextDouble() * TAU;
                double distance = Math.sqrt(random.nextDouble()) * radius;

                sink.add(
                        Math.cos(angle) * distance,
                        0,
                        Math.sin(angle) * distance
                );
            }
        };
    }

    public static GeometrySource combine(GeometrySource... sources) {
        Objects.requireNonNull(sources, "sources");

        return (context, sink) -> {
            if (sources.length == 0) {
                return;
            }

            int amountPerSource = Math.max(1, context.amount() / sources.length);

            for (GeometrySource source : sources) {
                source.generate(context.withAmount(amountPerSource), sink);
            }
        };
    }

    public static GeometrySource transform(GeometrySource source, Transform3 transform) {
        Objects.requireNonNull(source, "source");
        Objects.requireNonNull(transform, "transform");

        return (context, sink) -> source.generate(context, (x, y, z) -> {
            Vec3 transformed = transform.apply(new Vec3(x, y, z));
            sink.add(transformed);
        });
    }

    public static GeometrySource repeatAroundCircle(
            GeometrySource source,
            int copies,
            double radius,
            RepeatOrientation orientation
    ) {
        Objects.requireNonNull(source, "source");
        Objects.requireNonNull(orientation, "orientation");

        if (copies <= 0) {
            throw new IllegalArgumentException("Copies must be greater than zero.");
        }

        return (context, sink) -> {
            int amountPerCopy = Math.max(1, context.amount() / copies);

            for (int i = 0; i < copies; i++) {
                double angle = TAU * i / copies;

                Transform3 transform = Transform3.identity()
                        .translate(
                                Math.cos(angle) * radius,
                                0,
                                Math.sin(angle) * radius
                        );

                transform = switch (orientation) {
                    case NONE -> transform;
                    case FACING_CENTER -> transform.rotateY(angle + Math.PI);
                    case FACING_OUTWARD -> transform.rotateY(angle);
                    case TANGENT -> transform.rotateY(angle + Math.PI / 2.0);
                };

                transform(source, transform)
                        .generate(context.withAmount(amountPerCopy), sink);
            }
        };
    }

    public static GeometrySource animatedScale(GeometrySource source, double from, double to) {
        Objects.requireNonNull(source, "source");

        return (context, sink) -> {
            double scale = from + (to - from) * context.progress();

            transform(source, Transform3.identity().scale(scale))
                    .generate(context, sink);
        };
    }

    public static GeometrySource rotating(GeometrySource source, double radiansPerProgress) {
        Objects.requireNonNull(source, "source");

        return (context, sink) -> {
            double rotation = radiansPerProgress * context.progress();

            transform(source, Transform3.identity().rotateY(rotation))
                    .generate(context, sink);
        };
    }
}
