package com.group12.ripperdoc.decorator;

import com.group12.ripperdoc.model.Human;

public class KerenzikovReflex extends ImplantDecorator {

    public static final String NAME = "Kerenzikov";
    public static final int PRICE = 5000;

    public KerenzikovReflex(Human wrapped) {
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
        return wrapped.getReflexes() + 15;
    }

    @Override
    public int getHumanity() {
        return wrapped.getHumanity() - 10;
    }
}
