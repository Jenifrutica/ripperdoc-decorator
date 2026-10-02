package com.group12.ripperdoc.factory;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class CyberwareFactoryRegistry {

    private final Map<String, CyberwareFactory> factories = new LinkedHashMap<>();

    public CyberwareFactoryRegistry() {
        register(new CivilianCyberwareFactory());
        register(new MilitaryCyberwareFactory());
    }

    private void register(CyberwareFactory factory) {
        factories.put(factory.id(), factory);
    }

    public List<CyberwareFactory> list() {
        return new ArrayList<>(factories.values());
    }

    public Optional<CyberwareFactory> find(String id) {
        return Optional.ofNullable(factories.get(id));
    }
}
