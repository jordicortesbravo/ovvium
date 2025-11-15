import * as React from 'react';
import * as style from './style.css';
import * as classNames from 'classnames';

namespace Button {
  export interface ButtonProps {
    value: string;
    block: boolean;
    className?: string;
    onClick: () => void;
  }
}

export class Button extends React.Component<Button.ButtonProps> {
  constructor(props: Button.ButtonProps) {
    super(props);
    this.handleClick = this.handleClick.bind(this);
  }

  handleClick(e: React.FormEvent<HTMLButtonElement>) {
    e.preventDefault;
    this.props.onClick();
  }

  render() {
    const classes = classNames(
      {
        [style.block]: this.props.block
      },
      style.button,
      this.props.className
    );
    return (
      <button className={classes} onClick={this.handleClick.bind(this)}>
        {this.props.value}
      </button>
    );
  }
}
