package com.group12.ripperdoc.api;

import com.group12.ripperdoc.model.Condition;
import com.group12.ripperdoc.model.Human;
import com.group12.ripperdoc.service.BuildResult;
import com.group12.ripperdoc.service.Implant;
import com.group12.ripperdoc.service.ImplantCatalog;
import com.group12.ripperdoc.service.Layer;
import com.group12.ripperdoc.service.Lifepath;
import com.group12.ripperdoc.service.Ripperdoc;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class ApiRouter {

    private static final int MAX_IMPLANTS = 12;

    private final ImplantCatalog catalog;
    private final Ripperdoc ripperdoc;

    public ApiRouter() {
        this.catalog = new ImplantCatalog();
        this.ripperdoc = new Ripperdoc(catalog);
    }

    public ApiResponse route(String method, String path, Map<String, String> query) {
        if (method == null || !method.equalsIgnoreCase("GET")) {
            return ApiResponse.error(405, "Method not allowed");
        }
        return switch (path) {
            case "/api/lifepaths" -> ApiResponse.json(200, lifepaths());
            case "/api/implants" -> ApiResponse.json(200, implants());
            case "/api/build" -> build(query);
            default -> ApiResponse.error(404, "Not found: " + path);
        };
    }

    public ImplantCatalog getCatalog() {
        return catalog;
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

    private ApiResponse build(Map<String, String> query) {
        try {
            String lifepath = query.get("lifepath");
            if (lifepath == null || lifepath.isBlank()) {
                return ApiResponse.error(400, "Missing required parameter 'lifepath'");
            }
            String name = query.get("name");
            List<String> implants = new ArrayList<>();
            String raw = query.get("implants");
            if (raw != null && !raw.isBlank()) {
                for (String part : raw.split(",")) {
                    String trimmed = part.trim();
                    if (!trimmed.isEmpty()) {
                        implants.add(trimmed);
                    }
                }
            }
            if (implants.size() > MAX_IMPLANTS) {
                return ApiResponse.error(400, "Too many implants requested");
            }
            BuildResult result = ripperdoc.operate(name, lifepath, implants);
            return ApiResponse.json(200, buildJson(result));
        } catch (IllegalArgumentException error) {
            return ApiResponse.error(400, error.getMessage());
        }
    }

    private static String buildJson(BuildResult result) {
        Human patient = result.patient();
        Map<String, Object> root = new LinkedHashMap<>();
        root.put("description", patient.getDescription());
        root.put("stats", stats(patient));
        root.put("condition", result.condition().name());
        Map<String, Object> thresholds = new LinkedHashMap<>();
        thresholds.put("stable", Condition.STABLE_THRESHOLD);
        thresholds.put("psychosis", Condition.PSYCHOSIS_THRESHOLD);
        root.put("thresholds", thresholds);
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
        return Json.write(root);
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
