package com.group12.ripperdoc.model;

public abstract class BaseHuman implements Human {

    private final String name;
    private final String lifepath;
    private final int strength;
    private final int reflexes;
    private final int hacking;
    private final int armor;
    private final int humanity;

    protected BaseHuman(String name, String lifepath, int strength, int reflexes, int hacking, int armor, int humanity) {
        this.name = name;
        this.lifepath = lifepath;
        this.strength = strength;
        this.reflexes = reflexes;
        this.hacking = hacking;
        this.armor = armor;
        this.humanity = humanity;
    }

    public String getName() {
        return name;
    }

    public String getLifepath() {
        return lifepath;
    }

    @Override
    public String getDescription() {
        return name + " the " + lifepath;
    }

    @Override
    public int getStrength() {
        return strength;
    }

    @Override
    public int getReflexes() {
        return reflexes;
    }

    @Override
    public int getHacking() {
        return hacking;
    }

    @Override
    public int getArmor() {
        return armor;
    }

    @Override
    public int getHumanity() {
        return humanity;
    }

    @Override
    public int getCost() {
        return 0;
    }
}
