package com.udacity.vehicles.service;

import com.udacity.vehicles.client.maps.MapsClient;
import com.udacity.vehicles.client.prices.PriceClient;
import com.udacity.vehicles.domain.car.Car;
import com.udacity.vehicles.domain.car.CarRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(SpringRunner.class)
public class CarServiceTest {

    @MockBean
    private CarRepository carRepository;

    @MockBean
    private PriceClient priceClient;

    @MockBean
    private MapsClient mapsClient;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void zeroSizedListCars() {
        list(0);
    }

    @Test
    public void oneSizedListCars() {
        list(1);
    }

    @Test
    public void fiveSizedListCars() {
        list(5);
    }

    @Test
    public void tenSizedListCars() {
        list(10);
    }

    private void list(final int size) {
        final List<Car> cars = IntStream.range(0, size).mapToObj(this::createCar).collect(Collectors.toList());
        given(carRepository.findAll()).willReturn(cars);

        final CarService carService = new CarService(carRepository, priceClient, mapsClient);
        carService.list();

        verify(carRepository, times(1)).findAll();
        verify(priceClient, times(size)).getPrice(anyLong());
        verify(mapsClient, times(size)).getAddress(any());
    }

    @Test
    public void findById() {
        final long id = 1L;

        final Car car = createCar(id);
        given(carRepository.findById(anyLong())).willReturn(Optional.of(car));

        final CarService carService = new CarService(carRepository, priceClient, mapsClient);
        assertEquals(carService.findById(id).getId(), car.getId());

        verify(priceClient, times(1)).getPrice(anyLong());
        verify(mapsClient, times(1)).getAddress(any());
    }

    @Test(expected = CarNotFoundException.class)
    public void findByIdShouldThrowsCarNotFoundExceptionIfCarNotExists() {
        given(carRepository.findById(anyLong())).willReturn(Optional.empty());

        final CarService carService = new CarService(carRepository, priceClient, mapsClient);
        carService.findById(1L);
    }

    private Car createCar(final long id) {
        final Car car = new Car();
        car.setId(id);

        return car;
    }

}
