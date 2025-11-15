import React from 'react';
import { AppState, Image, ListRenderItemInfo, StyleSheet, Text, View, Button, TouchableHighlight } from 'react-native';
import AppIntroSlider from 'react-native-app-intro-slider';
import { connect } from 'react-redux';
import { AnyAction, bindActionCreators, Dispatch } from 'redux';
import { hideOnboardingActionCreator } from '../../../actions/OnboardingActions';
import { msg } from '../../../services/LocalizationService';
import MultifamilyIcon, { IconFamily } from '../../components/MultiFamilyIcon/MultifamilyIcon';
import { AppColors } from '../../styles/layout/AppColors';
import AsyncStorage from '@react-native-community/async-storage';
import * as TestIds from '../../../constants/TestIds';
import testID from '../../../util/TestIdsUtils';
import { AppFonts } from '../../styles/layout/AppFonts';

const styles = StyleSheet.create({
  buttonCircle: {
    width: 40,
    height: 40,
    backgroundColor: AppColors.main,
    borderRadius: 20,
    justifyContent: 'center',
    alignItems: 'center',
  },
});

interface AppOnboardingSliderProps {
  hideOnboarding: () => void;
}

class AppOnboardingSlider extends React.Component<AppOnboardingSliderProps> {
  slide1 = {
    title: msg("onboarding:step1:title"),
    image: <Image width={200} source={require('../../../../assets/images/onboarding/onboarding-1.png')} />,
    description: msg("onboarding:step1:description")
  };

  slide2 = {
    title: msg("onboarding:step2:title"),
    image: <Image width={200} source={require('../../../../assets/images/onboarding/onboarding-2.png')} />,
    description: msg("onboarding:step2:description")
  };

  slide3 = {
    title: msg("onboarding:step3:title"),
    image: <Image source={require('../../../../assets/images/onboarding/onboarding-3.png')} />,
    description: msg("onboarding:step3:description")
  };
  slide4 = {
    title: msg("onboarding:step4:title"),
    image: <Image source={require('../../../../assets/images/onboarding/onboarding-4.png')} />,
    description: msg("onboarding:step4:description")
  };

  slide5 = {
    title: msg("onboarding:step5:title"),
    image: <Image source={require('../../../../assets/images/onboarding/onboarding-5.png')} />,
    description: msg("onboarding:step5:description")
  }

  render() {
    return (
      <AppIntroSlider
        activeDotStyle={{ backgroundColor: AppColors.main }}
        data={[this.slide1, this.slide2, this.slide3, this.slide4, this.slide5]}
        showPrevButton={true}
        showSkipButton={false}
        renderItem={this.renderItem}
        renderDoneButton={this.renderDoneButton}
        renderPrevButton={this.renderBackButton}
        renderNextButton={this.renderNextButton}
        onDone={this.hideOnboarding.bind(this)}
      />
    );
  }

  renderItem = (info: ListRenderItemInfo<any>) => {
    var slide = info.item;
    return (
      <View key={"slide-" + info.index} style={{ backgroundColor: AppColors.white, height: '100%', paddingTop: 150, paddingHorizontal: '5%', alignItems: 'center' }}>
        {slide.image}
        <View style={{ marginTop: 100 }}>
          <Text style={{ fontFamily: AppFonts.bold, color: AppColors.mainText, fontSize: 22, textAlign: 'center' }}>{slide.title}</Text>
          <Text style={{ fontFamily: AppFonts.regular, color: AppColors.listItemDescriptionText, fontSize: 16, textAlign: 'center', marginTop: 10 }}>{slide.description}</Text>
        </View>
      </View>
    );
  }

  renderNextButton = () => {
    return (
      <View style={styles.buttonCircle}
        {...testID(TestIds.ONBOARDING_NEXT)}
      >
        <MultifamilyIcon family={IconFamily.ION} name="md-arrow-round-forward" size={24} color="rgba(255, 255, 255, .9)" />
      </View>
    );
  };

  renderBackButton = () => {
    return (
      <View style={styles.buttonCircle}
      {...testID(TestIds.ONBOARDING_BACK)}>
        <MultifamilyIcon family={IconFamily.ION} name="md-arrow-round-back" size={24} color="rgba(255, 255, 255, .9)" />
      </View>
    );
  };

  renderDoneButton = () => {
    return (
      <View style={styles.buttonCircle}
      {...testID(TestIds.ONBOARDING_DONE)}
      >
        <MultifamilyIcon family={IconFamily.ION} name="md-checkmark" size={24} color="rgba(255, 255, 255, .9)" />
      </View>
    );
  };

  hideOnboarding() {
    AsyncStorage.setItem("hideOnboarding", "true");
    this.props.hideOnboarding();
  }
}

function mapStateToProps(state: AppState): AppOnboardingSliderProps {
  return {} as AppOnboardingSliderProps;
}

function mapDispatchToProps(dispatch: Dispatch<AnyAction>) {
  return bindActionCreators(
    {
      hideOnboarding: hideOnboardingActionCreator
    },
    dispatch
  );
}

export default connect(
  mapStateToProps,
  mapDispatchToProps
)(AppOnboardingSlider);
