import * as React from 'react';
import { Bill } from '../../../model/Bill';
import { User } from '../../../model/User';
import { Customer } from '../../../model/Customer';
import { properties } from '../../../../resources/Properties';
import { BillStatus } from '../../../model/enum/BillStatus';
import { AppState } from '../../../store/State';
import { Dispatch, AnyAction, bindActionCreators } from 'redux';
import { refreshBillActionCreator } from '../../../actions/BillActions';
import { connect } from 'react-redux';


interface ScheduledJobsContainerProps {
  bill?: Bill;
  customer?: Customer;
  user?: User;
  refreshBill: (user: User) => void;
}

class ScheduledJobsContainer extends React.Component<ScheduledJobsContainerProps, any> {

  jobs: Array<NodeJS.Timeout> = new Array<NodeJS.Timeout>();

  UNSAFE_componentWillMount() {
    this.registerJob(() => {
      if (this.props.bill != undefined && this.props.bill.billStatus == BillStatus.OPEN && this.props.customer && this.props.user) {
        this.props.refreshBill(this.props.user);
      }
    }, properties.jobs.refreshBill.delay);
  }

  componentWillUnmount() {
    this.unregisterAllJobs();
  }

  registerJob(callback: () => void, timeout: number) {
    var jobId = setInterval(callback, timeout);
    this.jobs.push(jobId);
  }

  unregisterAllJobs() {
    this.jobs.forEach(job => this.unregisterJob(job));
  }

  unregisterJob(jobId: NodeJS.Timeout) {
    clearInterval(jobId);
  }

  render() {
    return <></>;
  }
}

function mapStateToProps(state: AppState): ScheduledJobsContainerProps {
  return {
    customer: state.billState.customer,
    bill: state.billState.bill,
    user: state.sessionState.user
  } as ScheduledJobsContainerProps;
}

function mapDispatchToProps(dispatch: Dispatch<AnyAction>) {
  return bindActionCreators(
    {
      refreshBill: refreshBillActionCreator,
    },
    dispatch
  );
}

export default connect(
  mapStateToProps,
  mapDispatchToProps
)(ScheduledJobsContainer);
