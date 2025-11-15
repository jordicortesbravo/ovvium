import Button from '@material-ui/core/Button';
import Dialog from '@material-ui/core/Dialog';
import DialogActions from '@material-ui/core/DialogActions';
import DialogContent from '@material-ui/core/DialogContent';
import DialogTitle from '@material-ui/core/DialogTitle';
import * as React from 'react';
import { IssueStatus, getIssueStatusLabel, getIssueStatusColor } from 'app/model/enum/IssueStatus';
import { getServiceTimeLabel, ServiceTime } from 'app/model/enum/ServiceTime';

interface ChangeIssueStatusDialogProps {
    location: string;
    serviceTime: ServiceTime;
    open : boolean;
    onChangeIssueStatus : (issueStatus: IssueStatus) => void;
    onCancel: () => void;
}

export class ChangeIssueStatusDialog extends React.Component<ChangeIssueStatusDialogProps> {


    constructor(props) {
        super(props);
    }

    render() {
        return (<Dialog
            open={this.props.open}
            aria-labelledby="alert-dialog-title"
            aria-describedby="alert-dialog-description"
          >
            <DialogTitle id="alert-dialog-title">Selecciona el estado de preparación para:</DialogTitle>
            <DialogContent style={{textAlign: 'center'}}>
              <div style={{margin: "20px"}}>{this.props.location + ": " + getServiceTimeLabel(this.props.serviceTime)}</div>
              {this.renderIssueStatusButton(IssueStatus.PENDING)}
              {this.renderIssueStatusButton(IssueStatus.PREPARING)}
              {this.renderIssueStatusButton(IssueStatus.READY)}
              {this.renderIssueStatusButton(IssueStatus.ISSUED)}
            </DialogContent>
            <DialogActions>
              <Button onClick={this.props.onCancel} color={"primary"}>
                Cancelar
              </Button>
            </DialogActions>
          </Dialog>);
    }

    private renderIssueStatusButton(issueStatus: IssueStatus) {
      return  <Button variant="contained" onClick={() => this.props.onChangeIssueStatus(issueStatus)} 
                style={{marginRight: "10px", color: "white", backgroundColor: getIssueStatusColor(issueStatus)}}>
                {getIssueStatusLabel(issueStatus)}
              </Button>
    }
}