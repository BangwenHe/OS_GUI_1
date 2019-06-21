package com;

public enum State {
    READY,
    RUN,
    BLOCK;

    public static String getState(State s) {
        if (s == READY) {
            return "ready";
        } else if (s == RUN) {
            return "run";
        } else if (s == BLOCK) {
            return "block";
        } else {
            return "wrong state";
        }
    }

    public boolean equal(State s) {
        return this == s;
    }
}
