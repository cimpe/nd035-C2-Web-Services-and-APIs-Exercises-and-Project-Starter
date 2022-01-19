package com.udacity.pricing;

import com.udacity.pricing.domain.price.Price;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.emptyCollectionOf;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.notNullValue;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
		properties = {"eureka.client.enabled: false"})
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD) //https://stackoverflow.com/a/37246354
public class PricingServiceApplicationTests {

	@LocalServerPort
	private int port;

	@Autowired
	private TestRestTemplate testRestTemplate;

	@Test
	public void contextLoads() {
	}

	@Test
	public void getAllPrices() {
		final String url = String.format("http://localhost:%s/prices", port);

		final ResponseEntity<PricesJson> response = testRestTemplate.getForEntity(url, PricesJson.class);
		assertThat(response.getStatusCode(), equalTo(HttpStatus.OK));
		assertThat(response.getBody().getEmbedded().getPrices(), emptyCollectionOf(Price.class));
	}

	@Test
	public void getPriceIfNotExists() {
		final String url = String.format("http://localhost:%s/prices/1", port);

		final ResponseEntity<Price> response = testRestTemplate.getForEntity(url, Price.class);
		assertThat(response.getStatusCode(), equalTo(HttpStatus.NOT_FOUND));
	}

	@Test
	public void getPriceIfExists() {
		final Price price = createRandomPrice();

		final ResponseEntity<Price> postResponse = postPrice(price);

		final String url = String.format("http://localhost:%s/prices/%s", port, postResponse.getBody().getId());

		final ResponseEntity<Price> response = testRestTemplate.getForEntity(url, Price.class);
		assertThat(response.getStatusCode(), equalTo(HttpStatus.OK));

		final Price body = response.getBody();
		assertThat(body.getId(), notNullValue(Long.class));
		assertThat(body.getCurrency(), equalTo(price.getCurrency()));
		assertThat(body.getPrice().longValue(), equalTo(price.getPrice().longValue()));
		assertThat(body.getVehicleId(), equalTo(price.getVehicleId()));
	}

	@Test
	public void postPrice() {
		final Price price = createRandomPrice();

		final ResponseEntity<Price> response = postPrice(price);
		assertThat(response.getStatusCode(), equalTo(HttpStatus.CREATED));

		final Price body = response.getBody();
		assertThat(body.getId(), notNullValue(Long.class));
		assertThat(body.getCurrency(), equalTo(price.getCurrency()));
		assertThat(body.getPrice().longValue(), equalTo(price.getPrice().longValue()));
		assertThat(body.getVehicleId(), equalTo(price.getVehicleId()));
	}

	@Test
	public void postPricesAndGetAll() {
		final Price price1 = createRandomPrice();
		final Long price1Id = postPrice(price1).getBody().getId();

		final Price price2 = createRandomPrice();
		final Long price2Id = postPrice(price2).getBody().getId();

		final String url = String.format("http://localhost:%s/prices", port);

		final ResponseEntity<PricesJson> response = testRestTemplate.getForEntity(url, PricesJson.class);
		assertThat(response.getStatusCode(), equalTo(HttpStatus.OK));

		final List<Price> prices = response.getBody().getEmbedded().getPrices();
		assertThat(prices, hasSize(equalTo(2)));

		final Map<Long, Price> mapPrices = prices.stream().collect(Collectors.toMap(Price::getId, p -> p));

		final Price actualPrice1 = mapPrices.get(price1Id);
		assertThat(actualPrice1.getId(), notNullValue(Long.class));
		assertThat(actualPrice1.getCurrency(), equalTo(price1.getCurrency()));
		assertThat(actualPrice1.getPrice().longValue(), equalTo(price1.getPrice().longValue()));
		assertThat(actualPrice1.getVehicleId(), equalTo(price1.getVehicleId()));

		final Price actualPrice2 = mapPrices.get(price2Id);
		assertThat(actualPrice2.getId(), notNullValue(Long.class));
		assertThat(actualPrice2.getCurrency(), equalTo(price2.getCurrency()));
		assertThat(actualPrice2.getPrice().longValue(), equalTo(price2.getPrice().longValue()));
		assertThat(actualPrice2.getVehicleId(), equalTo(price2.getVehicleId()));
	}

	private Price createRandomPrice() {
		final Price price = new Price();
		price.setCurrency("USD");
		price.setPrice(BigDecimal.valueOf(20000));
		price.setVehicleId(1L);

		return price;
	}

	private ResponseEntity<Price> postPrice(final Price price) {
		final String url = String.format("http://localhost:%s/prices", port);

		return testRestTemplate.postForEntity(url, price, Price.class);
	}

}
