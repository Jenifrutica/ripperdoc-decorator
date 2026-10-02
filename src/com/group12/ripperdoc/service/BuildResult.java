package com.group12.ripperdoc.service;

import com.group12.ripperdoc.model.Condition;
import com.group12.ripperdoc.model.Human;
import java.util.List;

public record BuildResult(Human patient, Condition condition, List<Layer> layers) {
}
