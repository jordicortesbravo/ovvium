import React from "react";
import { ScrollView, View, Platform, TouchableOpacity, Dimensions, Text, TextInput } from 'react-native';
import { msg } from '../../../services/LocalizationService';
import { AppColors } from '../../styles/layout/AppColors';
import { MenuItem } from '../MenuItem/MenuItem';
import MultifamilyIcon, { IconFamily } from '../MultiFamilyIcon/MultifamilyIcon';
import { bodyStyles } from "../../styles/layout/BodyStyle";
import { User } from "../../../model/User";
import { ImageWithPlaceholder } from "../ImageWithPlaceholder/ImageWithPlaceholder";
import { AppFonts } from "../../styles/layout/AppFonts";
import { Icon } from "react-native-elements";
import { editProfileViewStyles } from "./style";

interface EditProfileViewProps {
    user: User;
    onEditPhoto: () => void;
    onEditAllergens: () => void;
    onEditFoodPreferences: () => void;
    onEditName: (name: string) => void;
    onRemoveAccount: () => void;
    onChangePassword: () => void;
    goBack: () => void;
}

interface EditProfileViewState {
    name: string;
}

export class EditProfileView extends React.Component<EditProfileViewProps, EditProfileViewState> {

    constructor(props: EditProfileViewProps) {
        super(props);
        this.state = {
            name: props.user.name
        }
    }

    render() {
        const headerHeight = Dimensions.get('screen').height * 0.35;
        return <ScrollView style={bodyStyles.container}>
            <View style={{ height: headerHeight }}>
                <TouchableOpacity onPress={this.props.goBack} style={{ zIndex: 10, position: 'absolute', top: Platform.OS == 'ios' ? 45 : 30, left: Platform.OS == 'ios' ? 20 : 15, width: 36, borderRadius: 20, backgroundColor: AppColors.white, elevation: 2, padding: 5 }}>
                    <View style={{ flexDirection: 'row' }}>
                        <MultifamilyIcon family={IconFamily.MATERIAL_COMMUNITY} name="arrow-left" size={25} color={Platform.OS == 'ios' ? AppColors.gray : AppColors.secondaryText} />
                    </View>
                </TouchableOpacity>
                <View style={{ backgroundColor: 'rgba(0,0,0,0.75)', width: '100%', height: '100%', justifyContent: "center", alignItems: "center" }}>
                    <ImageWithPlaceholder source={this.props.user.imageUri}
                        imageStyle={{ width: 120, height: 120, borderRadius: 60, marginTop: 40, backgroundColor: AppColors.listItemDescriptionText }} imagePlaceholderSize={30}
                        showPhotoButton={false} showTitle={false} showPickPhotoPlaceholder={false} touchable={true} asBackground={false}
                        openPickPhoto={this.props.onEditPhoto} />
                    <View style={editProfileViewStyles.searchSection}>
                        <TextInput
                            style={editProfileViewStyles.input}
                            defaultValue={this.state.name}
                            autoCapitalize="none"
                            autoCorrect={false}
                            placeholderTextColor={AppColors.white}
                            returnKeyType="next"
                            selectionColor={AppColors.ovviumYellow}
                            underlineColorAndroid="transparent"
                            placeholder={msg("login:name")}
                            onChangeText={(name: string) => this.setState({ name })}
                            onBlur={() => this.props.onEditName(this.state.name)}
                        />
                        <Icon style={editProfileViewStyles.searchIcon} name="edit" size={20} color={AppColors.white} />
                    </View>
                </View>
            </View>
            <MenuItem iconColor={AppColors.userPlaceholderColors[1].soft} iconTextColor={AppColors.userPlaceholderColors[1].hard} iconFamily={IconFamily.FONT_AWESOME} iconName="pagelines" title={msg("profile:allergens:label")} subtitle={msg("profile:allergens:subtitle")} onPress={this.props.onEditAllergens} />
            <MenuItem iconColor={AppColors.userPlaceholderColors[4].soft} iconTextColor={AppColors.userPlaceholderColors[4].hard} iconFamily={IconFamily.ION} iconName="ios-checkbox-outline" title={msg("profile:foodPreferences:label")} subtitle={msg("profile:foodPreferences:subtitle")} onPress={this.props.onEditFoodPreferences} />
            <MenuItem iconColor={AppColors.userPlaceholderColors[3].soft} iconTextColor={AppColors.userPlaceholderColors[3].hard} iconFamily={IconFamily.MATERIAL_COMMUNITY} iconName="key-change" title={msg("login:changePassword:title")} subtitle={msg("login:changePassword:subtitle")} onPress={this.props.onChangePassword} />
            <MenuItem iconColor={AppColors.userPlaceholderColors[0].soft} iconTextColor={AppColors.userPlaceholderColors[0].hard} hideArrow={true} iconFamily={IconFamily.FEATHER} iconName="trash-2" title={msg("profile:remove:title")} subtitle={msg("profile:remove:subtitle")} onPress={this.props.onRemoveAccount} />
        </ScrollView>
    }
}
