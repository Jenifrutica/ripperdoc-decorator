package com.group12.ripperdoc.decorator;

import com.group12.ripperdoc.model.Human;

public class ProjectileArms extends ImplantDecorator {

    public static final String NAME = "Projectile Arms";
    public static final int PRICE = 18000;

    public ProjectileArms(Human wrapped) {
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
        return wrapped.getStrength() + 25;
    }

    @Override
    public int getArmor() {
        return wrapped.getArmor() + 15;
    }

    @Override
    public int getHumanity() {
        return wrapped.getHumanity() - 20;
    }

    @Override
    protected ImplantDecorator recreate(Human inner) {
        return new ProjectileArms(inner);
    }
}
