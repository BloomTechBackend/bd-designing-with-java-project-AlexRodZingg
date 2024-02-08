package com.amazon.ata.cost;

import com.amazon.ata.types.Material;
import com.amazon.ata.types.Packaging;
import com.amazon.ata.types.ShipmentCost;
import com.amazon.ata.types.ShipmentOption;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

public class CarbonCostStrategy implements CostStrategy {

    private final Map<Material, BigDecimal> materialCarbonCostPerGram;

    public CarbonCostStrategy() {
        materialCarbonCostPerGram = new HashMap<>();
        materialCarbonCostPerGram.put(Material.CORRUGATE, BigDecimal.valueOf(.017));
        materialCarbonCostPerGram.put(Material.LAMINATED_PLASTIC, BigDecimal.valueOf(.012));
    }

    @Override
    public ShipmentCost getCost(ShipmentOption shipmentOption) {
        Packaging packaging = shipmentOption.getPackaging();
        BigDecimal materialCost = materialCarbonCostPerGram.get(packaging.getMaterial());
        BigDecimal totalCarbonCost = materialCost.multiply(packaging.getMass());

        return new ShipmentCost(shipmentOption, totalCarbonCost);
    }
}
