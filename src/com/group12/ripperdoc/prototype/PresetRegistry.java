package com.group12.ripperdoc.prototype;

import com.group12.ripperdoc.decorator.CyberdeckQuickhack;
import com.group12.ripperdoc.decorator.GorillaArms;
import com.group12.ripperdoc.decorator.KerenzikovReflex;
import com.group12.ripperdoc.decorator.KiroshiOptics;
import com.group12.ripperdoc.decorator.MantisBlades;
import com.group12.ripperdoc.decorator.OpticalCamo;
import com.group12.ripperdoc.decorator.Sandevistan;
import com.group12.ripperdoc.decorator.SubdermalArmor;
import com.group12.ripperdoc.model.Corpo;
import com.group12.ripperdoc.model.Human;
import com.group12.ripperdoc.model.Nomad;
import com.group12.ripperdoc.model.StreetKid;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class PresetRegistry {

    public record Preset(
            String id,
            String name,
            String description,
            String chain,
            String lifepathId,
            List<String> implantIds) {
    }

    private final Map<String, Human> prototypes = new LinkedHashMap<>();
    private final Map<String, Preset> metadata = new LinkedHashMap<>();

    public PresetRegistry() {
        register("street-samurai", "Street Samurai", "Mantis blades, boosted nerves and bone plating.",
                "street-kid", List.of("mantis-blades", "kerenzikov", "subdermal-armor"),
                new SubdermalArmor(new KerenzikovReflex(new MantisBlades(new StreetKid("Samurai")))));
        register("netrunner", "Netrunner", "A corpo deck and chrome optics for deep dives.",
                "corpo", List.of("cyberdeck", "kiroshi-optics"),
                new KiroshiOptics(new CyberdeckQuickhack(new Corpo("Runner"))));
        register("solo", "Solo", "Gorilla arms and a Sandevistan for front-line work.",
                "nomad", List.of("gorilla-arms", "sandevistan", "subdermal-armor"),
                new SubdermalArmor(new Sandevistan(new GorillaArms(new Nomad("Solo")))));
        register("ghost", "Ghost", "Optical camo and a Kerenzikov. You never saw them.",
                "street-kid", List.of("optical-camo", "kiroshi-optics", "kerenzikov"),
                new KerenzikovReflex(new KiroshiOptics(new OpticalCamo(new StreetKid("Ghost")))));
    }

    private void register(String id, String name, String description, String lifepathId,
                          List<String> implantIds, Human prototype) {
        prototypes.put(id, prototype);
        metadata.put(id, new Preset(id, name, description, prototype.getDescription(), lifepathId, implantIds));
    }

    public List<Preset> list() {
        return new ArrayList<>(metadata.values());
    }

    public Optional<Human> clone(String id) {
        return Optional.ofNullable(prototypes.get(id)).map(Human::copy);
    }
}
