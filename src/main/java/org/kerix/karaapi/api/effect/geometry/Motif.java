package org.kerix.karaapi.api.effect.geometry;

import java.util.Objects;

public interface Motif extends GeometrySource {

    String key();

    static Motif of(String key, GeometrySource source) {
        return new SimpleMotif(key, source);
    }

    final class SimpleMotif implements Motif {

        private final String key;
        private final GeometrySource source;

        private SimpleMotif(String key, GeometrySource source) {
            this.key = Objects.requireNonNull(key, "key");
            this.source = Objects.requireNonNull(source, "source");
        }

        @Override
        public String key() {
            return key;
        }

        @Override
        public void generate(GeometryContext context, PointSink sink) {
            source.generate(context, sink);
        }
    }
}
