package com.amazon.ata;

import com.amazon.ata.cost.CarbonCostStrategy;
import com.amazon.ata.cost.CostStrategy;
import com.amazon.ata.cost.MonetaryCostStrategy;
import com.amazon.ata.cost.WeightedCostStrategy;
import com.amazon.ata.dao.PackagingDAO;
import com.amazon.ata.datastore.PackagingDatastore;
import com.amazon.ata.service.ShipmentService;

import java.math.BigDecimal;

public class App {
    /* don't instantiate me */
    private App() {}

    private static PackagingDatastore getPackagingDatastore() {
        return new PackagingDatastore();
    }

    private static PackagingDAO getPackagingDAO() {
        return new PackagingDAO(getPackagingDatastore());
    }

    private static CostStrategy getCostStrategy() {
        return new WeightedCostStrategy.Builder()
                .addStrategyWithWeight(new MonetaryCostStrategy(), BigDecimal.valueOf(.8))
                .addStrategyWithWeight(new CarbonCostStrategy(), BigDecimal.valueOf(.2))
                .build();
    }

    public static ShipmentService getShipmentService() {
        return new ShipmentService(getPackagingDAO(), getCostStrategy());
    }
}
