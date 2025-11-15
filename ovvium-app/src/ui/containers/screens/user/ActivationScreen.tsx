import React from "react";
import { connect } from "react-redux";
import { AnyAction, Dispatch } from "redux";
import { NavigationProp, RouteProp, Route } from "@react-navigation/core";
import { activate } from '../../../../actions/UserActions';
import { Session } from '../../../../model/Session';
import { User } from '../../../../model/User';
import { AppState } from "../../../../store/State";
import { StringUtils } from '../../../../util/StringUtils';
import { AppScreens } from "../../../navigation/AppScreens";
import { baseMapDispatchToProps, baseMapStateToProps, BaseScreen, BaseScreenProps, BaseScreenState } from '../BaseScreen';
import { ActivationView } from '../../../components/ActivationView/ActivationView';

interface ActivationScreenProps extends BaseScreenProps {
  user?: User;
  session?: Session;
  navigation: NavigationProp<any>;
  route: Route<string>;
  activate: (email: string, activationCode: string) => void;
}

interface ActivationScreenState extends BaseScreenState {
  showIndicator: boolean;
  activationError: boolean;
}

export class ActivationScreen extends BaseScreen<ActivationScreenProps, ActivationScreenState> {
  static navigationOptions = {
    header: null
  };

  constructor(props: ActivationScreenProps) {
    super(props, {showIndicator: false, activationError: false, errorDialogOpened: false});
  }
  
  UNSAFE_componentWillMount() {
    this.processActivation(this.props);
  }

  UNSAFE_componentWillReceiveProps(props: ActivationScreenProps) {
    this.processActivation(props);
  }

  render() {
    return <ActivationView showIndicator={this.state.showIndicator} email={this.props.user ? this.props.user.email : ""} error={this.state.activationError}/>;
  }

  processActivation(props: ActivationScreenProps) {
    let params = this.props.route.params!;
    var email = params['email'];
    var activationCode = params['activationCode'];
    if(StringUtils.isNotBlank(email) && StringUtils.isNotBlank(activationCode)) {
      this.setState({showIndicator: true, activationError: false});
      this.props.activate(email, activationCode);
      this.props.navigation.setParams({
        email: undefined,
        activationCode: undefined
      })
    } else if(props.error) {
      this.setState({activationError: true});
    }
    this.props.clearError(this.props.screen);

    if(props.user && props.session && props.user.enabled && props.session.accessToken) {
        this.props.navigation.navigate(AppScreens.JoinBill);
    }
  }
}

function mapStateToProps(state: AppState): ActivationScreenProps {
  return baseMapStateToProps(state, {
    user: state.sessionState.user,
    session: state.sessionState.session,
    screen: AppScreens.Activation
  } as ActivationScreenProps);
}

function mapDispatchToProps(dispatch: Dispatch<AnyAction>) {
  return baseMapDispatchToProps(dispatch,
    {
     activate: activate
    }
    );
}

const ActivationContainer = connect(
  mapStateToProps,
  mapDispatchToProps
)(ActivationScreen);
export { ActivationContainer };

