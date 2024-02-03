package com.amazon.ata.types;

import java.math.BigDecimal;

public class PolyBag extends Packaging {

    private BigDecimal volume;

    public PolyBag(Material material, BigDecimal volume) {
        super(material);
        this.volume = volume;
    }

    public BigDecimal getVolume() {
        return volume;
    }

    /**
     * Returns whether the given item will fit in this packaging.
     *
     * @param item the item to test fit for
     * @return whether the item will fit in this packaging
     */
    @Override
    public boolean canFitItem(Item item) {
        BigDecimal itemVolume = item.getLength().multiply(item.getWidth()).multiply(item.getHeight());

        return this.volume.compareTo(itemVolume) > 0;
    }

    /**
     * Returns the mass of the packaging in grams. The packaging weighs 1 gram per square centimeter.
     * @return the mass of the packaging
     */
    @Override
    public BigDecimal getMass() {
        double mass = Math.ceil(Math.sqrt(volume.doubleValue()));

        return BigDecimal.valueOf(mass);
    }
}
