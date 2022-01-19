package com.udacity.pricing;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
class PricesJson {

    @JsonProperty("_embedded")
    private PriceList embedded;

}
