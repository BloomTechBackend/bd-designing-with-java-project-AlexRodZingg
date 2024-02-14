package com.amazon.ata.service;

import com.amazon.ata.cost.CostStrategy;
import com.amazon.ata.cost.MonetaryCostStrategy;
import com.amazon.ata.dao.PackagingDAO;
import com.amazon.ata.datastore.PackagingDatastore;
import com.amazon.ata.exceptions.NoPackagingFitsItemException;
import com.amazon.ata.exceptions.UnknownFulfillmentCenterException;
import com.amazon.ata.types.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

class ShipmentServiceTest {

//    private Item smallItem = Item.builder()
//            .withHeight(BigDecimal.valueOf(1))
//            .withWidth(BigDecimal.valueOf(1))
//            .withLength(BigDecimal.valueOf(1))
//            .withAsin("abcde")
//            .build();
//
//    private Item largeItem = Item.builder()
//            .withHeight(BigDecimal.valueOf(1000))
//            .withWidth(BigDecimal.valueOf(1000))
//            .withLength(BigDecimal.valueOf(1000))
//            .withAsin("12345")
//            .build();

    private Item item;

//
//    private FulfillmentCenter existentFC = new FulfillmentCenter("ABE2");
//    private FulfillmentCenter nonExistentFC = new FulfillmentCenter("NonExistentFC");

    //private ShipmentService shipmentService = new ShipmentService(new PackagingDAO(new PackagingDatastore()),
            //new MonetaryCostStrategy());

    private FulfillmentCenter fulfillmentCenter;
    private ShipmentOption shipmentOption;
    private Packaging packaging;
    private Material material;
    private List<ShipmentOption> shipmentOptionList;
    private ShipmentCost shipmentCost;

    @InjectMocks
    private ShipmentService shipmentService;
    @Mock
    private PackagingDAO packagingDAO;
    @Mock
    private CostStrategy costStrategy;

    @BeforeEach
    void setUp() {
        initMocks(this);
        item = Item.builder()
                .withHeight(BigDecimal.valueOf(1))
                .withWidth(BigDecimal.valueOf(1))
                .withLength(BigDecimal.valueOf(1))
                .withAsin("abcde")
                .build();
        fulfillmentCenter = new FulfillmentCenter("");
        material = Material.LAMINATED_PLASTIC;
        packaging = new PolyBag(material, BigDecimal.valueOf(2000));
        shipmentOption = ShipmentOption.builder()
                .withItem(item)
                .withPackaging(packaging)
                .withFulfillmentCenter(fulfillmentCenter)
                .build();
        shipmentOptionList = new ArrayList<>();
        shipmentOptionList.add(shipmentOption);
        shipmentCost = new ShipmentCost(shipmentOption, BigDecimal.ZERO);
    }

    @Test
    void findBestShipmentOption_unknownFulfillmentCenter_returnNullFulfillmentCenterAndPackaging() throws UnknownFulfillmentCenterException, NoPackagingFitsItemException {
        // GIVEN & WHEN
        when(packagingDAO.findShipmentOptions(item, fulfillmentCenter)).thenThrow(UnknownFulfillmentCenterException.class);
        ShipmentOption shipmentOption = shipmentService.findShipmentOption(item, fulfillmentCenter);

        // THEN
        assertNull(shipmentOption.getFulfillmentCenter(), "Should have caught UnknownFulfillmentCenterException and return null FulfillmentCenter");
        assertNull(shipmentOption.getPackaging(), "Should have caught noPackagingFitsItemException and return null Packaging");
    }

    @Test
    void findBestShipmentOption_noPackagingFitsItemException_returnFulfillmentCenterAndNullPackaging() throws UnknownFulfillmentCenterException, NoPackagingFitsItemException {
        // GIVEN & WHEN
        when(packagingDAO.findShipmentOptions(item, fulfillmentCenter)).thenThrow(NoPackagingFitsItemException.class);
        ShipmentOption shipmentOption = shipmentService.findShipmentOption(item, fulfillmentCenter);

        // THEN
        assertNull(shipmentOption.getPackaging(), "Should have caught noPackagingFitsItemException and return null Packaging");
        assertEquals(fulfillmentCenter, shipmentOption.getFulfillmentCenter(), "Should have caught noPackagingFitsItemException and still returned" +
                "known FulfillmentCenter");
    }

    @Test
    void findBestShipmentOption_existentFCAndItemCanFit_returnsShipmentOption() throws UnknownFulfillmentCenterException, NoPackagingFitsItemException {
        // GIVEN & WHEN
        when(packagingDAO.findShipmentOptions(item, fulfillmentCenter)).thenReturn(shipmentOptionList);
        when(costStrategy.getCost(shipmentOption)).thenReturn(shipmentCost);
        ShipmentOption shipmentOption = shipmentService.findShipmentOption(item, fulfillmentCenter);

        // THEN
        assertNotNull(shipmentOption);
    }

    @Test
    void findBestShipmentOption_existentFCAndItemCannotFit_returnsShipmentOption() throws UnknownFulfillmentCenterException, NoPackagingFitsItemException {
        // GIVEN & WHEN
        when(packagingDAO.findShipmentOptions(item, fulfillmentCenter)).thenThrow(NoPackagingFitsItemException.class);
        ShipmentOption shipmentOption = shipmentService.findShipmentOption(item, fulfillmentCenter);

        // THEN
        assertNotNull(shipmentOption);
    }

    @Test
    void findBestShipmentOption_nonExistentFCAndItemCanFit_returnsShipmentOption() throws UnknownFulfillmentCenterException, NoPackagingFitsItemException {
        // GIVEN & WHEN
        when(packagingDAO.findShipmentOptions(item, fulfillmentCenter)).thenThrow(UnknownFulfillmentCenterException.class);
        ShipmentOption shipmentOption = shipmentService.findShipmentOption(item, fulfillmentCenter);

        // THEN
        assertNotNull(shipmentOption);
    }

    @Test
    void findBestShipmentOption_nonExistentFCAndItemCannotFit_returnsShipmentOption() throws UnknownFulfillmentCenterException, NoPackagingFitsItemException {
        // GIVEN & WHEN
        when(packagingDAO.findShipmentOptions(item, fulfillmentCenter)).thenThrow(UnknownFulfillmentCenterException.class);
        ShipmentOption shipmentOption = shipmentService.findShipmentOption(item, fulfillmentCenter);

        // THEN
        assertNotNull(shipmentOption);
    }
}