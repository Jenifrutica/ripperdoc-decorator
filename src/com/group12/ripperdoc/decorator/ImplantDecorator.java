package com.group12.ripperdoc.decorator;

import com.group12.ripperdoc.model.Human;

public abstract class ImplantDecorator implements Human {

    protected final Human wrapped;

    protected ImplantDecorator(Human wrapped) {
        this.wrapped = wrapped;
    }

    public abstract String getImplantName();

    public abstract int getPrice();

    protected abstract ImplantDecorator recreate(Human inner);

    @Override
    public Human copy() {
        return recreate(wrapped.copy());
    }

    @Override
    public String getDescription() {
        return wrapped.getDescription() + " + " + getImplantName();
    }

    @Override
    public int getStrength() {
        return wrapped.getStrength();
    }

    @Override
    public int getReflexes() {
        return wrapped.getReflexes();
    }

    @Override
    public int getHacking() {
        return wrapped.getHacking();
    }

    @Override
    public int getArmor() {
        return wrapped.getArmor();
    }

    @Override
    public int getHumanity() {
        return wrapped.getHumanity();
    }

    @Override
    public int getCost() {
        return wrapped.getCost() + getPrice();
    }
}
