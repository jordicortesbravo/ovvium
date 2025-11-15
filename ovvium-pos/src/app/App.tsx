import { initAxios } from 'app/config/AxiosConfig';
import { persistor, store } from 'app/config/ReduxConfig';
import BillContainer from 'app/containers/Bill/BillContainer';
import LayoutTemplateContainer from 'app/containers/LayoutTemplate/LayoutTemplateContainer';
import LocationsContainer from 'app/containers/Locations/LocationsContainer';
import LoginContainer from 'app/containers/Login/LoginContainer';
import { AppRoute } from 'app/containers/Router/AppRoute';
import SecuredRoute from 'app/containers/Router/SecuredRoute';
import ScheduledJobsContainer from 'app/containers/ScheduledJobs/ScheduledJobsContainer';
import ProductsContainer from 'app/containers/Products/ProductsContainer';
import { createBrowserHistory } from 'history';
import * as React from 'react';
import * as ReactDOM from 'react-dom';
import { Provider } from 'react-redux';
import { Router, Redirect, Route, Switch, withRouter } from 'react-router-dom';
import { PersistGate } from 'redux-persist/integration/react';
import ConfigurationContainer from 'app/containers/Configuration/ConfigurationContainer';
import ForgotPasswordContainer from './containers/ForgotPassword/ForgotPasswordContainer';
import KitchenContainer from 'app/containers/Kitchen/KitchenContainer';
import { Loading } from './components/Loading/Loading';

import { initBugTracker } from './config/BugsnagConfig';

export const browserHistory = createBrowserHistory();
const Layout = withRouter(props => <LayoutTemplateContainer {...props} />)

export class App extends React.Component<any> {

  componentDidMount() {
    initAxios();
  }

  render() {
    if (window.location.pathname.includes('index.html')) {
      browserHistory.push(AppRoute.TAKE_ORDER);
    }
    return <Provider store={store}>
      <PersistGate persistor={persistor} loading={<Loading />}>
          <ScheduledJobsContainer />
          <Router history={browserHistory}>
            <Layout>
              <Switch>
                <Route path="/" exact>
                  <Redirect to={AppRoute.TAKE_ORDER} />
                </Route>
              <Route path={AppRoute.LOGIN} exact component={LoginContainer} />
              <Route path={AppRoute.FORGOT_PASSWORD} exact component={ForgotPasswordContainer} />
              <SecuredRoute path={AppRoute.TAKE_ORDER} exact component={ProductsContainer} />
              <SecuredRoute path={AppRoute.LOCATIONS} exact component={LocationsContainer} />
              <SecuredRoute path={AppRoute.BILL} exact component={BillContainer} />
              <SecuredRoute path={AppRoute.CONFIG} exact component={ConfigurationContainer} />
              <SecuredRoute path={AppRoute.KITCHEN} exact component={KitchenContainer} />
              <Redirect to="/" /> {/* all not configured routes, fallback to index */}
              </Switch>
            </Layout>
          </Router>
        </PersistGate>
      </Provider>
  }
}

const ErrorBoundary = initBugTracker();
ReactDOM.render(<ErrorBoundary><App /></ErrorBoundary>, document.getElementById('root'));