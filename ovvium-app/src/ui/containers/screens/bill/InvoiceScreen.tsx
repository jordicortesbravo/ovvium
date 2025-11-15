import { RouteProp, NavigationProp } from "@react-navigation/core";
import React from 'react';
import { View } from 'react-native';
import { connect } from 'react-redux';
import { AnyAction, bindActionCreators, Dispatch } from 'redux';
import { AppState } from '../../../../store/State';
import { headerStyles } from '../../../components/Header/style';
import InvoiceView from '../../../components/InvoiceView/InvoiceView';


interface InvoiceScreenProps {
    navigation: NavigationProp<any>;
    route: RouteProp<any, any>;
}

class InvoiceScreen extends React.Component<InvoiceScreenProps> {

  static navigationOptions = {
    header: (
      <View style={headerStyles.emptyHeaderContainer}/>
    )
  };

  render() {
      var invoice = this.props.route.params['invoice'];
    return <InvoiceView goBack={this.props.navigation.goBack} invoice={invoice} /> 
  }

}

function mapStateToProps(state: AppState): InvoiceScreenProps {
  return {} as InvoiceScreenProps;
}

function mapDispatchToProps(dispatch: Dispatch<AnyAction>) {
  return bindActionCreators(
    {
    },
    dispatch
  );
}

const InvoiceContainer = connect(
  mapStateToProps,
  mapDispatchToProps
)(InvoiceScreen);

export default InvoiceContainer;


