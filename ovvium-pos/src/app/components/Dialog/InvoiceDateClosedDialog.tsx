import * as React from 'react';


import Button from '@material-ui/core/Button';
import Dialog from '@material-ui/core/Dialog';
import DialogActions from '@material-ui/core/DialogActions';
import DialogContent from '@material-ui/core/DialogContent';
import DialogContentText from '@material-ui/core/DialogContentText';
import DialogTitle from '@material-ui/core/DialogTitle';

interface InvoiceDateClosedDialogProps {
    open : boolean;
    onClick : () => void;
}

export class InvoiceDateClosedDialog extends React.Component<InvoiceDateClosedDialogProps> {


    constructor(props) {
        super(props);
    }

    render() {
        return (<Dialog
            open={this.props.open}
            aria-labelledby="alert-dialog-title"
            aria-describedby="alert-dialog-description"
          >
            <DialogTitle id="alert-dialog-title">Alerta: No existe Fecha de Facturación abierta</DialogTitle>
            <DialogContent>
              <DialogContentText id="alert-dialog-description">
                Para que los pedidos y facturas se creen bajo fecha de hoy, debe abrirse una Fecha de Facturación primero.
              </DialogContentText>
            </DialogContent>
            <DialogActions>
              <Button onClick={this.onClick.bind(this)} color="primary">
                Ver Facturación
              </Button>
            </DialogActions>
          </Dialog>);
    }
    
    private onClick() {
        this.props.onClick()
    }

}