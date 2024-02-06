package com.amazon.ata.dao;

import com.amazon.ata.datastore.PackagingDatastore;
import com.amazon.ata.exceptions.NoPackagingFitsItemException;
import com.amazon.ata.exceptions.UnknownFulfillmentCenterException;
import com.amazon.ata.types.FcPackagingOption;
import com.amazon.ata.types.FulfillmentCenter;
import com.amazon.ata.types.Item;
import com.amazon.ata.types.Packaging;
import com.amazon.ata.types.ShipmentOption;

import java.util.*;

/**
 * Access data for which packaging is available at which fulfillment center.
 */
public class PackagingDAO {
    /**
     * A list of fulfillment centers with a packaging options they provide.
     */
    //private List<FcPackagingOption> fcPackagingOptions;
    private Map<FulfillmentCenter, Set<FcPackagingOption>> fcPackagingOptionsMap;

    /**
     * Instantiates a PackagingDAO object.
     * @param datastore Where to pull the data from for fulfillment center/packaging available mappings.
     */
    public PackagingDAO(PackagingDatastore datastore) {
        //this.fcPackagingOptions =  new ArrayList<>(datastore.getFcPackagingOptions());

        fcPackagingOptionsMap = new HashMap<>();
        for (FcPackagingOption f : datastore.getFcPackagingOptions()) {
            // What if key doesn't exist - put() using new key and newly created set.
            if (!fcPackagingOptionsMap.containsKey(f.getFulfillmentCenter())) {
                Set<FcPackagingOption> fcSet = new HashSet<>();
                fcSet.add(f);
                fcPackagingOptionsMap.put(f.getFulfillmentCenter(), fcSet);
            } else { // What if key exists already - retrieve FulfillmentCenter's set, and add the new FcPackagingOption
                Set<FcPackagingOption> fcSet = fcPackagingOptionsMap.get(f.getFulfillmentCenter());
                fcSet.add(f);
            }
        }

    }

    /**
     * Returns the packaging options available for a given item at the specified fulfillment center. The API
     * used to call this method handles null inputs, so we don't have to.
     *
     * @param item the item to pack
     * @param fulfillmentCenter fulfillment center to fulfill the order from
     * @return the shipping options available for that item; this can never be empty, because if there is no
     * acceptable option an exception will be thrown
     * @throws UnknownFulfillmentCenterException if the fulfillmentCenter is not in the fcPackagingOptions list
     * @throws NoPackagingFitsItemException if the item doesn't fit in any packaging at the FC
     */
    public List<ShipmentOption> findShipmentOptions(Item item, FulfillmentCenter fulfillmentCenter)
            throws UnknownFulfillmentCenterException, NoPackagingFitsItemException {

        // Notify caller about unexpected results
        if (fcPackagingOptionsMap.get(fulfillmentCenter) == null) {
            throw new UnknownFulfillmentCenterException(
                    String.format("Unknown FC: %s!", fulfillmentCenter.getFcCode()));
        }
        // Check all FcPackagingOptions for a suitable Packaging in the given FulfillmentCenter
        List<ShipmentOption> result = new ArrayList<>();
        boolean fcFound = false;

        for (FcPackagingOption fcPackagingOption : fcPackagingOptionsMap.get(fulfillmentCenter)) {
            Packaging packaging = fcPackagingOption.getPackaging();
            String fcCode = fcPackagingOption.getFulfillmentCenter().getFcCode();

            if (fcCode.equals(fulfillmentCenter.getFcCode())) {
                fcFound = true;
                if (packaging.canFitItem(item)) {
                    result.add(ShipmentOption.builder()
                            .withItem(item)
                            .withPackaging(packaging)
                            .withFulfillmentCenter(fulfillmentCenter)
                            .build());
                }
            }
        }

        // Notify caller about unexpected results
//        if (!fcFound) {
//            throw new UnknownFulfillmentCenterException(
//                    String.format("Unknown FC: %s!", fulfillmentCenter.getFcCode()));
//        }

        if (result.isEmpty()) {
            throw new NoPackagingFitsItemException(
                    String.format("No packaging at %s fits %s!", fulfillmentCenter.getFcCode(), item));
        }

        return result;
    }
}
