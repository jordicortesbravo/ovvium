import * as React from 'react';


import Button from '@material-ui/core/Button';
import Dialog from '@material-ui/core/Dialog';
import DialogActions from '@material-ui/core/DialogActions';
import DialogContent from '@material-ui/core/DialogContent';
import DialogContentText from '@material-ui/core/DialogContentText';
import DialogTitle from '@material-ui/core/DialogTitle';
import { Location } from 'app/model/Location';

interface CreateBillDialogProps {
  location?: Location;
  onAccept: () => void;
  onCancel: () => void;
}

export class CreateBillConfirmDialog extends React.Component<CreateBillDialogProps> {

  constructor(props: CreateBillDialogProps) {
    super(props);
  }

  render() {
    return (<Dialog
      open={this.props.location != undefined}
      onClose={this.onCancel.bind(this)}
      aria-labelledby="alert-dialog-title"
      aria-describedby="alert-dialog-description"
      transitionDuration={0}
    >
      <DialogTitle id="alert-dialog-title">Abrir Mesa</DialogTitle>
      <DialogContent>
        <DialogContentText id="alert-dialog-description">
          ¿Quieres abrir una nueva cuenta para '{this.props.location?.description  ?? "" }'?
        </DialogContentText>
      </DialogContent>
      <DialogActions>
        <Button onClick={this.onCancel.bind(this)}>
          Cancelar
        </Button>
        <Button onClick={this.onAccept.bind(this)} color="primary" variant="contained">
          Abrir
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