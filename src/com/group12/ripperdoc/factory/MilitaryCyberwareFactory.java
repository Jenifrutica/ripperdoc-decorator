package com.group12.ripperdoc.factory;

import com.group12.ripperdoc.decorator.Berserk;
import com.group12.ripperdoc.decorator.ProjectileArms;
import com.group12.ripperdoc.decorator.TargetingOptics;
import com.group12.ripperdoc.service.BodySlot;
import com.group12.ripperdoc.service.Implant;

public class MilitaryCyberwareFactory implements CyberwareFactory {

    @Override
    public String id() {
        return "military";
    }

    @Override
    public String label() {
        return "Military";
    }

    @Override
    public String description() {
        return "Militech-grade hardware. Heavy hitters with a heavy humanity price.";
    }

    @Override
    public Implant operatingSystem() {
        return new Implant("militech-berserk", Berserk.NAME, Berserk.class.getSimpleName(),
                BodySlot.OPERATING_SYSTEM, Berserk.PRICE, "+40 STR, -30 HUM",
                "Adrenal combat mode. Pushes the body past its red line.", Berserk::new);
    }

    @Override
    public Implant arms() {
        return new Implant("projectile-arms", ProjectileArms.NAME, ProjectileArms.class.getSimpleName(),
                BodySlot.ARMS, ProjectileArms.PRICE, "+25 STR, +15 ARM, -20 HUM",
                "Folding launchers built into the forearms.", ProjectileArms::new);
    }

    @Override
    public Implant optics() {
        return new Implant("targeting-optics", TargetingOptics.NAME, TargetingOptics.class.getSimpleName(),
                BodySlot.FACE, TargetingOptics.PRICE, "+15 REF, +15 HACK, -10 HUM",
                "Combat optics that paint a firing solution on every target.", TargetingOptics::new);
    }
}
