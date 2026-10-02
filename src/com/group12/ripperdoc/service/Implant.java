package com.group12.ripperdoc.service;

import com.group12.ripperdoc.model.Human;
import java.util.function.UnaryOperator;

public record Implant(
        String id,
        String name,
        String className,
        BodySlot slot,
        int price,
        String effect,
        String description,
        UnaryOperator<Human> installer) {

    public Human installOn(Human patient) {
        return installer.apply(patient);
    }
}
