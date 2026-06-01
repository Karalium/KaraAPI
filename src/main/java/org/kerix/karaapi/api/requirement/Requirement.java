package org.kerix.karaapi.api.requirement;

@FunctionalInterface
public interface Requirement<T> {

    RequirementResult check(T target);

    default Requirement<T> and(Requirement<T> other) {
        return target -> {
            RequirementResult first = check(target);

            if (first.denied()) {
                return first;
            }

            return other.check(target);
        };
    }

    default Requirement<T> or(Requirement<T> other) {
        return target -> {
            RequirementResult first = check(target);

            if (first.allowed()) {
                return first;
            }

            return other.check(target);
        };
    }
}
