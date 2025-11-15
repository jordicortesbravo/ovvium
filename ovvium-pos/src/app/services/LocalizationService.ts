import i18next, { LanguageDetectorModule } from "i18next";
import { MultiLangStringResponse } from './../model/response/MultiLangStringResponse';

export const defaultLanguage = 'es-ES';

const languageDetector = {
  type: "languageDetector",
  async: false,
  detect: () => {
    return defaultLanguage;
  },
  init: () => { },
  cacheUserLanguage: () => { }
} as LanguageDetectorModule;

const localization = {
  init: () => {
    return new Promise((resolve, reject) => {
      i18next
        .use(languageDetector)
        .init(
          {
            fallbackLng: getLanguageFromRegional(defaultLanguage),
            resources: require("../localization/messages.json"),
            interpolation: {
              escapeValue: false
            },
            load: "currentOnly"
          },
          (error: any) => {
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


export function getLanguageFromRegional(regionalLanguage: string) {
  return regionalLanguage.substr(0, 2);
}

export function getLocalization(multiLangString?: MultiLangStringResponse) {
  if (multiLangString == undefined || multiLangString == null) {
    return undefined;
  }
  const language = getLanguageFromRegional(defaultLanguage);
  const translations = multiLangString.translations;
  const keys = Object.keys(translations);
  for (var k in keys) {
    var key = keys[k];
    var localizationLanguage = getLanguageFromRegional(key);
    if (key == i18next.language || key == language || localizationLanguage == language) {
      return translations[key];
    }
  }
  if (translations[defaultLanguage]) {
    return translations[defaultLanguage];
  }
  return multiLangString.defaultValue;
}

export function enumMsg(key: string, enumValue: string) {
  return msg(key + ":" + enumValue);
}

export default localization;
