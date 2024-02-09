package com.amazon.ata.cost;

import com.amazon.ata.types.ShipmentCost;
import com.amazon.ata.types.ShipmentOption;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

public class WeightedCostStrategy implements CostStrategy {

    private Map<CostStrategy, BigDecimal> weightedStrategiesMap;

    private WeightedCostStrategy(Map<CostStrategy, BigDecimal> weightedStrategiesMap) {
        this.weightedStrategiesMap = weightedStrategiesMap;
    }

    // Builder static inner class
    public static class Builder {
        private Map<CostStrategy, BigDecimal> weightedStrategiesMap = new HashMap<>();

        public Builder addStrategyWithWeight(CostStrategy costStrategy, BigDecimal weight) {
            weightedStrategiesMap.put(costStrategy, weight);
            return this;
        }

        public WeightedCostStrategy build() {
            return new WeightedCostStrategy(weightedStrategiesMap);
        }
    }

    @Override
    public ShipmentCost getCost(ShipmentOption shipmentOption) {

        BigDecimal cost = BigDecimal.ZERO;
        for (Map.Entry<CostStrategy, BigDecimal> anEntry : weightedStrategiesMap.entrySet()) {
            ShipmentCost entryCost = anEntry.getKey().getCost(shipmentOption);
            cost.add(entryCost.getCost().multiply(anEntry.getValue()));
        }

        return new ShipmentCost(shipmentOption, cost);
    }
}
