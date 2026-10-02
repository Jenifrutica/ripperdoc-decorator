package com.group12.ripperdoc.decorator;

import com.group12.ripperdoc.model.Human;

public class OpticalCamo extends ImplantDecorator {

    public static final String NAME = "Optical Camo";
    public static final int PRICE = 8500;

    public OpticalCamo(Human wrapped) {
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
    public int getHacking() {
        return wrapped.getHacking() + 5;
    }

    @Override
    public int getArmor() {
        return wrapped.getArmor() + 10;
    }

    @Override
    public int getHumanity() {
        return wrapped.getHumanity() - 12;
    }
}
