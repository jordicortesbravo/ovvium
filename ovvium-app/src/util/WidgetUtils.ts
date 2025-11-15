import { Alert } from "react-native";
import { msg } from '../services/LocalizationService';

export interface ErrorDialogProps {
  error?: any;
  title?: string;
  message?: string;
  screen: string;
  clearError?: (screen:string) => void;
}

export function dialog(title: string|null, message:string, onOk: () => void, cancelable: boolean) {
  var buttons = [];
  buttons.push({
      text: msg('actions:confirm'),
      onPress: onOk
    });
  if(cancelable) {
    buttons.push({
      text:  msg('actions:cancel'),
      onPress: () => {}
    });
  }
  Alert.alert(title ? title : '',message, buttons);
}

export function errorDialog(props: ErrorDialogProps) {
  var msg = errorMessage(props.error);
  if(!props.message && msg == "") {
    return;
  }
  Alert.alert(
      props.title ? props.title : '',
      props.message ? props.message : msg,
      [
        {text: 'OK', onPress: () => {
          if(props.clearError) {
            props.clearError(props.screen)
          }
        }},
      ],
      {cancelable: false},
    );
  
    
}

export function errorMessage(error?: any) {
  if(error) {
    var i18Message = msg("error:" + error.code);
    if(error == undefined || i18Message == error.code + "") {
        return "";
    }
    return error.code ?  i18Message : error.message;
  }

}
