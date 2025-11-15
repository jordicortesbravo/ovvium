import { loadBillsActionCreator } from 'app/actions/BillActions';
import { Customer } from 'app/model/Customer';
import { AppState } from 'app/reducers/RootReducer';
import * as React from 'react';
import { connect } from 'react-redux';
import { AnyAction, bindActionCreators, Dispatch } from 'redux';
import { properties } from 'app/config/Properties';


interface ScheduledJobsContainerProps {
  customer?: Customer;
  loadBills?: (customer: Customer) => void;
}

interface ScheduledJobsContainerState {
  initialized: boolean;
}

class ScheduledJobsContainer extends React.Component<ScheduledJobsContainerProps, ScheduledJobsContainerState> {

  jobs: Array<number> = new Array<number>();
  
  constructor(props) {
    super(props);
    this.state = {
      initialized: false
    };
  }

  componentDidMount() {
    const {
      customer,
      loadBills
    } = this.props;
    if (customer) {
      this.setState({ initialized: true });
      this.registerJob(() => loadBills!(customer!), properties.jobs.loadBills.delay);
    }
  }

  componentWillUnmount() {
    this.jobs.forEach(job => this.unregisterJob(job));
  }

  componentDidUpdate(prevProps:ScheduledJobsContainerProps) {
    const {
      customer,
      loadBills
    } = this.props;
      if (customer !== prevProps.customer) {
      const { initialized } = this.state;
      if (!customer) {
        this.jobs.forEach(job => this.unregisterJob(job));
        this.setState({ initialized: false });
      } else if (!initialized) {
        this.setState({initialized: true});
        this.registerJob(() => loadBills!(customer!), properties.jobs.loadBills.delay);
      }
    }
  }

  registerJob(callback: () => void, timeout:number) {
    var jobId = window.setInterval(callback, timeout);
    this.jobs.push(jobId);
  }

  unregisterJob(jobId: number) {
    window.clearInterval(jobId);
  }

  render() {
    return <></>;
  }
}

function mapStateToProps(state: AppState): ScheduledJobsContainerProps {
  return {
    customer: state.billState.customer!
  } as ScheduledJobsContainerProps;
}

function mapDispatchToProps(dispatch: Dispatch<AnyAction>) {
  return bindActionCreators(
    {
      loadBills: loadBillsActionCreator,
    },
    dispatch
  );
}

export default connect(
  mapStateToProps,
  mapDispatchToProps
)(ScheduledJobsContainer);
