package org.kerix.api.utils;

import java.util.HashMap;

public class Pair<A, B> {
    private A first;
    private B second;

    public Pair(A first, B second) {
        this.first = first;
        this.second = second;
    }

    public A getFirst() {
        return first;
    }

    public B getSecond() {
        return second;
    }

    public void setSecond(B b){
        second = b;
    }
    public void setFirst(A a){
        first = a;
    }
}
