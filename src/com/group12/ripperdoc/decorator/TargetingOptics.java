package com.group12.ripperdoc.decorator;

import com.group12.ripperdoc.model.Human;

public class TargetingOptics extends ImplantDecorator {

    public static final String NAME = "Targeting Optics";
    public static final int PRICE = 9500;

    public TargetingOptics(Human wrapped) {
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
    public int getHacking() {
        return wrapped.getHacking() + 15;
    }

    @Override
    public int getHumanity() {
        return wrapped.getHumanity() - 10;
    }

    @Override
    protected ImplantDecorator recreate(Human inner) {
        return new TargetingOptics(inner);
    }
}
