import i18next, { LanguageDetectorModule } from "i18next";
import { NativeModules, Platform } from "react-native";
import { properties } from '../../resources/Properties';

const languageDetector = {
  type: "languageDetector",
  async: false,
  detect: () => {
    var locale;
    if(Platform.OS === 'ios') {
      locale = NativeModules.SettingsManager.settings.AppleLocale;
      if (locale === undefined) {
        // iOS 13 workaround, take first of AppleLanguages array  ["en", "en-NZ"]
        locale = NativeModules.SettingsManager.settings.AppleLanguages[0];
      }
    } else {
      locale = NativeModules.I18nManager.localeIdentifier;
    }
    return locale.split('_')[0];        
  },
  init: () => {},
  cacheUserLanguage: () => {}
} as LanguageDetectorModule;

const localization = {
  init: () => {
    return new Promise((resolve, reject) => {
      i18next
        .use(languageDetector)
        .init(
          {
            fallbackLng: "es",
            resources: {
              es: require("../localization/messages.es.json"),
              ca: require("../localization/messages.ca.json"),
              en: require("../localization/messages.en.json")
            },
            interpolation: {
              escapeValue: false
            },
            load: "currentOnly",
            debug: properties.debug
          },
          (error:any) => {
            if (error) {
              return reject(error);
            }
            return resolve();
          }
        );
    });
  },

  m: (key: string, options?: any) => i18next.t(key, options).toString()
};

export const msg = localization.m;

export const defaultLanguage = 'es-ES';

export function getLanguageFromRegional(regionalLanguage: string) {
  return regionalLanguage.substr(0,2);
}

export function getLocalization(localizations: any) {
  var language = getLanguageFromRegional(i18next.language);
  var keys = Object.keys(localizations);
  for(var k in keys) {
    var key = keys[k];
    var localizationLanguage = getLanguageFromRegional(key);
    if(key == i18next.language || key == language || localizationLanguage == language) {
      return localizations[key];
    }
  }
  return localizations[defaultLanguage];
}

export function getCurrentLocale() {
  return i18next.language;
}

export function enumMsg(key:string, enumValue: string) {
  return msg(key + ":" + enumValue);
}

export default localization;
