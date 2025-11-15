import { applyMiddleware, createStore } from "redux";
import { persistReducer, persistStore } from "redux-persist";
import autoMergeLevel2 from "redux-persist/es/stateReconciler/autoMergeLevel2";
import AsyncStorage from '@react-native-community/async-storage';
import thunk from "redux-thunk";
import { properties } from "../../resources/Properties";
import { rootReducer } from '../reducers/RootReducer';


const rootPersistConfig = {
  version: 1,
  key: "root",
  storage: AsyncStorage,
  stateReconciler: autoMergeLevel2,
  whitelist: ["sessionState", "billState", "profileState", "onboardingState"],
  debug: properties.debug
};

const _persistReducer = persistReducer(rootPersistConfig, rootReducer);

export const store = createStore(
  _persistReducer,
  applyMiddleware(thunk)
);
export const persistor = persistStore(store); 