import React from "react";
import { ActivityIndicator, View } from "react-native";
import { AppColors } from '../../styles/layout/AppColors';
import { loadingViewStyles } from './style';

export class LoadingView extends React.Component {
  render() {
    return (
      <View style={loadingViewStyles.container}>
        <ActivityIndicator color={AppColors.main} size="large"/>
      </View>
    );
  }
} 
