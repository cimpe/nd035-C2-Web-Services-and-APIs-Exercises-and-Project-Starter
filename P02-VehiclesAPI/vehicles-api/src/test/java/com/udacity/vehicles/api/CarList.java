package com.udacity.vehicles.api;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.udacity.vehicles.domain.car.Car;

import java.util.List;

class CarList {

    @JsonProperty("carList")
    private List<Car> cars;

    List<Car> getCars() {
        return cars;
    }

    void setCars(final List<Car> cars) {
        this.cars = cars;
    }

}
