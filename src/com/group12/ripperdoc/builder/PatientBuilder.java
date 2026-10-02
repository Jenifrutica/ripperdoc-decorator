package com.group12.ripperdoc.builder;

import com.group12.ripperdoc.factory.CyberwareFactory;
import com.group12.ripperdoc.model.Condition;
import com.group12.ripperdoc.model.Human;
import com.group12.ripperdoc.service.BodySlot;
import com.group12.ripperdoc.service.BuildResult;
import com.group12.ripperdoc.service.Implant;
import com.group12.ripperdoc.service.ImplantCatalog;
import com.group12.ripperdoc.service.Layer;
import com.group12.ripperdoc.service.Lifepath;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

public class PatientBuilder {

    private static final String DEFAULT_NAME = "V";
    private static final int MAX_NAME_LENGTH = 24;

    private final ImplantCatalog catalog;
    private final List<Implant> implants = new ArrayList<>();
    private String name = DEFAULT_NAME;
    private String lifepathId = "nomad";

    public PatientBuilder(ImplantCatalog catalog) {
        this.catalog = catalog;
    }

    public PatientBuilder named(String patientName) {
        if (patientName != null) {
            this.name = patientName;
        }
        return this;
    }

    public PatientBuilder withLifepath(String lifepathId) {
        if (lifepathId != null) {
            this.lifepathId = lifepathId;
        }
        return this;
    }

    public PatientBuilder install(String implantId) {
        Implant implant = catalog.findImplant(implantId)
                .orElseThrow(() -> new IllegalArgumentException("Unknown implant: " + implantId));
        return install(implant);
    }

    public PatientBuilder install(Implant implant) {
        boolean alreadyPresent = implants.stream().anyMatch(existing -> existing.id().equals(implant.id()));
        if (!alreadyPresent) {
            implants.add(implant);
        }
        return this;
    }

    public PatientBuilder installKit(CyberwareFactory factory) {
        List<Implant> kit = List.of(factory.operatingSystem(), factory.arms(), factory.optics());
        implants.removeIf(existing -> kit.stream().anyMatch(product -> product.slot() == existing.slot()));
        kit.forEach(this::install);
        return this;
    }

    public List<String> implantIds() {
        return implants.stream().map(Implant::id).toList();
    }

    public BuildResult build() {
        Lifepath lifepath = catalog.findLifepath(lifepathId)
                .orElseThrow(() -> new IllegalArgumentException("Unknown lifepath: " + lifepathId));

        Human patient = lifepath.create(cleanName(name));
        List<Layer> layers = new ArrayList<>();
        layers.add(Layer.of(lifepath.id(), lifepath.name(), lifepath.className(), patient));

        Map<BodySlot, Implant> occupiedSlots = new EnumMap<>(BodySlot.class);
        for (Implant implant : implants) {
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

    private static String cleanName(String value) {
        if (value == null || value.isBlank()) {
            return DEFAULT_NAME;
        }
        String trimmed = value.trim();
        return trimmed.length() > MAX_NAME_LENGTH ? trimmed.substring(0, MAX_NAME_LENGTH) : trimmed;
    }
}
