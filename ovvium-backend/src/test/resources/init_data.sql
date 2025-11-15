TRUNCATE TABLE "product_option" CASCADE;
TRUNCATE TABLE "selected_product_option" CASCADE;
TRUNCATE TABLE "product_option_group" CASCADE;
TRUNCATE TABLE "order" CASCADE;
TRUNCATE TABLE "rating" CASCADE;
TRUNCATE TABLE "average_rating";
TRUNCATE TABLE "payment_order" CASCADE;
TRUNCATE TABLE "tip" CASCADE;
TRUNCATE TABLE "discount" CASCADE;
TRUNCATE TABLE "event" CASCADE;
TRUNCATE TABLE "picture" CASCADE;
TRUNCATE TABLE "product" CASCADE;
TRUNCATE TABLE "product_group_entry" CASCADE;
TRUNCATE TABLE "product_group_entry_products" CASCADE;
TRUNCATE TABLE "product" CASCADE;
TRUNCATE TABLE "product_pictures" CASCADE;
TRUNCATE TABLE "category" CASCADE;
TRUNCATE TABLE "bill_locations" CASCADE;
TRUNCATE TABLE "location" CASCADE;
TRUNCATE TABLE "bill_members" CASCADE;
TRUNCATE TABLE "bill" CASCADE;
TRUNCATE TABLE "invoice_date" CASCADE;
TRUNCATE TABLE "invoice" CASCADE;
TRUNCATE TABLE "zone" CASCADE;
TRUNCATE TABLE "user_pci_details" CASCADE;
TRUNCATE TABLE "user" CASCADE;
TRUNCATE TABLE "customer" CASCADE;
TRUNCATE TABLE "api_key" CASCADE;

-- Select N next val sequences to avoid collisions with the surrogate_id used on these queries
select nextval('ovvium_sequence')
from generate_series(1, 50);

-- Customer

INSERT INTO public.customer
(surrogate_id, id, address, cif, description, latitude, longitude, time_zone, "name", pci_split_user_id, phones,
 website, commission_config, created, updated, invoice_number_prefix)
VALUES (1, 'e409e86a-9d8c-4de4-9fe8-c79314278756', 'Casanova, 157. Barcelona', '123456',
        'Cocina casera elaborada con todo el cariño y los ingredientes de la mejor calidad para que disfrute de cada bocado',
        '41.3910795', '2.1510758', 'Europe/Madrid', 'Maitea Taberna',
        'a915c673219628d93c62e409c22f37b93259d33004672cdbe499d0fd3466ddb5', '934395107;933219790',
        'http://www.maitea.es/',
        '{"strategy":"BASIC","tipPercentage":0.5,"minimumCommission":0.2, "config":{}}',
        '2020-02-17 22:59:37.636', '2020-02-17 22:59:37.636', 'TEST');
INSERT INTO public.customer
(surrogate_id, id, address, cif, description, latitude, longitude, "name", pci_split_user_id, phones, website,
 commission_config, created, updated, invoice_number_prefix)
VALUES (2, 'e368683b-6a5a-4033-a101-63b8464f4351', 'C/ Villarroel, 247-249
08036 Barcelona - España', 'La Cratera', 'Aprovechando los productos típicos de cada temporada. Los platos fuertes son los arroces y los bacalaos, sin olvidar las pastas y las ensaladas de temporada.
El ambiente es relajado y tranquilo con el servicio especialmente seleccionado y muy amable. Su punto fuerte es la relación calidad-precio. La presentación de los platos es muy cuidada y los pequeños detalles nunca se olvidan.',
        '41.3917453', '2.1479208', 'La Cratera', 'abcdefg', '934052491', 'https://lacratera.com/',
        '{"strategy":"BY_CARD_CATEGORY","tipPercentage":0.5,"minimumCommission":0.0,"config":{"BUSINESS":{"EUROPEAN_NO_EEA":{"basePercentage":0.5,"commission":0.09},"NATIONAL":{"basePercentage":0.5,"commission":0.09},"EUROPEAN":{"basePercentage":0.5,"commission":0.09},"NOT_EUROPEAN":{"basePercentage":0.5,"commission":0.09}},"CONSUMER":{"EUROPEAN_NO_EEA":{"basePercentage":0.5,"commission":0.09},"NATIONAL":{"basePercentage":0.5,"commission":0.09},"EUROPEAN":{"basePercentage":0.5,"commission":0.09},"NOT_EUROPEAN":{"basePercentage":0.5,"commission":0.09}}}}',
        '2020-02-17 22:59:37.759', '2020-02-17 22:59:37.759', 'TEST');

-- User

INSERT INTO public."user"
(surrogate_id, id, allergens, email, enabled, facebook_email, facebook_profile_name, facebook_profile_id,
 food_preferences, google_profile_email, google_profile_name, google_profile_id, "name", "password", roles,
 user_customer_id, created, updated)
VALUES (3, '34800067-5a49-4505-9ea2-73ecb4395f9c', 'EGGS;FISH', 'jpadilla@ovvium.com', true, NULL, NULL, NULL,
        'OMNIVORE;VEGETARIAN', NULL, NULL, NULL, 'Jorge Padilla',
        '$2a$10$S2K6mPCIQLNqqIxr76XUUu9BA4wesCrrZ0q6DINcEVbO.qPsnJ2jm', 'ROLE_USERS', NULL, '2020-02-17 22:59:37.549',
        '2020-02-17 22:59:37.549');

INSERT INTO public."user"
(surrogate_id, id, allergens, email, enabled, facebook_email, facebook_profile_name, facebook_profile_id,
 food_preferences, google_profile_email, google_profile_name, google_profile_id, "name", "password", roles,
 user_customer_id, created, updated)
VALUES (4, 'a29b0452-dabb-4b43-b243-71713c9d75dc', NULL, 'mireia.sole@ovvium.com', true, NULL, NULL, NULL, NULL, NULL,
        NULL, NULL, 'Mireia Sole', '$2a$10$h5KEOOucQLhYZt1BoAzvPeUebGWsv5RPB4oItU2YzOeh6aJCivghG',
        'ROLE_CUSTOMERS_ADMIN;ROLE_USERS', 2, '2020-02-17 22:59:37.553', '2020-02-17 22:59:37.553');

INSERT INTO public."user"
(surrogate_id, id, allergens, email, enabled, facebook_email, facebook_profile_name, facebook_profile_id,
 food_preferences, google_profile_email, google_profile_name, google_profile_id, "name", "password", roles,
 user_customer_id, created, updated)
VALUES (17, '67490eed-bcfe-4372-aff9-f878f6152694', 'EGGS;DAIRY_PRODUCTS', 'jcortes@ovvium.com', true, NULL, NULL, NULL,
        'OMNIVORE;VEGAN', NULL, NULL, NULL, 'Jordi Cortés',
        '$2a$10$ZFxFGFpN8RVlxNuRBT8hW.jtOGwxMwoe4KsBtIbtrgKYm7jwe/YXK', 'ROLE_USERS', NULL, '2020-02-22 15:39:34.807',
        '2020-02-22 15:39:34.807');


INSERT INTO public."user" -- not enabled user
(surrogate_id, id, allergens, email, enabled, facebook_email, facebook_profile_name, facebook_profile_id,
 food_preferences, google_profile_email, google_profile_name, google_profile_id, "name", "password", roles,
 user_customer_id, created, updated)
VALUES (38, '0680eef8-3d3f-4b83-b39e-ccb3750dd2f2', 'EGGS', 'asole@ovvium.com', false, NULL, NULL, NULL,
        'OMNIVORE;VEGAN', NULL, NULL, NULL, 'Angel Solé',
        '$2a$10$ZFxFGFpN8RVlxNuRBT8hW.jtOGwxMwoe4KsBtIbtrgKYm7jwe/YXK', 'ROLE_USERS', NULL, '2020-02-22 15:39:34.807',
        '2020-02-22 15:39:34.807');


-- User Pci Details

INSERT INTO public."user_pci_details"
(surrogate_id, id, pci_provider, provider_reference_token, provider_user_id, user_id, created, updated)
VALUES (5, '8d812b9a-d576-431d-bb9d-14f6f91132cc', 'PAYCOMET', 'WkdCNlFIRmhLamc', '26893679', 17,
        '2020-02-22 15:39:34.871', '2020-02-22 15:39:34.871');


INSERT INTO public."user_pci_details"
(id, pci_provider, provider_reference_token, provider_user_id, user_id, created, updated)
VALUES ('6d812b9a-d576-431d-bb9d-14f6f91132d4', 'PAYCOMET', 'WkdCNlFIRmhLamc', '26893679', 4, '2020-02-22 15:39:34.871',
        '2020-02-22 15:39:34.871');


-- Zone

INSERT INTO public."zone"
    (surrogate_id, id, customer_id, name, created, updated)
VALUES (6, '186321cf-2bc5-471e-b8dd-a052731cd784', 'e409e86a-9d8c-4de4-9fe8-c79314278756', 'Salón principal',
        '2020-02-17 22:59:37.761', '2020-02-17 22:59:37.761');
INSERT INTO public."zone"
    (surrogate_id, id, customer_id, name, created, updated)
VALUES (7, 'bd229875-7bb6-4f7a-801d-98fcdfb3b37a', 'e368683b-6a5a-4033-a101-63b8464f4351', 'Salón principal',
        '2020-02-17 22:59:37.808', '2020-02-17 22:59:37.808');


-- Invoice Date
INSERT INTO public."invoice_date"
    (surrogate_id, id, customer_id, "date", status, created, updated)
VALUES (19, '7fdbb80a-4cbf-40d2-b9de-4252072b30a7', 'e409e86a-9d8c-4de4-9fe8-c79314278756', '2020-02-17', 'OPEN',
        '2020-02-17 23:00:19.442', '2020-02-17 23:00:19.500');
INSERT INTO public."invoice_date"
    (surrogate_id, id, customer_id, "date", status, created, updated)
VALUES (20, '20d44323-be52-4528-acb1-0f439bdf986f', 'e368683b-6a5a-4033-a101-63b8464f4351', '2020-02-17', 'OPEN',
        '2020-02-17 23:00:19.442', '2020-02-17 23:00:19.500');


-- Bill

INSERT INTO public.bill
(surrogate_id, id, customer_id, invoice_date_id, bill_status, created, updated)
VALUES (8, '245d5617-dbcd-4fd3-b273-1749db7e2aeb', 'e409e86a-9d8c-4de4-9fe8-c79314278756',
        '7fdbb80a-4cbf-40d2-b9de-4252072b30a7', 'OPEN', '2020-02-17 23:00:19.411', '2020-02-17 23:00:19.500');
INSERT INTO public.bill
(surrogate_id, id, customer_id, invoice_date_id, bill_status, created, updated)
VALUES (9, 'f5ecf091-9393-4687-9b22-68ad588b0528', 'e368683b-6a5a-4033-a101-63b8464f4351',
        '20d44323-be52-4528-acb1-0f439bdf986f', 'OPEN', '2020-02-18 19:58:43.751', '2020-02-18 19:58:43.751');
INSERT INTO public.bill -- CLOSED bill
(surrogate_id, id, customer_id, invoice_date_id, bill_status, created, updated)
VALUES (27, '453da1b2-bae2-4f55-8edc-3daee0b327af', 'e368683b-6a5a-4033-a101-63b8464f4351',
        '20d44323-be52-4528-acb1-0f439bdf986f', 'CLOSED', '2020-02-18 19:58:43.751', '2020-02-18 19:58:43.751');
INSERT INTO public.bill -- Bill of Advance Payment
(surrogate_id, id, customer_id, invoice_date_id, bill_status, created, updated)
VALUES (37, '780f20b5-6b04-4f84-aafc-73fbe47f7287', 'e368683b-6a5a-4033-a101-63b8464f4351',
        '20d44323-be52-4528-acb1-0f439bdf986f', 'OPEN', '2020-02-18 19:58:43.751', '2020-02-18 19:58:43.751');
INSERT INTO public.bill -- Bill of PENDING PaymentOrder
(surrogate_id, id, customer_id, invoice_date_id, bill_status, created, updated)
VALUES (39, 'cad30718-b354-47cb-87f7-3873344fa1ba', 'e368683b-6a5a-4033-a101-63b8464f4351',
        '20d44323-be52-4528-acb1-0f439bdf986f', 'OPEN', '2020-02-18 19:58:43.751', '2020-02-18 19:58:43.751');


-- Bill members

INSERT INTO public."bill_members"
    (bill_surrogate_id, members_surrogate_id)
VALUES (8, 17);
INSERT INTO public."bill_members"
    (bill_surrogate_id, members_surrogate_id)
VALUES (27, 3);

-- Location

INSERT INTO public."location"
(surrogate_id, id, customer_id, description, "position", "tag_id", zone_surrogate_id, location_customer_id, created,
 updated, serial_number)
VALUES (10, '992fb28d-a67b-4c08-99b0-8d1bbca56c11', 'e409e86a-9d8c-4de4-9fe8-c79314278756', 'Mesa 2', 2, 'KNgEnSbs4G',
        6, 1, '2020-02-17 22:59:37.766', '2020-02-17 22:59:37.766', '0001');
INSERT INTO public."location"
(surrogate_id, id, customer_id, description, "position", "tag_id", zone_surrogate_id, location_customer_id, created,
 updated, serial_number)
VALUES (11, '9f6abe45-8b1a-4bb6-8203-0f8ebe51d4d5', 'e409e86a-9d8c-4de4-9fe8-c79314278756', 'Mesa 3', 3, 'KNgEnSbs5G',
        6, 1, '2020-02-17 22:59:37.767', '2020-02-17 22:59:37.767', '0002');
INSERT INTO public."location" -- advance payment Location
(surrogate_id, id, customer_id, description, "position", "tag_id", zone_surrogate_id, location_customer_id, created,
 updated, serial_number, advance_payment)
VALUES (35, 'b116e2da-bb93-432f-880f-c25b68b68968', 'e409e86a-9d8c-4de4-9fe8-c79314278756', 'Mesa 3', 4, 'KNgEnSbs8G',
        7, 2, '2020-02-17 22:59:37.809', '2020-02-17 22:59:37.809', '0005', true);


INSERT INTO public."location"
(surrogate_id, id, customer_id, description, "position", "tag_id", zone_surrogate_id, location_customer_id, created,
 updated, serial_number)
VALUES (12, '915a4d57-7b1e-49f1-a3dc-a6e783f734ac', 'e368683b-6a5a-4033-a101-63b8464f4351', 'Mesa 1', 1, 'KNgEnSbs6G',
        7, 2, '2020-02-17 22:59:37.808', '2020-02-17 22:59:37.808', '0003');
INSERT INTO public."location"
(surrogate_id, id, customer_id, description, "position", "tag_id", zone_surrogate_id, location_customer_id, created,
 updated, serial_number)
VALUES (13, '297df470-f4c6-44dd-be81-8ab5e08852ab', 'e368683b-6a5a-4033-a101-63b8464f4351', 'Mesa 2', 2, 'KNgEnSbs7G',
        7, 2, '2020-02-17 22:59:37.809', '2020-02-17 22:59:37.809', '0004');

-- Bill Locations

INSERT INTO public."bill_locations"
    (bill_surrogate_id, locations_surrogate_id)
VALUES (8, 11);
INSERT INTO public."bill_locations"
    (bill_surrogate_id, locations_surrogate_id)
VALUES (9, 13);
INSERT INTO public."bill_locations"
    (bill_surrogate_id, locations_surrogate_id)
VALUES (37, 35);

-- Category

INSERT INTO public.category
    (surrogate_id, id, name_default_value, name_translations, "order", customer_surrogate_id, created, updated)
VALUES (14, '25e8f8d0-f535-4ec5-bad3-dfa9ab09e056', 'Entrantes','{"es-ES":"Entrantes"}', 0, 1, '2020-02-17 22:59:37.841', '2020-02-17 22:59:37.841');


INSERT INTO public.category
(surrogate_id, id, name_default_value, name_translations, "order", customer_surrogate_id, created, updated)
VALUES (42, '6e2a5f11-0231-4460-a0b7-584e4a3dd10d', 'Tapas','{"es-ES":"Tapas"}', 0, 2, '2020-02-17 22:59:37.841', '2020-02-17 22:59:37.841');

-- Picture

INSERT INTO public.picture
    (surrogate_id, id, base_uri, filename, created, updated)
VALUES (32, 'f8e402e0-2ce2-4fb4-b2f7-02d58b2beb41', '/media/pictures/20200708/f8e402e0-2ce2-4fb4-b2f7-02d58b2beb41',
        'cover.jpg', '2020-07-08 08:47:33.485', '2020-07-08 08:47:33.485');

INSERT INTO public.picture
    (surrogate_id, id, base_uri, filename, created, updated)
VALUES (33, '90119b94-a926-4be9-8b50-a1f3820f35fa', '/media/pictures/20200708/90119b94-a926-4be9-8b50-a1f3820f35fa',
        'picture.jpg', '2020-07-08 08:47:34.311', '2020-07-08 08:47:34.311');


-- Product

INSERT INTO public.product
(surrogate_id, id, product_type, name_default_value, name_translations, description_default_value, description_translations, base_price_amount, base_price_currency, "order", type, service_builder_location, tax,
 cover_picture_surrogate_id, category_surrogate_id, customer_surrogate_id, created, updated)
VALUES (15, '8f21a958-0417-42a1-8c6a-6cde1327aa22', 'PRODUCT_ITEM', 'Ensalada de bolitas de mozarella','{"es-ES":"Ensalada de bolitas de mozarella"}', 'con Cherry, Membrillo, Frutos Secos y Vinagreta','{"es-ES":"con Cherry, Membrillo, Frutos Secos y Vinagreta"}', 7.4500, 'EUR', 0, 'FOOD', 'KITCHEN', 0.1, 32, 14, 1,
        '2020-02-17 22:59:37.877', '2020-02-17 22:59:37.877');


-- Product Menu
INSERT INTO public.product
(surrogate_id, id, product_type,  name_default_value, name_translations, description_default_value, description_translations, base_price_amount, base_price_currency, "order", "type", service_builder_location, tax,
 allergens, hidden, recommended, days_of_week, start_time, end_time, category_surrogate_id, customer_surrogate_id,
 created, updated)
VALUES (29, '45ba8279-f8fc-4d33-8121-b5a13c5799f9', 'PRODUCT_GROUP', 'Menú Diario','{"es-ES":"Menú Diario"}', 'Nuestro menú del Día','{"es-ES":"Nuestro menú del Día"}', 10.0000, 'EUR', 1, 'GROUP', 'KITCHEN', 0.1, NULL,
        false, false, 'MONDAY;TUESDAY;WEDNESDAY;THURSDAY;FRIDAY;SATURDAY;SUNDAY', '00:00:00', '23:59:59', 14, 1,
        '2020-06-27 14:39:28.627', '2020-06-27 14:39:34.374');

INSERT INTO public.product_group_entry
    (surrogate_id, id, service_time, product_group_id, created, updated)
VALUES (31, '83609419-b22b-44b9-ac61-5e653e82292e', 'SOONER', 29, '2020-06-27 14:39:28.608', '2020-06-27 14:39:28.608');

INSERT INTO public.product_group_entry_products
    (product_group_entry_surrogate_id, products_surrogate_id)
VALUES (31, 15);


-- Pictures of Product

INSERT INTO public.product_pictures
    (product_surrogate_id, pictures_surrogate_id)
VALUES (15, 33);

-- Invoice

INSERT INTO public.invoice
(surrogate_id, id, invoice_number, customer_id, bill_id, invoice_date_surrogate_id, created, updated)
VALUES (23, '96f101aa-3473-485f-9d3e-c4900b3b5509', 'OVVTEST-1', 'e409e86a-9d8c-4de4-9fe8-c79314278756',
        '245d5617-dbcd-4fd3-b273-1749db7e2aeb', 19, '2020-03-29 18:14:23.365', '2020-03-29 18:14:23.365');

INSERT INTO public.invoice
(surrogate_id, id, invoice_number, customer_id, bill_id, invoice_date_surrogate_id, created, updated)
VALUES (24, '96f101aa-3473-485f-9d3e-c4900b3b5510', 'OVVTEST-2', 'e409e86a-9d8c-4de4-9fe8-c79314278756',
        'f5ecf091-9393-4687-9b22-68ad588b0528', 19, '2020-03-29 18:14:23.366', '2020-03-29 18:14:23.366');

-- Payment Order

INSERT INTO public.payment_order
(surrogate_id, id, payment_type, payment_order_type, status, provider, split_customer_amount, split_customer_currency,
 purchase_details_amount, purchase_details_currency, purchase_details_transaction_id, split_details_amount,
 split_details_currency, split_details_transaction_id, invoice_surrogate_id, bill_surrogate_id, discount_surrogate_id,
 payer_surrogate_id, tip_surrogate_id, created, updated)
VALUES (21, 'caa7d2a3-fa9e-4435-8380-56b9e05b1d01', 'APP_CARD', 'APP', 'CONFIRMED', 'PAYCOMET', NULL, NULL, 8.20, 'EUR',
        '80D22F7D122D40358EE5ACB40B8E297A', 8.00, 'EUR', '4427014BCFC74A22BB959C3FDEB10831', 23, 9, NULL, 17, NULL,
        '2020-03-29 18:14:23.284', '2020-03-29 18:14:23.463');

INSERT INTO public.payment_order -- PENDING PaymentOrder
(surrogate_id, id, payment_type, payment_order_type, pci_transaction_id, status, provider, split_customer_amount, split_customer_currency,
 purchase_details_amount, purchase_details_currency, purchase_details_transaction_id, split_details_amount,
 split_details_currency, split_details_transaction_id, invoice_surrogate_id, bill_surrogate_id, discount_surrogate_id,
 payer_surrogate_id, tip_surrogate_id, created, updated)
VALUES (40, '420b5fe7-c101-463e-a748-5a0b56eb7f4f', 'APP_CARD', 'APP', 'ac74ccd9-be8e-4eae-81a4-fd1c2812dd30',  'PENDING', 'PAYCOMET', NULL, NULL, NULL, NULL,
        NULL, NULL, NULL, NULL, NULL, 39, NULL, 17, NULL, '2020-03-29 18:14:23.284', '2020-03-29 18:14:23.463');


-- Orders

INSERT INTO public."order"
(surrogate_id, id, notes, payment_status, issue_status, base_price_amount, base_price_currency, tax, service_time,
 product_surrogate_id, user_surrogate_id, bill_id, created, updated, status)
VALUES (16, '5127717a-7c84-4fb9-b36d-8de60b118368', 'Bien hecho', 'PENDING', 'PENDING', 8.2000, 'EUR', 0.1,'SOONER', 15, 17,
        8, '2020-02-17 23:00:19.442', '2020-02-17 23:00:19.500', 'CREATED');

INSERT INTO public."order"
(surrogate_id, id, notes, payment_status, issue_status, base_price_amount, base_price_currency, tax, service_time,
 product_surrogate_id, user_surrogate_id, bill_id, created, updated, status)
VALUES (18, '82bd335d-b56c-4df3-b9f4-bb7b49b6d54f', 'Al punto', 'PENDING', 'PENDING', 8.2000, 'EUR', 0.1, 'SOONER', 15, 17,
        8, '2020-02-17 23:00:19.442', '2020-02-17 23:00:19.500', 'CREATED');

INSERT INTO public."order"
(surrogate_id, id, notes, payment_status, issue_status, base_price_amount, base_price_currency,tax, service_time,
 product_surrogate_id, user_surrogate_id, bill_id, invoice_id, created, updated, status)
VALUES (22, '82bd335d-b56c-4df3-b9f4-bb7b49b6d54e', 'Al punto', 'PAID', 'PENDING', 8.2000, 'EUR', 0.1, 'SOONER', 15, 17, 8,
        23, '2020-02-17 23:00:19.442', '2020-02-17 23:00:19.500', 'CREATED');

INSERT INTO public."order"
(surrogate_id, id, notes, payment_status, issue_status, base_price_amount, base_price_currency,tax, service_time,
 product_surrogate_id, user_surrogate_id, bill_id, invoice_id, created, updated, status)
VALUES (25, '82bd335d-b56c-4df3-b9f4-bb7b49b6d555', 'Bien hecho', 'PENDING', 'PENDING', 8.2000, 'EUR', 0.1, 'SOONER', 15, 17,
        9, 24, '2020-02-17 23:00:19.442', '2020-02-17 23:00:19.500', 'CREATED');

INSERT INTO public."order"
(surrogate_id, id, notes, payment_status, issue_status, base_price_amount, base_price_currency,tax, service_time,
 product_surrogate_id, user_surrogate_id, bill_id, invoice_id, created, updated, status)
VALUES (26, '82bd335d-b56c-4df3-b9f4-bb7b49b6d566', 'Al punto', 'PENDING', 'PENDING', 8.2000, 'EUR', 0.1, 'SOONER', 15, 17,
        9, 24, '2020-02-17 23:00:19.442', '2020-02-17 23:00:19.500', 'CREATED');

INSERT INTO public."order" -- order of PENDING PaymentOrder
(surrogate_id, id, notes, payment_status, issue_status, base_price_amount, base_price_currency,tax, service_time,
 product_surrogate_id, user_surrogate_id, bill_id, invoice_id, created, updated, status)
VALUES (41, '01b67b8a-2827-4e51-b439-1ce754ce0980', 'Al punto', 'PENDING', 'PENDING', 8.2000, 'EUR', 0.1, 'SOONER', 15, 17,
        39, NULL, '2020-02-17 23:00:19.442', '2020-02-17 23:00:19.500', 'CREATED');

-- PaymentOrder to Order

INSERT INTO public."payment_order_orders" (payment_order_surrogate_id, orders_surrogate_id)
VALUES (21, 22),
       (40, 41); -- order of PENDING PaymentOrder

-- API KEY

INSERT INTO api_key(id, key, client)
VALUES ('f9ca6604-a02f-43c5-a1ac-55e3524dcd29', '329N44McG2t7KUTkXd6ixy1f9816yM6N', 'Ovvium Tests');