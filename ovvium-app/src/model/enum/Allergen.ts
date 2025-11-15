export enum Allergen {
	GLUTEN = "GLUTEN", //
	CRUSTACEANS = "CRUSTACEANS", //
	EGGS ="EGGS", //
	FISH = "FISH", //
	PEANUTS = "PEANUTS", //
	SOY = "SOY", //
	DAIRY_PRODUCTS = "DAIRY_PRODUCTS", //
	NUTS = "NUTS", //
	CELERY = "CELERY", //
	MUSTARD = "MUSTARD", //
	SESAME = "SESAME", //
	SULPHITES = "SULPHITES", //
	LUPINS = "LUPINS", //
    MOLLUSKS = "MOLLUSKS"
}

export function allergensEnumValues() {
    return ["GLUTEN", "CRUSTACEANS", "EGGS", "FISH", "PEANUTS", "SOY",  "DAIRY_PRODUCTS", "NUTS", "CELERY", "MUSTARD", "SESAME", "SULPHITES", "LUPINS", "MOLLUSKS"];
}

export function asAllergen(value: string) {
    switch(value.toUpperCase()) {
        case Allergen.GLUTEN:
            return Allergen.GLUTEN;
        case Allergen.CRUSTACEANS:
            return Allergen.CRUSTACEANS;
        case Allergen.EGGS:
            return Allergen.EGGS;
        case Allergen.FISH:
            return Allergen.FISH;
        case Allergen.PEANUTS:
            return Allergen.PEANUTS;
        case Allergen.SOY:
            return Allergen.SOY;
        case Allergen.DAIRY_PRODUCTS:
            return Allergen.DAIRY_PRODUCTS;
        case Allergen.NUTS:
            return Allergen.NUTS;
        case Allergen.CELERY:
            return Allergen.CELERY;
        case Allergen.MUSTARD:
            return Allergen.MUSTARD;
        case Allergen.SESAME:
            return Allergen.SESAME;
        case Allergen.SULPHITES:
            return Allergen.SULPHITES;
        case Allergen.LUPINS:
            return Allergen.LUPINS;
        case Allergen.MOLLUSKS:
            return Allergen.MOLLUSKS;
        default:
            throw new Error("Allergen with value " + value + " doesn't exist");
    }
}