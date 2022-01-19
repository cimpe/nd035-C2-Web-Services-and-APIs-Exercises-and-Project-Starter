package com.udacity.pricing;

import com.udacity.pricing.domain.price.Price;

import java.util.List;

// https://knowledge.udacity.com/questions/307219
class PriceList {

    private List<Price> prices;

    public List<Price> getPrices() {
        return prices;
    }

    public void setPrices(final List<Price> prices) {
        this.prices = prices;
    }

}
