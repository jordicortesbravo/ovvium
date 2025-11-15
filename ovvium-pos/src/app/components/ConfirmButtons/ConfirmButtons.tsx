import * as React from 'react';
import * as style from './style.css';
import { Tile } from '../Tile/Tile';

export interface ConfirmButtonsProps {
  onCancel: () => void;
  onAccept: () => void;
  acceptDisabled?: boolean;
}

export class ConfirmButtons extends React.Component<ConfirmButtonsProps> {
  constructor(props: ConfirmButtonsProps) {
    super(props);
  }

  render() {
    return (<div className="w-100">
      <Tile value="Cancelar" className={style.cancelTile} onClick={() => this.props.onCancel()} />
      <Tile value="Aceptar" className={style.acceptTile} onClick={() => this.props.onAccept()} disabled={this.props.acceptDisabled == undefined ? false : this.props.acceptDisabled}/>
    </div>)
  }

}
