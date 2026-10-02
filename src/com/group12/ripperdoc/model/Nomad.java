package com.group12.ripperdoc.model;

public class Nomad extends BaseHuman {

    public Nomad(String name) {
        super(name, "Nomad", 50, 40, 20, 30, 100);
    }

    @Override
    public Human copy() {
        return new Nomad(getName());
    }
}
