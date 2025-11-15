import { Crop } from '../model/enum/Crop';
import { ProductType } from '../model/enum/ProductType';
import { Product } from '../model/Product';
import { StringUtils } from '../util/StringUtils';
import { ProductGroup } from '../model/ProductGroup';
import { ServiceTime } from '../model/enum/ServiceTime';
import { Picture } from '../model/Picture';

export class ProductService {

    static filterProducts(products: Product[], typeFilter: ProductType, textFilter: string) : Product[] {
        var filteredProducts = products;
        if(textFilter) {
            filteredProducts = filteredProducts.filter((product) => {
                var match = StringUtils.containsIgnoreCase(product.name, textFilter);
                match =  match || StringUtils.containsIgnoreCase(product.category, textFilter);
                return match;
              });
        } else if(typeFilter) {
            filteredProducts = products.filter((product) => {
                return product.type.toString() == typeFilter.toString();
            });
        }
        return filteredProducts.concat();
    }
    
    static mapByCategories(products: Product[]): Map<string, Product[]> {
        var map = new Map<string, Product[]>();
        products.forEach(product => {
            var category = product.category;
            if(!map.has(category)) {
                map.set(category, [] as Product[]);
            }
            var ps = map.get(category) as Product[];
            ps.push(product);
        });
        return map;
    }
    
    static filterAndGroupByCategory(products: Product[], typeFilter: ProductType, textFilter: string): Map<string, Product[]> {
        return ProductService.mapByCategories(ProductService.filterProducts(products, typeFilter, textFilter));
    }

    static getMediumProductImageUri(product: Product): string|undefined {
        return ProductService.getProductImageUri(product, Crop.MEDIUM);
    }

    static getLowProductImageUri(product: Product): string|undefined {
        return product.coverPicture ? product.coverPicture[Crop.LOW].url : undefined;
    }

    static getProductImageUri(product: Product, crop: string): string|undefined {
        var pictures = product.pictures;
        if(pictures && pictures.length > 0) {
            var picture = pictures[0][crop];
            return picture ? picture.url : undefined;
        }
        return undefined;
    }

    static getPicturesFromGroup(productGroup: ProductGroup) {
		
		var soonerPictures = ProductService.getPicturesFromGroupByServiceTime(productGroup, ServiceTime.SOONER);
		var starterPictures = ProductService.getPicturesFromGroupByServiceTime(productGroup, ServiceTime.STARTER);
		var firstCoursePictures = ProductService.getPicturesFromGroupByServiceTime(productGroup, ServiceTime.FIRST_COURSE);
		var secondCoursePictures = ProductService.getPicturesFromGroupByServiceTime(productGroup, ServiceTime.SECOND_COURSE);
		var dessertPictures = ProductService.getPicturesFromGroupByServiceTime(productGroup, ServiceTime.DESSERT);

		var pictures = new Array<Picture>();
		soonerPictures.forEach(p => pictures.push(p[Crop.MEDIUM]));
		starterPictures.forEach(p => pictures.push(p[Crop.MEDIUM]));
		firstCoursePictures.forEach(p => pictures.push(p[Crop.MEDIUM]));
		secondCoursePictures.forEach(p => pictures.push(p[Crop.MEDIUM]));
		dessertPictures.forEach(p => pictures.push(p[Crop.MEDIUM]));
        
        return pictures;
    }
    
    private static getPicturesFromGroupByServiceTime(productGroup: ProductGroup, serviceTime: ServiceTime) {
        return productGroup.products && productGroup.products.has(serviceTime) ? 
				productGroup.products.get(serviceTime)!
					.filter(p => p.type == ProductType.FOOD) 
					.filter(p => p.pictures && p.pictures.length > 0)
					.map(p => p.pictures[0])
                : [];
    }
}



