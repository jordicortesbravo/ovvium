import { NavigationProp } from "@react-navigation/core";
import React from "react";
import { Alert, View, Linking } from "react-native";
import ImagePicker, { ImagePickerOptions } from 'react-native-image-picker';
import { connect } from "react-redux";
import { AnyAction, bindActionCreators, Dispatch } from "redux";
import { logout } from "../../../../actions/UserActions";
import { updateUser, uploadPicture } from "../../../../actions/UserProfileActions";
import { User } from '../../../../model/User';
import { StringUtils } from '../../../../util/StringUtils';
import { headerStyles } from '../../../components/Header/style';
import { ProfileView } from '../../../components/ProfileView/ProfileView';
import { AppScreens } from "../../../navigation/AppScreens";
import { msg } from './../../../../services/LocalizationService';
import { AppState } from "./../../../../store/State";
import { properties } from "../../../../../resources/Properties";

interface ProfileScreenProps {
  user: User;
  navigation: NavigationProp<any>;
  onLogout: () => void;
  uploadPicture: (user: User, image: any) => void;
  updateUser: (user: User) => void; 
}

interface ProfileScreenState {
  user: User;
}


export class ProfileScreen extends React.Component<ProfileScreenProps, ProfileScreenState> {
  static navigationOptions = () => {
    return {
      header: <View style={headerStyles.emptyHeaderContainer}/>
    };
  };

  constructor(props: ProfileScreenProps) {
    super(props);
    this.state = {user:props.user};
  }

  render() {
    return <ProfileView user={this.props.user}
                        onLogout={this.onLogout.bind(this)} 
                        onEditName={this.changeName.bind(this)}
                        onEditPaymentMethods={() => this.props.navigation.navigate(AppScreens.PaymentMethods)}
                        onOpenLegalTexts={() => Linking.openURL(properties.terms.url)}
                        onEditProfile={() => this.props.navigation.navigate(AppScreens.EditProfile)}
            />;
  }

  onLogout() {
    Alert.alert(
      msg('profile:logout.label'),
      msg('profile:logout.confirm'),
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
            this.props.onLogout();
          }
        }
      ],
      { cancelable: false }
    );
  }
  
  changeName(name: string) {
    if(StringUtils.isNotBlank(name)) {
      this.state.user.name = name;
      this.props.updateUser(this.state.user);
    }
  }
}

function mapStateToProps(state: AppState): ProfileScreenProps {
  return {
    user: state.sessionState.user
  } as ProfileScreenProps;
}

function mapDispatchToProps(dispatch: Dispatch<AnyAction>) {
  return bindActionCreators(
    {
      onLogout: logout,
      uploadPicture: uploadPicture,
      updateUser: updateUser
    },
    dispatch
  );
}

const ProfileContainer = connect(
  mapStateToProps,
  mapDispatchToProps
)(ProfileScreen);
export default ProfileContainer;
