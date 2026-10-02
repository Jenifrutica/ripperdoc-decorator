package com.group12.ripperdoc.decorator;

import com.group12.ripperdoc.model.Human;

public class MantisBlades extends ImplantDecorator {

    public static final String NAME = "Mantis Blades";
    public static final int PRICE = 9000;

    public MantisBlades(Human wrapped) {
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
    public int getStrength() {
        return wrapped.getStrength() + 20;
    }

    @Override
    public int getReflexes() {
        return wrapped.getReflexes() + 10;
    }

    @Override
    public int getHumanity() {
        return wrapped.getHumanity() - 18;
    }
}
