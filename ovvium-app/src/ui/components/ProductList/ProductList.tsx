import React from "react";
import { ListRenderItemInfo, SectionList, SectionListData, Text, View, RefreshControl } from 'react-native';
import { ProductType } from '../../../model/enum/ProductType';
import { Product } from '../../../model/Product';
import { ProductService } from '../../../services/ProductService';
import { ProductListItem } from '../ProductListitem/ProductListItem';
import { productListHeaderStyle } from './style';
import { Allergen } from "../../../model/enum/Allergen";
import { msg } from "../../../services/LocalizationService";

interface ProductListProps {
    products: Product[];
    userAllergens?: Allergen[];
    productType: ProductType;
    textFilter: string;
    onSelectProduct: (product: Product) => void;
    onRefreshProductList: () => void;
    onAddToCart: (product: Product) => void;
}
interface ProductListState {
    products: Map<string, Product[]>;
}
export class ProductList extends React.Component<ProductListProps, ProductListState> {

    constructor(props: ProductListProps) {
        super(props);
        this.state = {products: new Map<string, Product[]>()}
    }

    UNSAFE_componentWillReceiveProps(props: ProductListProps) {
        if(this.props.textFilter != props.textFilter || props.products != this.props.products) {
            this.filter(props.products, props.textFilter);
        }
    }

    filter(products: Product[], textFilter: string) {
        this.setState({products: ProductService.filterAndGroupByCategory(products, this.props.productType, textFilter)})
    }

    render() {
        const firstSection = this.props.products.keys().next().value;
        return <SectionList 
            style={{backgroundColor:'#FFF'}}
            sections={this.getSectionListData(this.state.products)}
            renderSectionHeader={({section}) => 
                <View style={[productListHeaderStyle.container, section.title == firstSection ? {borderTopWidth:0, marginTop: 10} : {}]}>
                    <Text style={productListHeaderStyle.text}>{section.title}</Text>
                </View>
            }
            refreshControl={<RefreshControl refreshing={false} onRefresh={this.props.onRefreshProductList}/>}
            extraData={this.state.products}
            renderItem={this.renderItem}
            keyExtractor={(item,index) => item.id + index}
          /> 
    }

    renderItem = (info: ListRenderItemInfo<Product>) => (
        <ProductListItem key={info.item.id + info.index} product={info.item} 
            onSelectProduct={this.props.onSelectProduct} onAddToCart={this.props.onAddToCart} 
            horizontalScroll={true}
            userAllergens={this.props.userAllergens}/>
    );

   
    getSectionListData(input : Map<string, Product[]>) : SectionListData<Product>[] {
        var output = [] as SectionListData<Product>[];
        var recomendationsSectionTitle = msg("products:recommendations");
        var recommendedProducts = new Array<Product>();
        output.push({title: recomendationsSectionTitle, data:recommendedProducts});
        input.forEach((value:Product[], key:string) => {
            value.filter(p => p.recommended).forEach(p => recommendedProducts.push(p));
            output.push({title: key, data:value} as SectionListData<Product>);
        });
        if(recommendedProducts.length == 0) {
            output.shift();
        }
        return output;
    }

    renderSectionHeader(info: { section: SectionListData<Product[]> }) :  React.ReactElement<any> {
        return <Text style={{fontWeight: 'bold', color:'gray'}}>{info.section.key}</Text>
    }
}