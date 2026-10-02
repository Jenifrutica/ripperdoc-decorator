package com.group12.ripperdoc.decorator;

import com.group12.ripperdoc.model.Human;

public class SubdermalArmor extends ImplantDecorator {

    public static final String NAME = "Subdermal Armor";
    public static final int PRICE = 6000;

    public SubdermalArmor(Human wrapped) {
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
        return wrapped.getReflexes() - 5;
    }

    @Override
    public int getArmor() {
        return wrapped.getArmor() + 30;
    }

    @Override
    public int getHumanity() {
        return wrapped.getHumanity() - 12;
    }
}
