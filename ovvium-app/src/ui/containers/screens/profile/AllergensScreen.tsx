import React from "react";
import { View } from 'react-native';
import { connect } from 'react-redux';
import { AnyAction, Dispatch } from 'redux';
import { NavigationProp } from "@react-navigation/core";
import { updateUser } from '../../../../actions/UserProfileActions';
import { User } from '../../../../model/User';
import { AppState } from '../../../../store/State';
import { AppScreens } from '../../../navigation/AppScreens';
import { headerStyles } from '../../../components/Header/style';
import { baseMapDispatchToProps, baseMapStateToProps, BaseScreen, BaseScreenProps } from '../BaseScreen';
import { AllergensView } from '../../../components/AllergensView/AllergensView';
import { mapSelectedAllergens } from "../../../../services/UserService";

interface AllergensScreenProps extends BaseScreenProps {
  user: User;
  allergens: Map<string, boolean>;
  navigation: NavigationProp<any>;
  updateUser: (user: User, allergens?: string[]) => void;
}

class AllergensScreen extends BaseScreen<AllergensScreenProps, any> {

  static navigationOptions = {
    header: (
      <View style={headerStyles.emptyHeaderContainer}/>
    )
  };

  render() {
    return <AllergensView
              allergens={this.props.allergens}
              saveAllergens={this.saveAllergens.bind(this)}
              goBack={() => this.props.navigation.goBack()}
            />
  }

  saveAllergens(allergens?: Map<string, boolean>) {
    if(allergens) {
      this.props.updateUser(this.props.user, this.getSelectedAllergens(allergens));
    }
  }

  getSelectedAllergens(allergens?: Map<string, boolean>) {
    if(!allergens) {
      return undefined;
    }
    var selectedAllergens: string[] = [];
    allergens.forEach((selected:boolean, allergen:string) => {
      if(selected) {
        selectedAllergens.push(allergen);
      }
    }) 
    return selectedAllergens;
  }
}

function mapStateToProps(state: AppState): AllergensScreenProps {
  return baseMapStateToProps(state, {
    user: state.sessionState.user,
    allergens: mapSelectedAllergens(state.profileState.allergens)
  })
}

function mapDispatchToProps(dispatch: Dispatch<AnyAction>) {
  return baseMapDispatchToProps(dispatch, {
    updateUser: updateUser
  });
}

const AllergensContainer = connect(mapStateToProps, mapDispatchToProps)(AllergensScreen);

export { AllergensContainer };

