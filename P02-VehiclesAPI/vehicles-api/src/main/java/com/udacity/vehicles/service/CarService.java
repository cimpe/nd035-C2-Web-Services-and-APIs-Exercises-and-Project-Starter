package com.udacity.vehicles.service;

import com.udacity.vehicles.client.maps.MapsClient;
import com.udacity.vehicles.client.prices.PriceClient;
import com.udacity.vehicles.domain.Location;
import com.udacity.vehicles.domain.car.Car;
import com.udacity.vehicles.domain.car.CarRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Implements the car service create, read, update or delete
 * information about vehicles, as well as gather related
 * location and price data when desired.
 */
@Service
public class CarService {

    private final CarRepository repository;

    private final PriceClient priceClient;

    private final MapsClient mapsClient;

    public CarService(final CarRepository repository, final PriceClient priceClient, final MapsClient mapsClient) {
        this.repository = repository;
        this.priceClient = priceClient;
        this.mapsClient = mapsClient;
    }

    /**
     * Gathers a list of all vehicles
     * @return a list of all vehicles in the CarRepository
     */
    public List<Car> list() {
        return repository.findAll().stream().map(this::fillPriceAndLocation).collect(Collectors.toList());
    }

    /**
     * Gets car information by ID (or throws exception if non-existent)
     * @param id the ID number of the car to gather information on
     * @return the requested car's information, including location and price
     */
    public Car findById(Long id) {
        final Car car = repository.findById(id).orElseThrow(() -> new CarNotFoundException());

        fillPriceAndLocation(car);

        return car;
    }

    private Car fillPriceAndLocation(final Car car) {
        fillPrice(car);
        fillLocation(car);

        return car;
    }

    private void fillPrice(final Car car) {
        final String price = priceClient.getPrice(car.getId());
        car.setPrice(price);
    }

    private void fillLocation(final Car car) {
        final Location location = mapsClient.getAddress(car.getLocation());
        car.setLocation(location);
    }

    /**
     * Either creates or updates a vehicle, based on prior existence of car
     * @param car A car object, which can be either new or existing
     * @return the new/updated car is stored in the repository
     */
    public Car save(Car car) {
        if (car.getId() != null) {
            return repository.findById(car.getId())
                    .map(carToBeUpdated -> {
                        carToBeUpdated.setCondition(car.getCondition());
                        carToBeUpdated.setDetails(car.getDetails());
                        carToBeUpdated.setLocation(car.getLocation());

                        return repository.save(carToBeUpdated);
                    }).orElseThrow(CarNotFoundException::new);
        }

        return repository.save(car);
    }

    /**
     * Deletes a given car by ID
     * @param id the ID number of the car to delete
     */
    public void delete(Long id) {
        final Car car = repository.findById(id).orElseThrow(() -> new CarNotFoundException());

        repository.delete(car);
    }
}
