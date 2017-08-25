package com.team.noty.gzavnili.adapters;

/**
 * Created by copch on 24.08.2017.
 */

public class ParcelPaymentData {

    private String trackingNumber, store, weight, deliveryPrice;

    public ParcelPaymentData(String trackingNumber, String store, String weight, String deliveryPrice){

        this.trackingNumber = trackingNumber;
        this.store = store;
        this.weight = weight;
        this.deliveryPrice = deliveryPrice;
    }

    public String getTrackingNumber() {
        return trackingNumber;
    }

    public String getStore() {
        return store;
    }

    public String getWeight() {
        return weight;
    }

    public String getDeliveryPrice() {
        return deliveryPrice;
    }
}

