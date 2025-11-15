import { connect } from 'react-redux';
import * as React from 'react';
import { Image } from 'react-bootstrap';
import * as style from './style.css';

interface VirtualKeyboardToggleProps {}

interface VirtualKeyboardToggleState {}

export class VirtualKeyboardToggle extends React.Component<
  VirtualKeyboardToggleProps,
  VirtualKeyboardToggleState
> {
  constructor(props: VirtualKeyboardToggleProps) {
    super(props);
  }

  onTap = (event: React.SyntheticEvent<HTMLDivElement>) => {};

  render() {
    return (
      <div className={style['kb-icon-container']} onClick={this.onTap} onTouchEnd={this.onTap}>
         <Image src="../../../assets/img/icons/kb_icon/kb-icon.png" />
          <span>Activar Teclado</span>
      </div>
    );
  }
}

export default connect(
  null,
  {}
)(VirtualKeyboardToggle);
