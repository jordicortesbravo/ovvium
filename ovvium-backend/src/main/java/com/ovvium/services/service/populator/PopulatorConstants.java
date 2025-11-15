package com.ovvium.services.service.populator;

import com.ovvium.services.model.customer.TagId;
import com.ovvium.services.util.util.container.Maps;
import lombok.experimental.UtilityClass;

import java.util.Map;
import java.util.Set;
import java.util.UUID;

@UtilityClass
public class PopulatorConstants {

    public static final UUID BILL_UUID = UUID.fromString("245d5617-dbcd-4fd3-b273-1749db7e2aeb");
    public static final UUID USER_1_FRODO_UUID = UUID.fromString("67490eed-bcfe-4372-aff9-f878f6152694");
    public static final UUID USER_2_SAM_UUID = UUID.fromString("34800067-5a49-4505-9ea2-73ecb4395f9c");
    public static final UUID USER_3_WALTER_UUID = UUID.fromString("60a74777-9697-4f21-a1b5-1e76709c8ccf");
    public static final UUID USER_4_JESSE_UUID = UUID.fromString("9989c966-2d9e-4cbb-bc21-07cb1a992ad8");
    public static final UUID USER_5_JUAN_UUID = UUID.fromString("d4bb4af4-13e4-41b1-9a27-db4612e2d44f");
    public static final UUID USER_6_ROSA_UUID = UUID.fromString("492286f5-aa4a-4cbb-bd17-214476bbc722");

    public static final UUID CUSTOMER_USER_1_GANDALF_UUID = UUID.fromString("3bfeab49-4085-45e7-a712-7398d79f5c75");
    public static final UUID CUSTOMER_USER_2_IBRUN_UUID = UUID.fromString("2aa77b38-353c-4cae-ae54-47a08f6d3ae8");
    public static final UUID CUSTOMER_USER_3_GUSTAVO_UUID = UUID.fromString("73ff12d5-ce0a-49fe-8b6d-ecc92ad79be1");
    public static final UUID CUSTOMER_USER_4_JORDI_UUID = UUID.fromString("97d8898b-e798-44e3-a857-180b21bdae91");

    public static final UUID CUSTOMER_1_UUID = UUID.fromString("e409e86a-9d8c-4de4-9fe8-c793142787f4");
    public static final UUID CUSTOMER_2_UUID = UUID.fromString("e368683b-6a5a-4033-a101-63b8464f4350");
    public static final UUID CUSTOMER_3_UUID = UUID.fromString("ba5a5465-bfe1-4f00-a1cb-09d63a3c65e8");
    public static final UUID CUSTOMER_1_LOCATION_1_UUID = UUID.fromString("f2e4b39a-60f3-497b-ad18-e0f3df65a1bb");
    public static final UUID CUSTOMER_2_LOCATION_1_UUID = UUID.fromString("0ce8d0bb-5452-4a44-8c0a-6ec0191f88bc");
    public static final UUID CUSTOMER_3_LOCATION_1_UUID = UUID.fromString("67969f04-0973-465d-94dd-a04b1290b6ff");

    public static final TagId CUSTOMER_1_LOCATION_1_TAG_ID = new TagId("sJo1nSbsdE");
    public static final TagId CUSTOMER_2_LOCATION_1_TAG_ID = new TagId("KNgEnSbs4G");
    public static final TagId CUSTOMER_3_LOCATION_1_TAG_ID = new TagId("UEp8FD42wD");

    public static final Double TAX = 0.1;

    public static final Set<UUID> TEST_USERS = Set.of(
            CUSTOMER_USER_1_GANDALF_UUID,
            CUSTOMER_USER_2_IBRUN_UUID,
            CUSTOMER_USER_3_GUSTAVO_UUID,
            USER_1_FRODO_UUID,
            USER_2_SAM_UUID,
            USER_3_WALTER_UUID,
            USER_4_JESSE_UUID,
            USER_5_JUAN_UUID,
            USER_6_ROSA_UUID
    );
    public static final Set<UUID> TEST_CUSTOMERS = Set.of(CUSTOMER_1_UUID, CUSTOMER_2_UUID, CUSTOMER_3_UUID);

    public static final UUID CUSTOMER_USER_1_PCI_DETAILS_ID = UUID.fromString("551745cf-86bc-4958-96ce-e02ecd356bc7");
    public static final UUID CUSTOMER_USER_2_PCI_DETAILS_ID = UUID.fromString("ef2537ce-7198-460d-b8bf-6cf967cfea02");
    public static final UUID CUSTOMER_USER_3_PCI_DETAILS_ID = UUID.fromString("7397beef-6fc8-441d-a44c-f199a76ccaca");
    public static final UUID CUSTOMER_USER_4_PCI_DETAILS_ID = UUID.fromString("bdef7df7-863f-4c34-8c2e-8699544e27f0");

    public static final UUID USER_1_PCI_DETAILS_ID = UUID.fromString("8d812b9a-d576-431d-bb9d-14f6f91132cc");
    public static final UUID USER_2_PCI_DETAILS_ID = UUID.fromString("ed811823-1da1-4ed9-b782-7fdc9423f4b5");
    public static final UUID USER_3_PCI_DETAILS_ID = UUID.fromString("b880c06b-18c1-404d-8be0-3e6d13bdf354");
    public static final UUID USER_4_PCI_DETAILS_ID = UUID.fromString("81462e9e-a66d-44c4-961f-5fb4398fdca1");
    public static final UUID USER_5_PCI_DETAILS_ID = UUID.fromString("7dc4b5a9-bdd1-4f9b-b91c-ebb6f58c11f0");
    public static final UUID USER_6_PCI_DETAILS_ID = UUID.fromString("18d17ea4-c308-4779-be7c-111f670f4f22");

    public static final String USER_PCI_USER_ID = "40590619";
    public static final String USER_PCI_USER_TOKEN = "YkNWaFIycG5MRE4";
    public static final String CUSTOMER_SPLIT_ID = "a915c673219628d93c62e409c22f37b93259d33004672cdbe499d0fd3466ddb5";

    public static final Map<String, String> CATEGORIES = Maps.mapSS()
            .with("BEBIDAS", "DRINKS").with("TAPAS", "TAPAS").with("ENTRANTES", "APPETIZERS").with("CARNES", "MEAT").with("PESCADOS", "FISH")
            .with("PASTA Y ARROCES", "PASTA & RICE").with("POSTRES", "DESSERT").with("VINOS", "WINE").with("COPAS", "DRINKS").with("DESAYUNOS", "BREAKFAST")
            .with("CAFES", "COFFEE").with("FAVORITOS", "FAVOURITES").with("MENUS", "MENUS");


}
