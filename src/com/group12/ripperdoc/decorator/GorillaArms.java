package com.group12.ripperdoc.decorator;

import com.group12.ripperdoc.model.Human;

public class GorillaArms extends ImplantDecorator {

    public static final String NAME = "Gorilla Arms";
    public static final int PRICE = 7500;

    public GorillaArms(Human wrapped) {
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
        return wrapped.getStrength() + 35;
    }

    @Override
    public int getArmor() {
        return wrapped.getArmor() + 5;
    }

    @Override
    public int getHumanity() {
        return wrapped.getHumanity() - 14;
    }
}
