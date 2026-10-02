package com.group12.ripperdoc.factory;

import com.group12.ripperdoc.service.Implant;

public interface CyberwareFactory {

    String id();

    String label();

    String description();

    Implant operatingSystem();

    Implant arms();

    Implant optics();
}
