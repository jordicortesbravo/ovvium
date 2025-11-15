import * as React from 'react';
import { RouteProps, Route, Redirect } from 'react-router-dom';
import { AppState } from 'app/store/AppState';
import { Dispatch, AnyAction, bindActionCreators } from 'redux';
import { connect } from 'react-redux';
import { AppRoute } from 'app/containers/Router/AppRoute';

interface SecuredRouteProps extends RouteProps {
  isAuthenticated?: boolean;
}

class SecuredRoute extends React.Component<SecuredRouteProps> {

  render() {
    if (this.props.isAuthenticated) {
      return <Route path={this.props.path} exact={this.props.exact} component={this.props.component} />
    }
    return <Redirect to={AppRoute.LOGIN} />
  }

}

function mapStateToProps(state: AppState): SecuredRouteProps {
  return {
    isAuthenticated: state.sessionState.isAuthenticated
  } as SecuredRouteProps;
}

function mapDispatchToProps(dispatch: Dispatch<AnyAction>) {
  return bindActionCreators({}, dispatch);
}

export default connect(
  mapStateToProps,
  mapDispatchToProps
)(SecuredRoute);
