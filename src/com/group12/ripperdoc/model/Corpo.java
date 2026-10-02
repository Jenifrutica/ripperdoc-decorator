package com.group12.ripperdoc.model;

public class Corpo extends BaseHuman {

    public Corpo(String name) {
        super(name, "Corpo", 25, 30, 55, 15, 90);
    }

    @Override
    public Human copy() {
        return new Corpo(getName());
    }
}
