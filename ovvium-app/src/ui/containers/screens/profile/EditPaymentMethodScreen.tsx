import React from "react";
import { Alert, View } from 'react-native';
import { connect } from 'react-redux';
import { AnyAction, Dispatch } from 'redux';
import { NavigationProp, RouteProp, Route } from "@react-navigation/core";
import { PaymentMethod } from '../../../../model/PaymentMethod';
import { msg } from '../../../../services/LocalizationService';
import { AppState } from '../../../../store/State';
import { EditPaymentMethodView } from '../../../components/EditPaymentMethodView/EditPaymentMethodView';
import { headerStyles } from '../../../components/Header/style';
import { AppScreens } from '../../../navigation/AppScreens';
import { baseMapDispatchToProps, baseMapStateToProps, BaseScreen, BaseScreenProps, BaseScreenState } from '../BaseScreen';
import { savePaymentMethodActionCreator, deletePaymentMethodActionCreator } from '../../../../actions/UserProfileActions';

interface EditPaymentMethodScreenProps extends BaseScreenProps {
  navigation: NavigationProp<any>;
  route: Route<string>;
  savePaymentMethod: (paymentMethod: PaymentMethod) => void;
  deletePaymentMethod: (paymentMethod: PaymentMethod) => void;
}

interface EditPaymentMethodScreenState extends BaseScreenState{
  confirmInterceptor?: () => void;
}

class EditPaymentMethodScreen extends BaseScreen<EditPaymentMethodScreenProps, EditPaymentMethodScreenState> {

  constructor(props: EditPaymentMethodScreenProps) {
    super(props, {});
  }

  static navigationOptions = {
    header: (
      <View style={headerStyles.emptyHeaderContainer}/>
    )
  };

  render() {
    let params = this.props.route.params!;
    return <EditPaymentMethodView
              paymentMethod={params['paymentMethod']}
              addRunConfirmInterceptor={interceptor => this.setState({confirmInterceptor: interceptor})}
              editable={params['isNewPaymentMethod']}
              onSaveButtonPressed={() => this.state.confirmInterceptor!()}
              onSavePaymentMethod={this.onSavePaymentMethod.bind(this)}
              onDeletePaymentMethod={(paymentMethod: PaymentMethod) => this.deletePaymentMethod(paymentMethod)}
              goBack={() => this.props.navigation.navigate(AppScreens.PaymentMethods)}
            />
  }

  onSavePaymentMethod(paymentMethod: PaymentMethod) {
      this.props.savePaymentMethod(paymentMethod);
      this.props.navigation.navigate(AppScreens.PaymentMethods);
  }

  deletePaymentMethod(paymentMethod: PaymentMethod) {
      Alert.alert(
        msg('profile:paymentMethods:remove:label'),
        msg('profile:paymentMethods:remove:question'),
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
              this.props.deletePaymentMethod(paymentMethod);
              this.props.navigation.navigate(AppScreens.PaymentMethods);
            }
          }
        ],
        { cancelable: false }
      );
  }
}

function mapStateToProps(state: AppState): EditPaymentMethodScreenProps {
  return baseMapStateToProps(state, {
    
  })
}

function mapDispatchToProps(dispatch: Dispatch<AnyAction>) {
  return baseMapDispatchToProps(dispatch, {
    savePaymentMethod: savePaymentMethodActionCreator,
    deletePaymentMethod: deletePaymentMethodActionCreator
  });
}

const EditPaymentMethodContainer = connect(mapStateToProps, mapDispatchToProps)(EditPaymentMethodScreen);

export { EditPaymentMethodContainer };

