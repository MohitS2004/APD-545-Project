package com.blueharbor.hotel.model.addon;

import java.math.BigDecimal;

public class GenericAddOnDecorator extends AddOnDecorator {

    private final String label;
    private final BigDecimal cost;

    public GenericAddOnDecorator(PricedComponent delegate, String label, BigDecimal cost) {
        super(delegate);
        this.label = label;
        this.cost = cost;
    }

    @Override
    protected BigDecimal additionalCost() {
        return cost;
    }

    @Override
    protected String label() {
        return label;
    }
}
