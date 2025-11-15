import * as React from 'react';

import Bugsnag from '@bugsnag/js'
import BugsnagPluginReact from '@bugsnag/plugin-react'
import isElectron from 'is-electron';

import { getEnv, properties } from 'app/config/Properties';
import { store } from './ReduxConfig';

export function initBugTracker() {
    Bugsnag.start({
        apiKey: properties.apiKey.bugSnag,
        plugins: [new BugsnagPluginReact()],
        releaseStage: getEnv(),
        enabledReleaseStages: ['staging', 'production'],
        appType: isElectron() ? 'standalone' : 'browser',
        onError: function (event) {
            // Add useful context data to be sent to BugSnag
            const user = store.getState().sessionState?.user;
            if (user) {
                event.setUser(user.customerId, undefined, user.customerName);
            }
        }
    })
    return Bugsnag.getPlugin('react')!!.createErrorBoundary(React);
}