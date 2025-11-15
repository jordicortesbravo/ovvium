import * as React from 'react';


import Button from '@material-ui/core/Button';
import Dialog from '@material-ui/core/Dialog';
import DialogActions from '@material-ui/core/DialogActions';
import DialogContent from '@material-ui/core/DialogContent';
import DialogContentText from '@material-ui/core/DialogContentText';
import DialogTitle from '@material-ui/core/DialogTitle';
import { Bill } from 'app/model/Bill';
import { LocationService } from 'app/services/LocationService';

interface RemoveBillConfirmDialogProps {
  bill?: Bill;
  onAccept: () => void;
  onCancel: () => void;
}

export class RemoveBillConfirmDialog extends React.Component<RemoveBillConfirmDialogProps> {

  constructor(props: RemoveBillConfirmDialogProps) {
    super(props);
  }

  render() {
    return (<Dialog
      open={this.props.bill != undefined}
      onClose={this.onCancel.bind(this)}
      aria-labelledby="alert-dialog-title"
      aria-describedby="alert-dialog-description"
      transitionDuration={0}
    >
      <DialogTitle id="alert-dialog-title">Eliminar Mesa</DialogTitle>
      <DialogContent>
        <DialogContentText id="alert-dialog-description">
          Esta cuenta no está vacía, eliminarla implicaria perder estos pedidos.
          ¿Seguro que quieres eliminar la cuenta de '{LocationService.getLocationName(this.props.bill)}'?
        </DialogContentText>
      </DialogContent>
      <DialogActions>
        <Button onClick={this.onCancel.bind(this)}>
          Cancelar
        </Button>
        <Button onClick={this.onAccept.bind(this)} color="primary" variant="contained">
          Eliminar
        </Button>
      </DialogActions>
    </Dialog>);
  }

  private onAccept() {
    this.props.onAccept()
  }

  private onCancel() {
    this.props.onCancel()
  }

}