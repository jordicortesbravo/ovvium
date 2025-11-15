import crashlytics from '@react-native-firebase/crashlytics';

export class CrashlyticsUtil {

    static recordError(message: string, error: Error) {
        crashlytics().log(message + ": " + error.message);
        if(error.stack) {
            crashlytics().log(error.stack);
        }
        crashlytics().recordError(error);
        console.log(message + ": " + error.message);
    }
} 