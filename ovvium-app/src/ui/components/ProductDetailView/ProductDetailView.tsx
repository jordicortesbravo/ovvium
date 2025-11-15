import React from "react";
import { ActivityIndicator, Dimensions, Platform, ScrollView, Text, View, Image, TouchableOpacity } from "react-native";
import { Picture } from '../../../model/Picture';
import { Product } from "../../../model/Product";
import { msg } from "../../../services/LocalizationService";
import { ArrayUtils } from '../../../util/ArrayUtils';
import { AppColors } from '../../styles/layout/AppColors';
import { bodyStyles } from "../../styles/layout/BodyStyle";
import AndroidFloatingButton from '../AndroidFloatingButton/AndroidFloatingButton';
import { Carousel } from '../Carousel/Carousel';
import { GoBackIcon } from '../GoBackIcon/GoBackIcon';
import { IconButton } from '../IconButton/IconButton';
import { LoadingView } from '../LoadingView/LoadingView';
import MultifamilyIcon, { IconFamily } from '../MultiFamilyIcon/MultifamilyIcon';
import { RatingList } from '../RatingList/RatingList';
import { StarRatingRow, StarRatingsMultiple } from '../StarRatingsMultiple/StarRatingsMultiple';
import { productDetailStyles } from './style';
import { withStaticsBaseUrl } from "../../../actions/BaseAction";
import { TotalRating } from "../../../model/TotalRating";
import { Rating } from "../../../model/Rating";
import { RatingsPageResponse } from "../../../model/response/RatingsPageResponse";
import { RatingResponse } from "../../../model/response/RatingResponse";
import { Crop } from "../../../model/enum/Crop";
import { AppFonts } from "../../styles/layout/AppFonts";

interface ProductDetailViewProps {
	product: Product;
	totalRatings?: TotalRating[];
	ratings?: RatingsPageResponse;
	uploadingImage: boolean;
	goBack: () => void;
	goToRateView: () => void;
	onAddToCart: () => void;
	onUploadPhoto: () => void;
}

export class ProductDetailView extends React.Component<ProductDetailViewProps> {

	render() {
		var pictures = this.props.product.pictures && this.props.product.pictures.length > 0 ? this.props.product.pictures.map(p => p[Crop.MEDIUM]) : this.props.product.coverPicture ? [this.props.product.coverPicture[Crop.MEDIUM]] : [];
		return (
			<View>
				<ScrollView style={bodyStyles.container}>
					<View>
						<TouchableOpacity onPress={this.props.goBack} style={{ zIndex: 10, position: 'absolute', top: Platform.OS == 'ios' ? 45 : 30, left: Platform.OS == 'ios' ? 20 : 15, width: 36, borderRadius: 20, backgroundColor: AppColors.white, elevation: 2, padding: 5 }}>
							<View style={{ flexDirection: 'row' }}>
								<MultifamilyIcon family={IconFamily.MATERIAL_COMMUNITY} name="arrow-left" size={25} color={Platform.OS == 'ios' ? AppColors.gray : AppColors.secondaryText} />
							</View>
						</TouchableOpacity>
						{this.props.uploadingImage && 
							<View style={{height: Dimensions.get('screen').height * 0.45, backgroundColor: AppColors.gray, justifyContent: 'center', alignItems:'center'}}>
								<ActivityIndicator size="large"/>
								<Text style={{fontFamily: AppFonts.regular, color: AppColors.white, marginTop:10}}>{msg("products:actions:uploadingPhoto")}</Text>
							</View>
						}
						{!this.props.uploadingImage && <Carousel pictures={pictures} imageStyle={productDetailStyles.imageContainer} showPhotoButton={ArrayUtils.isEmpty(pictures)} />}
					</View>
					<View style={productDetailStyles.buttonsBarContainer}>
						<IconButton
							icon="camera"
							family={IconFamily.FEATHER}
							onPress={this.props.onUploadPhoto}
							title={msg("products:actions:uploadPhoto")}
						/>
						{Platform.OS == 'android' && <View style={{ borderLeftWidth: 0.5, borderColor: AppColors.separator, height: 50 }} />}
						<IconButton
							icon="star"
							family={IconFamily.FEATHER}
							onPress={this.props.goToRateView}
							title={msg("products:actions:comment")}
						/>
						{Platform.OS == 'android' && <View style={{ borderLeftWidth: 0.5, borderColor: AppColors.separator, height: 50 }} />}
						<IconButton
							icon="shopping-cart"
							family={IconFamily.FEATHER}
							onPress={this.props.onAddToCart}
							title={msg("products:actions:ask")}
						/>
					</View>
					<View style={productDetailStyles.descriptionContainer}>
						<View style={productDetailStyles.titleContainer}>
							<View style={{ width: "78%" }}>
								<Text style={productDetailStyles.titleText}>{this.props.product.name}</Text>
							</View>
							<View style={{ width: "22%", alignItems: "flex-end" }}>
								<Text style={productDetailStyles.priceText}>
									{this.props.product.price.toFixed(2) + "€"}
								</Text>
							</View>
						</View>
						<Text style={productDetailStyles.descriptionText}>{this.props.product.description}</Text>
					</View>
					{this.props.product.allergens && this.props.product.allergens.length > 0 &&
						<View style={productDetailStyles.allergensContainer}>
							<Text style={[productDetailStyles.titleText, { marginBottom: 10 }]}>{msg("profile:allergens:label")}</Text>
							<View style={{ flexDirection: 'row', flexWrap: 'wrap' }}>
								{this.renderAllergens()}
							</View>
						</View>
					}
					<View style={productDetailStyles.ratingsContainer}>
						<Text style={productDetailStyles.ratingsTitle}>{msg("products:ratings:label")}</Text>
						<View style={productDetailStyles.ratingsContent}>
							<View>
								<View style={productDetailStyles.ratingsNumberContainer}>
									<Text style={productDetailStyles.ratingNumber}>
										{this.props.product.rate.toFixed(1)}
									</Text>
									<Text style={productDetailStyles.ratingsText}>{msg("products:ratings.maxRating")}</Text>
								</View>
							</View>
							<View>
								<View style={productDetailStyles.ratingStarsContainer}>
									<StarRatingsMultiple starRatingsConfig={this.getStarRatingsFromTotalRatings()} />
								</View>
								<Text style={{ color: AppColors.listItemDescriptionText, position: 'absolute', right: 0, bottom: -15, fontSize: 13, fontFamily: AppFonts.regular }}>
									{this.props.ratings ? this.props.ratings.numberOfElements + " " + msg("products:ratings.opinions") : ""}
								</Text>
							</View>
						</View>
					</View>
					<View>
						{this.getCommentsOrEmpty()}
					</View>
				</ScrollView>
				{Platform.OS == 'android' && <AndroidFloatingButton iconName="shopping-cart" iconFamily={IconFamily.FEATHER} onPress={this.props.onAddToCart} />}
			</View>
		);
	}

	renderAllergens() {
		if (this.props.product.allergens) {
			return this.props.product.allergens.map((allergen: string) => {
				var uri = withStaticsBaseUrl('/img/allergens/' + allergen.toLowerCase() + '.png');
				return <View style={{ flexDirection: 'row', marginVertical: 3, width: '50%' }}>
					<Image source={{ uri: uri }} style={{ width: 20, height: 20 }} />
					<Text style={{ fontFamily: AppFonts.regular, color: AppColors.mainText, marginLeft: 10 }}>{msg("profile:allergens:items:" + allergen.toLowerCase())}</Text>
				</View>
			});
		}
	}

	getStarRatingsFromTotalRatings(): StarRatingRow[] {
		let totalRatings = new Array<StarRatingRow>();

		if(this.props.totalRatings) {
			let sortedTotalRatings = this.props.totalRatings.sort((r1, r2) => (r1.rating < r2.rating ? 1 : -1));
	
			for (let totalRating of sortedTotalRatings) {
				totalRatings.push({
					selectedStars: totalRating.rating,
					progressPercentage: totalRating.percentage / 100
				} as StarRatingRow);
			}
		} else {
			[5,4,3,2,1].forEach(i => totalRatings.push({
				selectedStars: i,
				progressPercentage: 0 
			} as StarRatingRow));
		}
		return totalRatings;
	}

	private getCommentsOrEmpty() {
		if (!this.props.ratings)
			return (<ActivityIndicator size="small" color={AppColors.main} />)
		if (this.props.ratings && this.props.ratings.numberOfElements == 0)
			return this.getEmptyCommentsMessage()
		else
			return (<RatingList ratings={this.props.ratings.ratings.map(ratingResponse => Rating.from(ratingResponse))} />)
	}

	private getEmptyCommentsMessage(): JSX.Element | undefined {
		return <View style={{ width: '100%', justifyContent: 'center', alignItems: 'center', marginTop: 20 }}>
			<Text style={{ paddingHorizontal: 15, color: AppColors.listItemDescriptionText }}>{msg("products:ratings.emptyComments")}</Text>
		</View>
	}
}
