import { createStore, applyMiddleware } from 'redux';
import { logger } from 'app/middleware';
import { rootReducer } from 'app/reducers/RootReducer';
import { initialState } from 'app/store/AppState';
import thunk from 'redux-thunk';
import storage from 'redux-persist/lib/storage';
import { persistStore, persistReducer } from 'redux-persist';
import autoMergeLevel2 from 'redux-persist/lib/stateReconciler/autoMergeLevel2'

const persistConfig = {
  key: 'root',
  storage: storage,
  blacklist: ["productState"],
  stateReconciler: autoMergeLevel2
};

const persistedReducer = persistReducer(persistConfig, rootReducer);

let middleware = applyMiddleware(thunk, logger);

if (process.env.NODE_ENV !== 'production') {
  //const composeWithDevTools = require('redux-devtools-extension');
  //middleware = composeWithDevTools(middleware);
}
export const store = createStore(
  persistedReducer,
  initialState,
  middleware
);

if (module.hot) {
  module.hot.accept('app/reducers', () => {
    const nextReducer = require('app/reducers');
    store.replaceReducer(nextReducer);
  });
}

export const persistor = persistStore(store); 
