package com.group12.ripperdoc.service;

public enum BodySlot {

    OPERATING_SYSTEM("Operating System"),
    FACE("Face"),
    NERVOUS_SYSTEM("Nervous System"),
    ARMS("Arms"),
    SKELETON("Skeleton"),
    INTEGUMENTARY_SYSTEM("Integumentary System");

    private final String label;

    BodySlot(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
