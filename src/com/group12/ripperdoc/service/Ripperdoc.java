package com.group12.ripperdoc.service;

import com.group12.ripperdoc.model.Condition;
import com.group12.ripperdoc.model.Human;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

public class Ripperdoc {

    private static final String DEFAULT_NAME = "V";
    private static final int MAX_NAME_LENGTH = 24;

    private final ImplantCatalog catalog;

    public Ripperdoc(ImplantCatalog catalog) {
        this.catalog = catalog;
    }

    public BuildResult operate(String patientName, String lifepathId, List<String> implantIds) {
        Lifepath lifepath = catalog.findLifepath(lifepathId)
                .orElseThrow(() -> new IllegalArgumentException("Unknown lifepath: " + lifepathId));

        Human patient = lifepath.create(cleanName(patientName));
        List<Layer> layers = new ArrayList<>();
        layers.add(Layer.of(lifepath.id(), lifepath.name(), lifepath.className(), patient));

        Map<BodySlot, Implant> occupiedSlots = new EnumMap<>(BodySlot.class);
        for (String implantId : implantIds) {
            Implant implant = catalog.findImplant(implantId)
                    .orElseThrow(() -> new IllegalArgumentException("Unknown implant: " + implantId));
            Implant current = occupiedSlots.putIfAbsent(implant.slot(), implant);
            if (current != null) {
                throw new IllegalArgumentException(
                        implant.slot().getLabel() + " slot is already taken by " + current.name());
            }
            patient = implant.installOn(patient);
            layers.add(Layer.of(implant.id(), implant.name(), implant.className(), patient));
        }

        return new BuildResult(patient, Condition.from(patient.getHumanity()), layers);
    }

    private String cleanName(String name) {
        if (name == null || name.isBlank()) {
            return DEFAULT_NAME;
        }
        String trimmed = name.trim();
        return trimmed.length() > MAX_NAME_LENGTH ? trimmed.substring(0, MAX_NAME_LENGTH) : trimmed;
    }
}
