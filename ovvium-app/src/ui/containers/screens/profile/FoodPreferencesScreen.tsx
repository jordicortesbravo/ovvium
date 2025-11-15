import React from "react";
import { View } from 'react-native';
import { connect } from 'react-redux';
import { AnyAction, Dispatch } from 'redux';
import { NavigationProp } from "@react-navigation/core";
import { updateUser } from '../../../../actions/UserProfileActions';
import { User } from '../../../../model/User';
import { mapSelectedFoodPreferences } from '../../../../services/UserService';
import { AppState } from '../../../../store/State';
import { AppScreens } from '../../../navigation/AppScreens';
import { headerStyles } from '../../../components/Header/style';
import { baseMapDispatchToProps, baseMapStateToProps, BaseScreen, BaseScreenProps } from '../BaseScreen';
import { FoodPreferencesView } from '../../../components/FoodPreferencesView/FoodPreferencesView';

interface FoodPreferencesScreenProps extends BaseScreenProps {
  user: User;
  foodPreferences: Map<string, boolean>;
  navigation: NavigationProp<any>;
  updateUser: (user: User, allergens?: string[], foodPreferences?: string[]) => void;
}

class FoodPreferencesScreen extends BaseScreen<FoodPreferencesScreenProps, any> {

  static navigationOptions = {
    header: (
      <View style={headerStyles.emptyHeaderContainer} />
    )
  };

  render() {
    return <FoodPreferencesView
      foodPreferences={this.props.foodPreferences}
      goBack={this.goBack.bind(this)}
      saveFoodPreferences={this.saveFoodPreferences.bind(this)}
    />
  }

  goBack() {
    this.props.navigation.goBack();
  }

  saveFoodPreferences(foodPreferences?: Map<string, boolean>) {
    if (foodPreferences) {
      this.props.updateUser(this.props.user, undefined, this.getSelectedPreferences(foodPreferences));
    }
  }

  getSelectedPreferences(foodPreferences?: Map<string, boolean>) {
    if (!foodPreferences) {
      return undefined;
    }
    var selectedPreferences: string[] = [];
    foodPreferences.forEach((selected: boolean, preference: string) => {
      if (selected) {
        selectedPreferences.push(preference);
      }
    })
    return selectedPreferences;
  }
}

function mapStateToProps(state: AppState): FoodPreferencesScreenProps {
  return baseMapStateToProps(state, {
    user: state.sessionState.user,
    foodPreferences: mapSelectedFoodPreferences(state.profileState.foodPreferences)
  })
}

function mapDispatchToProps(dispatch: Dispatch<AnyAction>) {
  return baseMapDispatchToProps(dispatch, {
    updateUser: updateUser
  });
}

const FoodPreferencesContainer = connect(mapStateToProps, mapDispatchToProps)(FoodPreferencesScreen);

export { FoodPreferencesContainer };

