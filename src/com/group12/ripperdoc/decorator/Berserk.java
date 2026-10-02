package com.group12.ripperdoc.decorator;

import com.group12.ripperdoc.model.Human;

public class Berserk extends ImplantDecorator {

    public static final String NAME = "Militech Berserk";
    public static final int PRICE = 16000;

    public Berserk(Human wrapped) {
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
        return wrapped.getStrength() + 40;
    }

    @Override
    public int getHumanity() {
        return wrapped.getHumanity() - 30;
    }

    @Override
    protected ImplantDecorator recreate(Human inner) {
        return new Berserk(inner);
    }
}
