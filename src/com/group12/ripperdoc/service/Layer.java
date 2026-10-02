package com.group12.ripperdoc.service;

import com.group12.ripperdoc.model.Human;

public record Layer(
        String id,
        String name,
        String className,
        String description,
        int strength,
        int reflexes,
        int hacking,
        int armor,
        int humanity,
        int cost) {

    public static Layer of(String id, String name, String className, Human human) {
        return new Layer(
                id,
                name,
                className,
                human.getDescription(),
                human.getStrength(),
                human.getReflexes(),
                human.getHacking(),
                human.getArmor(),
                human.getHumanity(),
                human.getCost());
    }
}
