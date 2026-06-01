package org.kerix.karaapi.api.logic;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

public final class StateMachine<S> {

    private final Map<S, Set<S>> transitions = new HashMap<>();

    private S state;

    public StateMachine(S initialState) {
        this.state = Objects.requireNonNull(initialState, "initialState");
    }

    public static <S> StateMachine<S> of(S initialState) {
        return new StateMachine<>(initialState);
    }

    public StateMachine<S> allow(S from, S to) {
        Objects.requireNonNull(from, "from");
        Objects.requireNonNull(to, "to");

        transitions.computeIfAbsent(from, ignored -> new HashSet<>()).add(to);

        return this;
    }

    @SafeVarargs
    public final StateMachine<S> allow(S from, S... targets) {
        for (S target : targets) {
            allow(from, target);
        }

        return this;
    }

    public boolean canMoveTo(S target) {
        Objects.requireNonNull(target, "target");

        Set<S> allowed = transitions.get(state);

        return allowed != null && allowed.contains(target);
    }

    public boolean moveTo(S target) {
        if (!canMoveTo(target)) {
            return false;
        }

        state = target;
        return true;
    }

    public void force(S target) {
        this.state = Objects.requireNonNull(target, "target");
    }

    public S state() {
        return state;
    }

    public boolean is(S state) {
        return Objects.equals(this.state, state);
    }

    public void require(S expected) {
        if (!is(expected)) {
            throw new IllegalStateException(
                    "Expected state " + expected + " but got " + state + "."
            );
        }
    }
}