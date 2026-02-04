package com.blueharbor.hotel.model.addon;

import java.math.BigDecimal;

public interface PricedComponent {

    BigDecimal price();

    String description();
}
