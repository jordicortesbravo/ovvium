import { allergensEnumValues } from '../model/enum/Allergen';
import { FoodPreferences, foodPreferencesEnumValues } from '../model/enum/FoodPreferences';

export function mapSelectedAllergens(allergens: string[]): Map<string, boolean> {
    var map = new Map<string, boolean>();
    var allergensKeys = allergensEnumValues();
    allergensKeys.forEach(key => {
        map.set(key, allergens.indexOf(key) != -1);
    })

    return map;
}

export function mapSelectedFoodPreferences(foodPreferences: string[]): Map<string, boolean> {
    var map = new Map<string, boolean>();
    var FoodPreferencesKeys = foodPreferencesEnumValues();
    FoodPreferencesKeys.forEach(key => {
        map.set(key, foodPreferences.indexOf(key) != -1);
    })

    return map;
}

