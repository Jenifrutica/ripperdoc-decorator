package com.group12.ripperdoc.api;

import com.group12.ripperdoc.builder.PatientBuilder;
import com.group12.ripperdoc.factory.CyberwareFactory;
import com.group12.ripperdoc.factory.CyberwareFactoryRegistry;
import com.group12.ripperdoc.model.Condition;
import com.group12.ripperdoc.model.Human;
import com.group12.ripperdoc.prototype.PresetRegistry;
import com.group12.ripperdoc.service.BuildResult;
import com.group12.ripperdoc.service.Implant;
import com.group12.ripperdoc.service.ImplantCatalog;
import com.group12.ripperdoc.service.Layer;
import com.group12.ripperdoc.service.Lifepath;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class ApiRouter {

    private static final int MAX_IMPLANTS = 12;

    private final ImplantCatalog catalog = new ImplantCatalog();
    private final CyberwareFactoryRegistry factoryRegistry = new CyberwareFactoryRegistry();
    private final PresetRegistry presetRegistry = new PresetRegistry();

    public ApiResponse route(String method, String path, Map<String, String> query) {
        if (method == null || !method.equalsIgnoreCase("GET")) {
            return ApiResponse.error(405, "Method not allowed");
        }
        return switch (path) {
            case "/api/lifepaths" -> ApiResponse.json(200, lifepaths());
            case "/api/implants" -> ApiResponse.json(200, implants());
            case "/api/families" -> ApiResponse.json(200, families());
            case "/api/presets" -> ApiResponse.json(200, presets());
            case "/api/build" -> build(query);
            default -> ApiResponse.error(404, "Not found: " + path);
        };
    }

    public ImplantCatalog getCatalog() {
        return catalog;
    }

    public CyberwareFactoryRegistry getFactoryRegistry() {
        return factoryRegistry;
    }

    public PresetRegistry getPresetRegistry() {
        return presetRegistry;
    }

    private String lifepaths() {
        List<Object> list = new ArrayList<>();
        for (Lifepath lifepath : catalog.getLifepaths()) {
            Map<String, Object> map = new LinkedHashMap<>();
            map.put("id", lifepath.id());
            map.put("name", lifepath.name());
            map.put("className", lifepath.className());
            map.put("description", lifepath.description());
            map.put("stats", stats(lifepath.create("V")));
            list.add(map);
        }
        return Json.write(list);
    }

    private String implants() {
        List<Object> list = new ArrayList<>();
        for (Implant implant : catalog.getImplants()) {
            Map<String, Object> map = new LinkedHashMap<>();
            map.put("id", implant.id());
            map.put("name", implant.name());
            map.put("className", implant.className());
            map.put("slot", implant.slot().name());
            map.put("slotLabel", implant.slot().getLabel());
            map.put("price", implant.price());
            map.put("effect", implant.effect());
            map.put("description", implant.description());
            list.add(map);
        }
        return Json.write(list);
    }

    private String families() {
        List<Object> list = new ArrayList<>();
        for (CyberwareFactory factory : factoryRegistry.list()) {
            Map<String, Object> map = new LinkedHashMap<>();
            map.put("id", factory.id());
            map.put("label", factory.label());
            map.put("description", factory.description());
            map.put("products", List.of(
                    product(factory.operatingSystem()),
                    product(factory.arms()),
                    product(factory.optics())));
            list.add(map);
        }
        return Json.write(list);
    }

    private String presets() {
        List<Object> list = new ArrayList<>();
        for (PresetRegistry.Preset preset : presetRegistry.list()) {
            Map<String, Object> map = new LinkedHashMap<>();
            map.put("id", preset.id());
            map.put("name", preset.name());
            map.put("description", preset.description());
            map.put("chain", preset.descriptionChain());
            list.add(map);
        }
        return Json.write(list);
    }

    private ApiResponse build(Map<String, String> query) {
        try {
            String preset = query.get("preset");
            if (preset != null && !preset.isBlank()) {
                return presetBuild(preset);
            }

            String lifepath = query.get("lifepath");
            if (lifepath == null || lifepath.isBlank()) {
                return ApiResponse.error(400, "Missing required parameter 'lifepath'");
            }

            PatientBuilder builder = new PatientBuilder(catalog)
                    .named(query.get("name"))
                    .withLifepath(lifepath);

            String raw = query.get("implants");
            int count = 0;
            if (raw != null && !raw.isBlank()) {
                for (String part : raw.split(",")) {
                    String trimmed = part.trim();
                    if (!trimmed.isEmpty()) {
                        builder.install(trimmed);
                        count++;
                    }
                }
            }
            if (count > MAX_IMPLANTS) {
                return ApiResponse.error(400, "Too many implants requested");
            }

            String family = query.get("family");
            if (family != null && !family.isBlank()) {
                CyberwareFactory factory = factoryRegistry.find(family)
                        .orElseThrow(() -> new IllegalArgumentException("Unknown family: " + family));
                builder.installKit(factory);
            }

            return ApiResponse.json(200, buildJson(builder.build()));
        } catch (IllegalArgumentException error) {
            return ApiResponse.error(400, error.getMessage());
        }
    }

    private ApiResponse presetBuild(String presetId) {
        Human clone = presetRegistry.clone(presetId)
                .orElseThrow(() -> new IllegalArgumentException("Unknown preset: " + presetId));

        Map<String, Object> root = new LinkedHashMap<>();
        root.put("description", clone.getDescription());
        root.put("stats", stats(clone));
        root.put("condition", Condition.from(clone.getHumanity()).name());
        root.put("thresholds", thresholds());

        Map<String, Object> layer = new LinkedHashMap<>();
        layer.put("id", presetId);
        layer.put("name", presetId);
        layer.put("className", "Prototype");
        layer.put("description", clone.getDescription());
        layer.put("stats", stats(clone));
        root.put("layers", List.of(layer));
        root.put("source", "preset");
        return ApiResponse.json(200, Json.write(root));
    }

    private static Map<String, Object> product(Implant implant) {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("id", implant.id());
        map.put("name", implant.name());
        map.put("slot", implant.slot().name());
        map.put("slotLabel", implant.slot().getLabel());
        map.put("price", implant.price());
        map.put("effect", implant.effect());
        return map;
    }

    private static String buildJson(BuildResult result) {
        Human patient = result.patient();
        Map<String, Object> root = new LinkedHashMap<>();
        root.put("description", patient.getDescription());
        root.put("stats", stats(patient));
        root.put("condition", result.condition().name());
        root.put("thresholds", thresholds());
        List<Object> layers = new ArrayList<>();
        for (Layer layer : result.layers()) {
            Map<String, Object> map = new LinkedHashMap<>();
            map.put("id", layer.id());
            map.put("name", layer.name());
            map.put("className", layer.className());
            map.put("description", layer.description());
            map.put("stats", layerStats(layer));
            layers.add(map);
        }
        root.put("layers", layers);
        root.put("source", "build");
        return Json.write(root);
    }

    private static Map<String, Object> thresholds() {
        Map<String, Object> thresholds = new LinkedHashMap<>();
        thresholds.put("stable", Condition.STABLE_THRESHOLD);
        thresholds.put("psychosis", Condition.PSYCHOSIS_THRESHOLD);
        return thresholds;
    }

    private static Map<String, Object> stats(Human human) {
        Map<String, Object> stats = new LinkedHashMap<>();
        stats.put("strength", human.getStrength());
        stats.put("reflexes", human.getReflexes());
        stats.put("hacking", human.getHacking());
        stats.put("armor", human.getArmor());
        stats.put("humanity", human.getHumanity());
        stats.put("cost", human.getCost());
        return stats;
    }

    private static Map<String, Object> layerStats(Layer layer) {
        Map<String, Object> stats = new LinkedHashMap<>();
        stats.put("strength", layer.strength());
        stats.put("reflexes", layer.reflexes());
        stats.put("hacking", layer.hacking());
        stats.put("armor", layer.armor());
        stats.put("humanity", layer.humanity());
        stats.put("cost", layer.cost());
        return stats;
    }
}
