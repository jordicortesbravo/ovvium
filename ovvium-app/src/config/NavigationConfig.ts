
import { NavigationActions, NavigationRouter } from 'react-navigation';
import { AppScreens } from '../ui/navigation/AppScreens';
import { store } from './ReduxConfig';
import { AppState } from '../store/State';

export function initNavigationInterceptor(router: NavigationRouter<any,any>) {
    const previousGetActionForPathAndParams = router.getActionForPathAndParams;

    Object.assign(router, {
        getActionForPathAndParams(path: string, params: any) {
            if(path == AppScreens.Activation) {
                return navigate(path, params)
            } else if(matchWithAnyScreen(path)) {
                if(isLoggedIn()) {
                    return navigate(path, params);
                } else {
                    return navigate(AppScreens.Login, {});
                }
            }
        return previousGetActionForPathAndParams(path, params);
        },
    });

    function navigate(path: string, params: any) {
        return NavigationActions.navigate({
            routeName: path,
            params: { ...params, path },
        });
    }

    function isLoggedIn() {
        var state = store.getState() as AppState;
        var user = state.sessionState.user;
        return user && user.enabled;
    }

    function matchWithAnyScreen(path?:string): boolean {
        if(!path) {
            return false;
        }
        var screens = new Array<string>()
        for(var screen in AppScreens) {
            screens.push(AppScreens[screen]);
        }
        return screens.indexOf(path) != -1;
    } 
}
