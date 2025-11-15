import * as React from 'react';
import Snackbar from '@material-ui/core/Snackbar';
import Alert from '@material-ui/lab/Alert';
import { Slide } from '@material-ui/core';
import { TransitionProps } from '@material-ui/core/transitions';

interface ErrorSnackbarProps {
  show: boolean;
  error?: any;
  onClose: () => void;
}


export class ErrorSnackbar extends React.Component<ErrorSnackbarProps> {

  constructor(props: ErrorSnackbarProps) {
    super(props);
  }

  handleClose() {
    this.props.onClose()
  };

  render() {
    return <>
      <Snackbar
        open={this.props.show}
        autoHideDuration={6000}
        onClose={this.handleClose.bind(this)}
        anchorOrigin={{ vertical: 'top', horizontal: 'right' }}
        TransitionComponent={this.SlideTransition}
      >
        <Alert severity={this.props.error?.localizedMessage ? "warning" : "error"} closeText="X" onClose={this.handleClose.bind(this)}>{this.getMessage()}</Alert>
      </Snackbar>
    </>
  }

  private SlideTransition(props: TransitionProps) {
    return <Slide {...props} direction="down" />;
  }


  private getMessage(): string | undefined {
    if (this.props.error?.localizedMessage) {
      return this.props.error?.localizedMessage
    }
    return "Ups! Ha ocurrido un error. Error: " + this.props.error?.message;
  }
}