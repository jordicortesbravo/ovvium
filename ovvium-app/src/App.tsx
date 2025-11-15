import { NavigationContainer, NavigationState } from '@react-navigation/native';
import React from "react";
import { Provider } from "react-redux";
import { PersistGate } from "redux-persist/integration/react";
import { initAxios } from './config/AxiosConfig';
import { persistor, store } from './config/ReduxConfig';
import localization from "./services/LocalizationService";
import { LoadingView } from './ui/components/LoadingView/LoadingView';
import ScheduledJobsContainer from './ui/containers/widgets/ScheduledJobsContainer';
import RootNavigator from "./ui/navigation/RootNavigator";
import analytics from '@react-native-firebase/analytics';
import crashlytics from '@react-native-firebase/crashlytics';

interface InitState {
  isLocalizationInitialized: boolean;
}

console.disableYellowBox = true;

export default class App extends React.Component<any, InitState> {
  constructor(props: any) {
    super(props);
    this.state = {
      isLocalizationInitialized: false
    };
  }

  async componentDidMount() {
    analytics().logAppOpen();
    this.initLocalization();
    initAxios();
  }

  render() {
    return (
      <Provider store={store}>
        <PersistGate loading={<LoadingView />} persistor={persistor}>
          <ScheduledJobsContainer />
          {!this.state.isLocalizationInitialized ? (
            <LoadingView />
          ) : (
              <NavigationContainer onStateChange={(state: NavigationState | undefined) => {
                if (state) {
                  var route = state.routes[state.index];
                  if (route.state) {
                    route = route.state.routes[route.state.index];
                  }
                  analytics().logEvent("open_view_" + route.name.split("-").join("_"), { view: route.name, params: route.params });
                }
              }}>
                <RootNavigator />
              </NavigationContainer>
            )}
        </PersistGate>
      </Provider>
    );
  }

  private initLocalization() {
    localization
      .init()
      .then(() => {
        this.setState({
          isLocalizationInitialized: true
        });
      })
      .catch(error => {
        console.error(error);
      });
  }
}
