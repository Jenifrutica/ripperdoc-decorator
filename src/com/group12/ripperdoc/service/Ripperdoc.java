package com.group12.ripperdoc.service;

import com.group12.ripperdoc.builder.PatientBuilder;
import java.util.List;

public class Ripperdoc {

    private final ImplantCatalog catalog;

    public Ripperdoc(ImplantCatalog catalog) {
        this.catalog = catalog;
    }

    public BuildResult operate(String patientName, String lifepathId, List<String> implantIds) {
        PatientBuilder builder = new PatientBuilder(catalog)
                .named(patientName)
                .withLifepath(lifepathId);
        for (String implantId : implantIds) {
            builder.install(implantId);
        }
        return builder.build();
    }
}
