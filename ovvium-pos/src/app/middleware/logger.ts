import { getEnv } from 'app/config/Properties';
import { Middleware } from 'redux';

export const logger: Middleware = (store) => (next) => (action) => {
  if (getEnv() !== 'production') {
    console.log(action);
  }
  return next(action);
};
