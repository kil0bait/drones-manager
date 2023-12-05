package com.musala.artemis.dronemanager.model;


public enum ShipmentState {
    NEW(100, "Ready to load"),
    LOADING(200, "Package loading"),
    LOADED(300, "Loaded and ready for deliver"),
    DELIVERING(400, "Delivering"),
    CANCELLED_RETURNING(500, "Shipment is cancelled, returning"),
    DELIVERED(600, "Package has been delivered"),
    CANCELLED(700, "Shipment is cancelled");

    public final int order;
    public final String desc;

    ShipmentState(int order, String desc) {
        this.order = order;
        this.desc = desc;
    }
}
