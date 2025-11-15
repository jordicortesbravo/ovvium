import React from "react";
import { connect } from "react-redux";
import { AnyAction, bindActionCreators, Dispatch } from "redux";
import { hideOnboardingActionCreator } from "../../actions/OnboardingActions";
import { Bill } from "../../model/Bill";
import { BillStatus } from "../../model/enum/BillStatus";
import { Session } from "../../model/Session";
import { AppState } from "../../store/State";
import { DateUtils } from "../../util/DateUtils";
import { AppNavigator } from "./AppNavigator";
import { LoginNavigator } from "./LoginNavigator";
import { OnboardingNavigator } from "./OnboardingNavigator";
import AsyncStorage from "@react-native-community/async-storage";

interface RootNavigatorProps {
    session?: Session;
    bill?: Bill;
    showOnboarding: boolean;

    hideOnboarding: () => void;
}

interface RootNavigatorState {
    session?: Session;
    joinedToBill: boolean;
    showOnboarding: boolean;
}

//TODO: DESCUBRIR SI ESTO SE NECESITA DESPUÉS DEL REFACTOR 
//initNavigationInterceptor(RootNavigator.router);


class RootNavigator extends React.Component<RootNavigatorProps, RootNavigatorState> {

    constructor(props: RootNavigatorProps) {
        super(props);
        this.state = {session: props.session,  joinedToBill: false, showOnboarding: props.showOnboarding};
    }

    componentDidMount() {
        if(this.props.showOnboarding) {
            AsyncStorage.getItem("hideOnboarding")
            .then(item => {
                if(item == "true" && this.state.showOnboarding) {
                    this.setState({showOnboarding: false})
                    this.props.hideOnboarding();
                }
            })
        }
    }

    static getDerivedStateFromProps(nextProps: RootNavigatorProps, previousState: RootNavigatorState) {
        var joinedToBill = nextProps.bill != undefined && nextProps.bill.billStatus != BillStatus.CLOSED;

        var showOnboarding = previousState.showOnboarding && nextProps.showOnboarding;
        if(nextProps.session == undefined 
            || previousState.session == undefined 
            || !DateUtils.equals(nextProps.session.loggedUntil, previousState.session.loggedUntil)
            || nextProps.session.accessToken != previousState.session.accessToken
            || joinedToBill != previousState.joinedToBill ) {
            return {session: nextProps.session, joinedToBill: joinedToBill, showOnboarding: showOnboarding};
        }
        return {session: previousState.session, joinedToBill: joinedToBill, showOnboarding: showOnboarding};
    }

    render() {
        if(Session.isLoggedIn(this.state.session)) {
            return AppNavigator(this.state.joinedToBill);
        } else if(this.state.showOnboarding) {
            return <OnboardingNavigator />
        } else {
            return <LoginNavigator />;
        }
    }
}

function mapStateToProps(state: AppState, ownProps: RootNavigatorProps): RootNavigatorProps {
    return {
      session: state.sessionState.session  ? Session.from(state.sessionState.session) : undefined,
      bill: state.billState.bill,
      showOnboarding: state.onboardingState.showOnboarding,
    } as RootNavigatorProps;
}

function mapDispatchToProps(dispatch: Dispatch<AnyAction>) {
    return bindActionCreators({
        hideOnboarding: hideOnboardingActionCreator
    }, dispatch);
  }
  
  export default connect(
    mapStateToProps,
    mapDispatchToProps
  )(RootNavigator);
  