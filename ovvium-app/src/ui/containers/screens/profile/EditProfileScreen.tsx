import React from "react";
import { View, Alert } from 'react-native';
import { connect } from 'react-redux';
import { AnyAction, Dispatch } from 'redux';
import { NavigationProp } from "@react-navigation/core";
import { updateUser, uploadPicture } from '../../../../actions/UserProfileActions';
import { User } from '../../../../model/User';
import { AppState } from '../../../../store/State';
import { AppScreens } from '../../../navigation/AppScreens';
import { headerStyles } from '../../../components/Header/style';
import { baseMapDispatchToProps, baseMapStateToProps, BaseScreen, BaseScreenProps } from '../BaseScreen';
import { EditProfileView } from "../../../components/EditProfileView/EditProfileView";
import ImagePicker, { ImagePickerOptions } from 'react-native-image-picker';
import { msg } from "../../../../services/LocalizationService";
import { removeUser } from "../../../../actions/UserActions";

interface EditProfileScreenProps extends BaseScreenProps {
  user: User;
  removeUser: (user: User) => void;
  updateUser: (user: User) => void;
  uploadPicture: (user: User, image: any) => void;
}

class EditProfileScreen extends BaseScreen<EditProfileScreenProps, any> {

  static navigationOptions = {
    header: (
      <View style={headerStyles.emptyHeaderContainer}/>
    )
  };

  render() {
    return <EditProfileView
              user={this.props.user}
              onEditPhoto={this.onEditPhoto.bind(this)}
              onEditAllergens={() => this.props.navigation.navigate(AppScreens.Allergens)}
              onEditFoodPreferences={() => this.props.navigation.navigate(AppScreens.FoodPreferences)}
              onEditName={this.onEditName.bind(this)}
              onChangePassword={() => this.props.navigation.navigate(AppScreens.ChangePassword)}
              onRemoveAccount={this.onRemoveAccount.bind(this)}
              goBack={this.goBack.bind(this)}
            />
  }

  onEditName(name : string) {
    var user = Object.assign({}, this.props.user);
    user.name = name;
    this.props.updateUser(user);
  }

  async onEditPhoto() {
      var options = {mediaType: 'photo'} as ImagePickerOptions;
      ImagePicker.launchImageLibrary(options, (response: any) => {
          if(!response.didCancel) {
            this.props.uploadPicture(this.props.user, {uri: response.uri, type:response.type, name:response.fileName});
          }
      });
  }

  goBack() {
    this.props.navigation.navigate(AppScreens.Profile);
  }

  onRemoveAccount() {
    Alert.alert(
      msg('profile:remove.question'),
      msg('profile:remove.confirm'),
      [
        {
          text:  msg('actions:cancel'),
          onPress: () => {
            return null;
          }
        },
        {
          text: msg('actions:confirm'),
          onPress: () => {
            this.props.removeUser(this.props.user);
          }
        }
      ],
      { cancelable: false }
    );
  }
}

function mapStateToProps(state: AppState): EditProfileScreenProps {
  return baseMapStateToProps(state, {
    user: state.sessionState.user
  })
}

function mapDispatchToProps(dispatch: Dispatch<AnyAction>) {
  return baseMapDispatchToProps(dispatch, {
    uploadPicture: uploadPicture,
    removeUser: removeUser,
    updateUser: updateUser
  });
}

const EditProfileContainer = connect(mapStateToProps, mapDispatchToProps)(EditProfileScreen);

export { EditProfileContainer };

