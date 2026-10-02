package com.group12.ripperdoc.factory;

import com.group12.ripperdoc.decorator.GorillaArms;
import com.group12.ripperdoc.decorator.KiroshiOptics;
import com.group12.ripperdoc.decorator.Sandevistan;
import com.group12.ripperdoc.service.BodySlot;
import com.group12.ripperdoc.service.Implant;

public class CivilianCyberwareFactory implements CyberwareFactory {

    @Override
    public String id() {
        return "civilian";
    }

    @Override
    public String label() {
        return "Civilian";
    }

    @Override
    public String description() {
        return "Off-the-shelf chrome. Balanced, affordable, easy on humanity.";
    }

    @Override
    public Implant operatingSystem() {
        return new Implant("sandevistan", Sandevistan.NAME, Sandevistan.class.getSimpleName(),
                BodySlot.OPERATING_SYSTEM, Sandevistan.PRICE, "x1.5 REF, -25 HUM",
                "Slows the world down. Multiplies the reflexes it wraps.", Sandevistan::new);
    }

    @Override
    public Implant arms() {
        return new Implant("gorilla-arms", GorillaArms.NAME, GorillaArms.class.getSimpleName(),
                BodySlot.ARMS, GorillaArms.PRICE, "+35 STR, +5 ARM, -14 HUM",
                "Hydraulic fists that open doors nobody locked for you.", GorillaArms::new);
    }

    @Override
    public Implant optics() {
        return new Implant("kiroshi-optics", KiroshiOptics.NAME, KiroshiOptics.class.getSimpleName(),
                BodySlot.FACE, KiroshiOptics.PRICE, "+5 REF, +10 HACK, -6 HUM",
                "Zoom, scan and tag targets through chrome eyes.", KiroshiOptics::new);
    }
}
