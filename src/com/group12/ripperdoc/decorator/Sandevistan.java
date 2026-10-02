package com.group12.ripperdoc.decorator;

import com.group12.ripperdoc.model.Human;

public class Sandevistan extends ImplantDecorator {

    public static final String NAME = "Sandevistan";
    public static final int PRICE = 14000;

    public Sandevistan(Human wrapped) {
        super(wrapped);
    }

    @Override
    public String getImplantName() {
        return NAME;
    }

    @Override
    public int getPrice() {
        return PRICE;
    }

    @Override
    public int getReflexes() {
        return (int) Math.round(wrapped.getReflexes() * 1.5);
    }

    @Override
    public int getHumanity() {
        return wrapped.getHumanity() - 25;
    }
}
