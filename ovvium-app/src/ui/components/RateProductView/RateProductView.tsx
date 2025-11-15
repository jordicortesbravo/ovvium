import React from "react";
import { Platform, ScrollView, View } from 'react-native';
import { Product } from '../../../model/Product';
import { UserRating } from '../../../model/UserRating';
import { msg } from '../../../services/LocalizationService';
import { ProductService } from '../../../services/ProductService';
import { errorMessage } from '../../../util/WidgetUtils';
import { AppColors } from '../../styles/layout/AppColors';
import { bodyStyles } from '../../styles/layout/BodyStyle';
import AndroidFloatingButton from '../AndroidFloatingButton/AndroidFloatingButton';
import { Header } from '../Header/Header';
import { Input } from '../Input/Input';
import { StarRatings } from '../StarRatings/StarRatings';
import { ImageWithPlaceholder } from '../ImageWithPlaceholder/ImageWithPlaceholder';
import { IconFamily } from '../MultiFamilyIcon/MultifamilyIcon';
import { rateProductViewStyle } from './style';
import { KeyboardAwareScrollView } from "react-native-keyboard-aware-scroll-view";
import { AppFonts } from "../../styles/layout/AppFonts";

interface RateProductViewProps {
    product: Product;
    rate: (rating: UserRating) => void;
    goBack: () => void;
    openPickPhoto: () => void;
}

interface RateProductViewState {
    rating: UserRating;
    commenting: boolean;
}

export class RateProductView extends React.Component<RateProductViewProps, RateProductViewState> {

    constructor(props: RateProductViewProps) {
        super(props);
        var rating = this.props.product.userRating ? this.props.product.userRating : {} as UserRating;
        if(!rating.rating) {
            rating.rating = 3;
        }
        this.state = { rating, commenting: false }
    }

    render() {
        var imageUri = ProductService.getMediumProductImageUri(this.props.product);
        var input: Input;
        var ratingTexts = ["1","2","3","4","5"].map(star => msg("products:ratings:stars:" + star));
        return (<View  style={bodyStyles.container}>
            <Header goBack={this.props.goBack}
                title={msg("products:ratings:header")}
                actionTitle={msg("actions:send")}
                doAction={this.rate.bind(this)} />
            <KeyboardAwareScrollView endFillColor={AppColors.white}
                onKeyboardDidHide={() => this.setState({ commenting: false })}
                onKeyboardDidShow={() => this.setState({ commenting: true })}>
                <ScrollView>
                    <View style={rateProductViewStyle.imageContainer}>
                        <ImageWithPlaceholder source={imageUri} imageStyle={rateProductViewStyle.image} showPhotoButton={false} showTitle={true} title={this.props.product.name} openPickPhoto={this.props.openPickPhoto} />
                    </View>
                    <View style={{ justifyContent: 'center', alignItems: 'center', height: 120, borderBottomColor: AppColors.separator, borderBottomWidth: 0.5, backgroundColor: AppColors.white }}>
                        <StarRatings maxStars={5}
                            selectedStars={this.state.rating.rating}
                            onSelect={(rating: number) => this.state.rating.rating = rating}
                            selectable={true} withEmptyStars={true}
                            size={40}
                            color={AppColors.ovviumYellow}
                            starTexts={ratingTexts}
                            iconStyle={{ marginHorizontal: 7 }} />
                    </View>
                    <View style={{ backgroundColor: AppColors.white, padding: 10, height: '100%' }}>
                        <Input
                            ref={(_input: Input) => input = _input}
                            placeholder={msg("products:ratings:leaveAComment")}
                            defaultValue={this.props.product.userRating ? this.props.product.userRating.comment : ''}
                            multiline={true}
                            maxLength={500}
                            showValidationErrors={false}
                            onChangeText={(text: string) => this.state.rating.comment = text}
                            containerStyle={{width: "100%"}}
                            style={{ textAlignVertical: "top", marginHorizontal: 10, height: 170, padding: 20, fontFamily: AppFonts.regular, borderWidth: 1, borderColor: '#dfe1e5', borderRadius: 8 }}
                        />
                    </View>
                </ScrollView>
            </KeyboardAwareScrollView>
            {
                Platform.OS == 'android' && !this.state.commenting &&
                <AndroidFloatingButton onPress={() => input.focus()} iconName="pencil-outline" iconFamily={IconFamily.MATERIAL_COMMUNITY} />
            }
        </View >
        )
    }

    rate() {
        if (this.state.rating.rating == 0) {
            errorMessage({ message: "Puntue el producto de 1 a 5 *" });
        } else {
            this.props.rate(this.state.rating);
        }
    }
}
