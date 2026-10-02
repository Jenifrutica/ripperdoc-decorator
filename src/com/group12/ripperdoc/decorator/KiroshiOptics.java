package com.group12.ripperdoc.decorator;

import com.group12.ripperdoc.model.Human;

public class KiroshiOptics extends ImplantDecorator {

    public static final String NAME = "Kiroshi Optics";
    public static final int PRICE = 3000;

    public KiroshiOptics(Human wrapped) {
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
        return wrapped.getReflexes() + 5;
    }

    @Override
    public int getHacking() {
        return wrapped.getHacking() + 10;
    }

    @Override
    public int getHumanity() {
        return wrapped.getHumanity() - 6;
    }
}
