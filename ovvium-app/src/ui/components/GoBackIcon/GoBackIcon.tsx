import React from "react";
import { TouchableHighlight, Platform, Text, View, ViewStyle, TextStyle } from "react-native";
import MateriaComunitylIcons from "react-native-vector-icons/MaterialCommunityIcons";
import { AppColors } from '../../styles/layout/AppColors';
import { goBackIconStyles } from './style'

interface GoBackIconProps {
  goBack: () => void;
  label?: string;
  containerStyle?: ViewStyle;
  textStyle?: TextStyle;
}

export class GoBackIcon extends React.Component<GoBackIconProps> {
  render() {
      return  (
        <TouchableHighlight underlayColor={Platform.OS == 'ios' ? AppColors.white : AppColors.touchableOpacity}  style={[goBackIconStyles.container, this.props.containerStyle]} onPress={() => this.props.goBack()}>
          {Platform.OS == 'ios' ? this.renderIos() : this.renderAndroid()}
        </TouchableHighlight>
      );
    }

    renderIos() {
      return  <View style={{flexDirection: "row"}} >
                <MateriaComunitylIcons style={{marginBottom:5}}  name="chevron-left" size={34} color={AppColors.ovviumYellow} />
                {this.props.label &&
                  <Text style={{color: AppColors.ovviumYellow, marginTop: 5, fontSize:18}}>{this.props.label}</Text>
                }
              </View>
    }

    renderAndroid() {
      return <MateriaComunitylIcons name="arrow-left" size={28} color={AppColors.backButton} />
    }
}
