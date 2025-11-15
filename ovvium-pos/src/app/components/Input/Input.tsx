import * as React from 'react';
import * as style from './style.css';
import { VirtualKeyboard } from '../VirtualKeyboard/VirtualKeyboard';
import { VirtualKeyboardType } from 'app/components/VirtualKeyboard/VirtualKeyboard';
import * as classNames from 'classnames';
namespace Input {
  export interface InputProps {
    placeholder: string;
    type: string;
    name: string;
    className?: string;
    inputMode?: 'none' | 'text' | 'tel' | 'url' | 'email' | 'numeric' | 'decimal' | 'search',
    onChange: (value: string) => void;
    keyboardShown: boolean;
  }
}

export class Input extends React.Component<Input.InputProps> {
  constructor(props: Input.InputProps) {
    super(props);
    this.onChange = this.onChange.bind(this)
  }

  onChange(e: React.ChangeEvent<HTMLInputElement>) {
    this.props.onChange(e.target.value);
  }

  render() {
    if (this.props.keyboardShown) {
      return (
        <VirtualKeyboard
          type={VirtualKeyboardType.INPUT}
          inputClassName={style.input_text}
          name={this.props.name}
          placeholder={this.props.placeholder}
          onChange={this.props.onChange}
        />
      );
    } else {
      return (
        <input
          className={classNames(style.input_text, this.props.className)}
          type={this.props.type}
          name={this.props.name}
          onChange={this.onChange}
          placeholder={this.props.placeholder}
          autoCorrect="off"
          autoCapitalize="none"
          autoComplete="off"
          inputMode={this.props.inputMode}
        />
      );
    }
  }
}
