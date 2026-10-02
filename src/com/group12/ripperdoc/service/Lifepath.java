package com.group12.ripperdoc.service;

import com.group12.ripperdoc.model.BaseHuman;
import java.util.function.Function;

public record Lifepath(
        String id,
        String name,
        String className,
        String description,
        Function<String, BaseHuman> factory) {

    public BaseHuman create(String patientName) {
        return factory.apply(patientName);
    }
}
