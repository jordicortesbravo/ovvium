import { connect } from 'react-redux';
import * as React from 'react';
import Keyboard  from 'react-virtual-keyboard';
import '../../../assets/css/keyboard/keyboard.css'
import '../../../assets/css/keyboard/keyboard-basic.css'
import '../../../assets/css/keyboard/keyboard-dark.css'
import '../../../assets/css/keyboard/keyboard-previewkeyset.css'

export enum VirtualKeyboardType {
  INPUT = "input", TEXTAREA = "textarea"
}

interface VirtualKeyboardProps {
  type: VirtualKeyboardType;
  placeholder?: string;
  inputClassName?: string;
  name : string;
  onChange: (value: string) => void;
}

interface VirtualKeyboardState {
  value: string;
}

/*
  TODO: Queda ver como hacemos inputs de tipo password pues ahora la libreria no lo permite.
  https://github.com/Utzel-Butzel/react-virtual-keyboard/issues/23
*/
export class VirtualKeyboard extends React.Component<VirtualKeyboardProps, VirtualKeyboardState> {

  // private keyboard =  React.createRef<React.Component>();

  constructor(props: VirtualKeyboardProps) {
    super(props);
    this.state = {
      value: ""
    }
  }

  onInput(input: string) {
    this.setState({ value: input });
  }

  onInputChanged = (event?: string | Event, keyboard?: Element, el?: Element) => {
    this.setState({ value: event as string });
    this.props.onChange(event as string );
  }
  
  onInputSubmitted = (event?: string | Event, keyboard?: Element, el?: Element) => {
   
  }

  render() {
    let type = this.props.type.toString();
    // let node = this.keyboard.current;
    let inputCss = this.props.inputClassName
    return (
      <Keyboard 
        value={this.state.value}
        name={this.props.name}
        options={{
          type: type,
          layout: "international",
          alwaysOpen: false,
          usePreview: false,
          caretToEnd : true,
          useWheel: false,
          stickyShift: false,
          appendLocally: false,
          color: "dark",
          updateOnChange: true,
          initialFocus: false,
          acceptValid: true,
          display: {
            "accept" : "Aceptar",
            "cancel" : "Cancelar"
          },
          css: {
            input: inputCss
          }
        }}
        placeholder = {this.props.placeholder}
        onChange={this.onInputChanged}
        onAccepted={this.onInputSubmitted}
        // ref={k => node = k}
      />
    );
  }
}

export default connect(
  null,
  {}
)(VirtualKeyboard);
