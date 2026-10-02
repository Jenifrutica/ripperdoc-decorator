package com.group12.ripperdoc.service;

import com.group12.ripperdoc.decorator.CyberdeckQuickhack;
import com.group12.ripperdoc.decorator.GorillaArms;
import com.group12.ripperdoc.decorator.KerenzikovReflex;
import com.group12.ripperdoc.decorator.KiroshiOptics;
import com.group12.ripperdoc.decorator.MantisBlades;
import com.group12.ripperdoc.decorator.OpticalCamo;
import com.group12.ripperdoc.decorator.Sandevistan;
import com.group12.ripperdoc.decorator.SubdermalArmor;
import com.group12.ripperdoc.model.Corpo;
import com.group12.ripperdoc.model.Nomad;
import com.group12.ripperdoc.model.StreetKid;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class ImplantCatalog {

    private final Map<String, Lifepath> lifepaths = new LinkedHashMap<>();
    private final Map<String, Implant> implants = new LinkedHashMap<>();

    public ImplantCatalog() {
        addLifepath(new Lifepath("street-kid", "Street Kid", StreetKid.class.getSimpleName(),
                "Raised in the alleys. Quick hands, quicker exits.", StreetKid::new));
        addLifepath(new Lifepath("nomad", "Nomad", Nomad.class.getSimpleName(),
                "Grew up on the open road. Tough, grounded, fully human.", Nomad::new));
        addLifepath(new Lifepath("corpo", "Corpo", Corpo.class.getSimpleName(),
                "Former tower employee. Sharp mind, already a little worn down.", Corpo::new));

        addImplant(new Implant("sandevistan", Sandevistan.NAME, Sandevistan.class.getSimpleName(),
                BodySlot.OPERATING_SYSTEM, Sandevistan.PRICE, "x1.5 REF, -25 HUM",
                "Slows the world down. Multiplies the reflexes it wraps.", Sandevistan::new));
        addImplant(new Implant("cyberdeck", CyberdeckQuickhack.NAME, CyberdeckQuickhack.class.getSimpleName(),
                BodySlot.OPERATING_SYSTEM, CyberdeckQuickhack.PRICE, "x1.4 HACK, -15 HUM",
                "Runs quickhacks. Multiplies the hacking it wraps.", CyberdeckQuickhack::new));
        addImplant(new Implant("kiroshi-optics", KiroshiOptics.NAME, KiroshiOptics.class.getSimpleName(),
                BodySlot.FACE, KiroshiOptics.PRICE, "+5 REF, +10 HACK, -6 HUM",
                "Zoom, scan and tag targets through chrome eyes.", KiroshiOptics::new));
        addImplant(new Implant("kerenzikov", KerenzikovReflex.NAME, KerenzikovReflex.class.getSimpleName(),
                BodySlot.NERVOUS_SYSTEM, KerenzikovReflex.PRICE, "+15 REF, -10 HUM",
                "Boosted nerve paths for split second dodges.", KerenzikovReflex::new));
        addImplant(new Implant("mantis-blades", MantisBlades.NAME, MantisBlades.class.getSimpleName(),
                BodySlot.ARMS, MantisBlades.PRICE, "+20 STR, +10 REF, -18 HUM",
                "Retractable blades folded inside the forearms.", MantisBlades::new));
        addImplant(new Implant("gorilla-arms", GorillaArms.NAME, GorillaArms.class.getSimpleName(),
                BodySlot.ARMS, GorillaArms.PRICE, "+35 STR, +5 ARM, -14 HUM",
                "Hydraulic fists that open doors nobody locked for you.", GorillaArms::new));
        addImplant(new Implant("subdermal-armor", SubdermalArmor.NAME, SubdermalArmor.class.getSimpleName(),
                BodySlot.SKELETON, SubdermalArmor.PRICE, "+30 ARM, -5 REF, -12 HUM",
                "Plating bonded to the bones. Heavy, but bullets bounce.", SubdermalArmor::new));
        addImplant(new Implant("optical-camo", OpticalCamo.NAME, OpticalCamo.class.getSimpleName(),
                BodySlot.INTEGUMENTARY_SYSTEM, OpticalCamo.PRICE, "+10 ARM, +5 HACK, -12 HUM",
                "Skin that bends light. Now you see me.", OpticalCamo::new));
    }

    private void addLifepath(Lifepath lifepath) {
        lifepaths.put(lifepath.id(), lifepath);
    }

    private void addImplant(Implant implant) {
        implants.put(implant.id(), implant);
    }

    public List<Lifepath> getLifepaths() {
        return new ArrayList<>(lifepaths.values());
    }

    public List<Implant> getImplants() {
        return new ArrayList<>(implants.values());
    }

    public Optional<Lifepath> findLifepath(String id) {
        return Optional.ofNullable(lifepaths.get(id));
    }

    public Optional<Implant> findImplant(String id) {
        return Optional.ofNullable(implants.get(id));
    }
}
