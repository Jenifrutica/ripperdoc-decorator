package com.group12.ripperdoc.decorator;

import com.group12.ripperdoc.model.Human;

public class CyberdeckQuickhack extends ImplantDecorator {

    public static final String NAME = "Cyberdeck";
    public static final int PRICE = 12000;

    public CyberdeckQuickhack(Human wrapped) {
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
        return (int) Math.round(wrapped.getHacking() * 1.4);
    }

    @Override
    public int getHumanity() {
        return wrapped.getHumanity() - 15;
    }

    @Override
    protected ImplantDecorator recreate(Human inner) {
        return new CyberdeckQuickhack(inner);
    }
}
