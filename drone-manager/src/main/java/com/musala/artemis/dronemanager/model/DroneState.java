package com.musala.artemis.dronemanager.model;


public enum DroneState {
    IDLE(100, "Ready to load"),
    LOADING(200, "Package loading"),
    LOADED(300, "Loaded and ready for deliver"),
    DELIVERING(400, "Delivering a package"),
    DELIVERED(500, "Package has been delivered, ready to return"),
    RETURNING(600, "Coming back");

    public final int order;
    public final String desc;

    DroneState(int order, String desc) {
        this.order = order;
        this.desc = desc;
    }
}
