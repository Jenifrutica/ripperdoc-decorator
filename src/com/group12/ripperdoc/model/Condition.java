package com.group12.ripperdoc.model;

public enum Condition {

    STABLE,
    UNSTABLE,
    CYBERPSYCHOSIS;

    public static final int STABLE_THRESHOLD = 50;
    public static final int PSYCHOSIS_THRESHOLD = 20;

    public static Condition from(int humanity) {
        if (humanity >= STABLE_THRESHOLD) {
            return STABLE;
        }
        if (humanity >= PSYCHOSIS_THRESHOLD) {
            return UNSTABLE;
        }
        return CYBERPSYCHOSIS;
    }
}
