import { CategoryResponse } from "./response/CategoryResponse";
import { Product } from "./Product";
import { getLocalization } from "app/services/LocalizationService";

export class Category {
    id: string;
    name: string;
    products?: Array<Product>;

    
    constructor(category: Category = {} as Category) {
        this.id = category.id;
        this.name = category.name;
    }

    static from(categoryResponse: CategoryResponse): Category {
        var categoryName = getLocalization(categoryResponse.name);
        return new Category({
            id: categoryResponse.id,
            name: categoryName
        } as Category);
    }
    
}

