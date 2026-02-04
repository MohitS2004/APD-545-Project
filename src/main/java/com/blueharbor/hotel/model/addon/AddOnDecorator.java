package com.blueharbor.hotel.model.addon;

import java.math.BigDecimal;

public abstract class AddOnDecorator implements PricedComponent {

    protected final PricedComponent delegate;

    protected AddOnDecorator(PricedComponent delegate) {
        this.delegate = delegate;
    }

    @Override
    public BigDecimal price() {
        return delegate.price().add(additionalCost());
    }

    @Override
    public String description() {
        return delegate.description() + " + " + label();
    }

    protected abstract BigDecimal additionalCost();

    protected abstract String label();
}
