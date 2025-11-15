import React from "react";
import { ScrollView, View, Platform } from "react-native";
import { User } from '../../../model/User';
import { AppColors } from '../../styles/layout/AppColors';
import { Header } from '../Header/Header';
import { ImageWithPlaceholder } from '../ImageWithPlaceholder/ImageWithPlaceholder';
import { MenuItem } from '../MenuItem/MenuItem';
import { IconFamily } from '../MultiFamilyIcon/MultifamilyIcon';
import { msg } from "./../../../services/LocalizationService";
import { profileViewStyle } from './style';
import Swipeable from "../../containers/widgets/Swipeable";
import { Tricks } from "../../../model/enum/Tricks";

interface ProfileViewProps {
  user: User; 
  onLogout: () => void;
  onEditPaymentMethods: () => void;
  onEditName: (name: string) => void;
  onOpenLegalTexts: () => void;
  onEditProfile: () => void;
}

interface ProfileViewState {
  name: string;
}

export class ProfileView extends React.Component<ProfileViewProps,ProfileViewState> {

  constructor(props: ProfileViewProps) {
    super(props);
    this.state = {name: props.user.name}
  }

  render() {
    if(!this.props.user) {
      return null;
    }
    return  <View style={profileViewStyle.container}>
              <ScrollView style={{height:'100%'}}>
                <Header title={msg("profile:label")} format="big" subtitle={msg("profile:subtitle")} />
                  <MenuItem iconColor={AppColors.userPlaceholderColors[3].soft} iconTextColor={AppColors.userPlaceholderColors[3].hard} iconFamily={IconFamily.FEATHER} iconName="user" title={msg("profile:edit:title")} subtitle={msg("profile:edit:subtitle")} onPress={this.props.onEditProfile} />
                  <MenuItem iconColor={AppColors.userPlaceholderColors[2].soft} iconTextColor={AppColors.userPlaceholderColors[2].hard} iconFamily={IconFamily.FONT_AWESOME} iconName="credit-card" title={msg("profile:paymentMethods:title")} subtitle={msg("profile:paymentMethods:subtitle")} onPress={this.props.onEditPaymentMethods} />
                  <MenuItem iconColor={AppColors.userPlaceholderColors[7].soft} iconTextColor={AppColors.userPlaceholderColors[7].hard} iconFamily={IconFamily.FONT_AWESOME} iconName="legal" title={msg("profile:terms:label")} subtitle={msg("profile:terms:subtitle")} onPress={this.props.onOpenLegalTexts} />
                  <MenuItem iconColor={AppColors.userPlaceholderColors[8].soft} iconTextColor={AppColors.userPlaceholderColors[8].hard} iconFamily={IconFamily.ION} iconName="ios-power" title={msg("profile:logout:label")} subtitle={msg("profile:logout:subtitle")} onPress={this.props.onLogout} hideArrow={true}/>
              </ScrollView>
              <Swipeable message={msg("onboarding:tricks:profile")} id={Tricks.PROFILE}/>
            </View>
  }
}