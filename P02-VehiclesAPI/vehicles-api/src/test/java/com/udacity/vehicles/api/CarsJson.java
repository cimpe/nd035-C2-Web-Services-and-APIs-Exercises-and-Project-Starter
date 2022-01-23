package com.udacity.vehicles.api;

import com.fasterxml.jackson.annotation.JsonProperty;

class CarsJson {

    @JsonProperty("_embedded")
    private CarList embedded;

    CarList getEmbedded() {
        return embedded;
    }

    void setEmbedded(final CarList embedded) {
        this.embedded = embedded;
    }

}
