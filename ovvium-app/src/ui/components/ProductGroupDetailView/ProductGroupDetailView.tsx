import CheckBox from '@react-native-community/checkbox';
import React from "react";
import { Dimensions, ListRenderItemInfo, Platform, ScrollView, SectionList, SectionListData, Text, TextInput, TouchableOpacity, View } from "react-native";
import { KeyboardAwareScrollView } from 'react-native-keyboard-aware-scroll-view';
import { Allergen } from "../../../model/enum/Allergen";
import { Crop } from "../../../model/enum/Crop";
import { ProductType } from "../../../model/enum/ProductType";
import { ServiceTime } from "../../../model/enum/ServiceTime";
import { Order } from '../../../model/Order';
import { OrderGroupChoice } from '../../../model/OrderGroupChoice';
import { Picture } from '../../../model/Picture';
import { Product } from "../../../model/Product";
import { ProductGroup } from "../../../model/ProductGroup";
import { msg } from "../../../services/LocalizationService";
import { ArrayUtils } from '../../../util/ArrayUtils';
import { AppColors } from '../../styles/layout/AppColors';
import { AppFonts } from "../../styles/layout/AppFonts";
import { bodyStyles } from "../../styles/layout/BodyStyle";
import { Button } from '../Button/Button';
import { Carousel } from '../Carousel/Carousel';
import MultifamilyIcon, { IconFamily } from '../MultiFamilyIcon/MultifamilyIcon';
import { productListHeaderStyle } from "../ProductList/style";
import { ProductListItem } from "../ProductListitem/ProductListItem";
import { productGroupDetailStyles } from "./style";

class ClassifiedProduct extends Product {

	serviceTime: ServiceTime;

	constructor(product: Product, serviceTime: ServiceTime) {
		super(product);
		this.serviceTime = serviceTime;
	}
}

interface ProductGroupDetailViewProps {
	product: ProductGroup;
	userAllergens?: Allergen[];
	selectedProductInsideGroup?: ClassifiedProduct;
	onAddOrderToCart: (order: Order) => void;
	goBack: () => void;
	goToProductDetail: (product: Product) => void;
}

interface ProductGroupDetailViewState {
	pictures: Picture[];
	selectedProducts: Map<ServiceTime, Product>;
	serviceTimeList: ServiceTime[];
	notes?: string;
	keyboardShown: boolean;
}

export class ProductGroupDetailView extends React.Component<ProductGroupDetailViewProps, ProductGroupDetailViewState> {

	constructor(props: ProductGroupDetailViewProps) {
		super(props);
		this.state = {
			pictures: [],
			selectedProducts: new Map<ServiceTime, Product>(),
			serviceTimeList: [],
			keyboardShown: false
		};
	}

	static getDerivedStateFromProps(nextProps: ProductGroupDetailViewProps, previousState: ProductGroupDetailViewState) {
		var pictures = nextProps.product.pictures ? nextProps.product.pictures.map(p => p[Crop.MEDIUM]) : nextProps.product.coverPicture ? [nextProps.product.coverPicture] : [];
		var selectedProducts = previousState != undefined && previousState.selectedProducts != undefined ? previousState.selectedProducts : new Map<ServiceTime, Product>();
		var serviceTimeList = Array.from(nextProps.product.products.keys());
		if (nextProps.selectedProductInsideGroup) {
			selectedProducts.set(nextProps.selectedProductInsideGroup.serviceTime, nextProps.selectedProductInsideGroup);
		}
		return { pictures, selectedProducts, serviceTimeList }
	}

	render() {
		var productGroupCompleted = this.state.serviceTimeList.length != 0 &&
			this.state.serviceTimeList.length == Array.from(this.state.selectedProducts.keys()).length;
		return (
			<KeyboardAwareScrollView endFillColor={AppColors.white} onKeyboardDidHide={() => this.setState({ keyboardShown: false })} onKeyboardDidShow={() => this.setState({ keyboardShown: true })}>
				<ScrollView style={bodyStyles.container}>
					<View>
						<TouchableOpacity onPress={this.props.goBack} style={{ zIndex: 10, position: 'absolute', top: Platform.OS == 'ios' ? 45 : 30, left: Platform.OS == 'ios' ? 20 : 15, width: 36, borderRadius: 20, backgroundColor: AppColors.white, elevation: 2, padding: 5 }}>
							<View style={{ flexDirection: 'row' }}>
								<MultifamilyIcon family={IconFamily.MATERIAL_COMMUNITY} name="arrow-left" size={25} color={Platform.OS == 'ios' ? AppColors.gray : AppColors.secondaryText} />
							</View>
						</TouchableOpacity>
						<Carousel pictures={this.state.pictures} height={250} imageStyle={productGroupDetailStyles.imageContainer} showPhotoButton={ArrayUtils.isEmpty(this.state.pictures)} />
					</View>
					<View style={productGroupDetailStyles.descriptionContainer}>
						<View style={productGroupDetailStyles.titleContainer}>
							<View style={{ width: "78%" }}>
								<Text style={productGroupDetailStyles.titleText}>{this.props.product.name}</Text>
							</View>
							<View style={{ width: "22%", alignItems: "flex-end" }}>
								<Text style={productGroupDetailStyles.priceText}>
									{this.props.product.price.toFixed(2) + "€"}
								</Text>
							</View>
						</View>
						<Text style={productGroupDetailStyles.descriptionText}>{this.props.product.description}</Text>
					</View>
					<View style={{ marginVertical: 15 }}>
						<Text style={{ fontFamily: AppFonts.bold, fontSize: 16, color: AppColors.mainText, paddingLeft: 20 }}>{msg("products:configure:title")}</Text>
						<Text style={{ fontFamily: AppFonts.regular, fontSize: 14, color: AppColors.listItemDescriptionText, paddingLeft: 20, marginTop: 10 }}>{msg("products:configure:pick")}</Text>
					</View>
					<View style={{ marginBottom: 30 }}>
						<SectionList
							style={{ backgroundColor: '#FFF' }}
							sections={this.getSectionListData(this.props.product)}
							renderSectionHeader={({ section }) =>
								<View style={[productListHeaderStyle.container, { backgroundColor: AppColors.focusOutGrey }]}>
									<Text style={[productListHeaderStyle.text, { color: 'white' }]}>{section.title}</Text>
								</View>
							}
							renderItem={this.renderItem.bind(this)}
							keyExtractor={(item, index) => item.id + index}
						/>
						{this.renderQuestion(msg("products:configure:notes:question"))}
						<TextInput
							value={this.state.notes}
							onChangeText={(notes: string) => this.setState({ notes })}
							maxLength={200}
							placeholder={msg("products:configure:notes:examples")}
							style={{ paddingHorizontal: 20, marginHorizontal: 10, height: 60, fontFamily: AppFonts.regular, borderWidth: 1, borderColor: '#dfe1e5', borderRadius: 8 }}
						/>
					</View>
				</ScrollView>
				{!this.state.keyboardShown &&<View style={{ alignItems: 'center', width: '100%', backgroundColor: 'white', height: 80, justifyContent: 'center' }}>
					<Button label={msg("bill:cart:add")} onPress={this.onAddOrderToCart.bind(this)} disabled={!productGroupCompleted} />
				</View>}
			</KeyboardAwareScrollView >
		);
	}

	renderItem(info: ListRenderItemInfo<ClassifiedProduct>) {

		var selectedProducts = this.state.selectedProducts;
		var selected = selectedProducts.get(info.item.serviceTime) != undefined && selectedProducts.get(info.item.serviceTime)!.id == info.item.id;

		return <ProductListItem key={info.item.id + info.index + "productGroupItem"} product={info.item} horizontalScroll={false}
			hidePrice={true} onSelectProduct={() => this.props.goToProductDetail(info.item)} userAllergens={this.props.userAllergens}
			touchableWidth={Dimensions.get("screen").width - 100}
			rightWidget={
				<CheckBox
					disabled={false}
					value={selected}
					onValueChange={(value: boolean) => {
						if (value) {
							selectedProducts.set(info.item.serviceTime, info.item);
						} else {
							selectedProducts.delete(info.item.serviceTime);
						}
						this.setState({ selectedProducts });
					}}
				/>
			}
		/>
	}

	renderQuestion(question: string) {
		return <View style={{ paddingVertical: 20, paddingHorizontal: 20 }}>
			<Text style={{ fontFamily: AppFonts.regular, color: AppColors.listItemDescriptionText }}>{question}</Text>
		</View>
	}

	onAddOrderToCart() {
		var order = {
			product: this.props.product,
			price: this.props.product.price,
			notes: this.state.notes,
			choices: Array.from(this.state.selectedProducts.values()).map(p => new OrderGroupChoice(p))
		} as Order;
		this.props.onAddOrderToCart(order);
	}

	getSectionListData(productGroup: ProductGroup): SectionListData<ClassifiedProduct>[] {
		var output = [] as SectionListData<ClassifiedProduct>[];
		productGroup.products.forEach((products: Product[], serviceTime: ServiceTime) => {
			output.push({
				title: serviceTime == ServiceTime.SOONER ? msg("products:type:drink") : msg("products:serviceTime:" + serviceTime),
				data: products.map(p => new ClassifiedProduct(p, serviceTime))
			} as SectionListData<ClassifiedProduct>);
		});
		return output;
	}

	getDrinks() {
		var drinks = new Array<Product>();
		if (this.props.product.products) {
			var products = this.props.product.products.values();

			for (let productsByServiceTime of products) {
				productsByServiceTime
					.filter(p => p.type == ProductType.DRINK)
					.forEach(p => drinks.push(p));
			}
		}
		return drinks;
	}


}

