import { selectBillActionCreator, createOrJoinBillActionCreator, joinBillsAndLocationsActionCreator, removeBillActionCreator } from 'app/actions/BillActions';
import { LocationsView } from 'app/components/Locations/LocationsView';
import { BaseContainer, BaseContainerProps, BaseContainerState, baseMapDispatchToProps } from 'app/containers/BaseContainer';
import { Bill } from 'app/model/Bill';
import { Customer } from 'app/model/Customer';
import { Location } from 'app/model/Location';
import { AppState } from 'app/reducers/RootReducer';
import * as React from 'react';
import { connect } from 'react-redux';
import { AnyAction, Dispatch } from 'redux';
import { browserHistory } from 'app/App';
import { AppRoute } from 'app/containers/Router/AppRoute';
import { getLastInvoiceDateCreator } from './../../actions/InvoiceActions';
import { InvoiceDate } from 'app/model/InvoiceDate';
import { InvoiceDateClosedDialog } from 'app/components/Dialog/InvoiceDateClosedDialog';
import { CreateBillConfirmDialog } from 'app/components/Dialog/CreateBillConfirmDialog';
import { ErrorSnackbar } from 'app/components/ErrorSnackbar/ErrorSnackbar';
import { baseMapStateToProps } from './../BaseContainer';
import { ArrayUtils } from 'app/utils/ArrayUtils';
import { loadLocationsCreator } from 'app/actions/LocationActions';


interface LocationsContainerProps extends BaseContainerProps {
  locations: Array<Location>;
  bills: Array<Bill>;
  selectedBill?: Bill;
  customer: Customer;
  lastInvoiceDate?: InvoiceDate;

  loadLocations: (customer: Customer) => void;
  loadBills: (customer: Customer) => void;
  onSelectBill: (bill: Bill) => void;
  onCreateBill: (customer: Customer, locations: Array<Location>) => void;
  onJoinBills: (customer: Customer, locations: Array<Location>, bill?: Bill) => void;
  loadLastInvoiceDate: (customer: Customer) => void;
  removeBill: (customer: Customer, bill: Bill) => void;
}

interface LocationsContainerState extends BaseContainerState {
  selectedLocation?: Location;
}

class LocationsContainer extends BaseContainer<LocationsContainerProps, LocationsContainerState> {


  constructor(props: LocationsContainerProps) {
    super(props, {
      errorDialogOpened: false
    });
  }

  componentDidMount() {
    const customer = this.props.customer;
    if (ArrayUtils.isEmpty(this.props.locations)) {
      this.props.loadLocations(customer);
    }
    this.props.loadLastInvoiceDate(customer);
  }

  render() {
    return (<>
      <LocationsView
        locations={this.props.locations}
        bills={this.props.bills}
        selectedBill={this.props.selectedBill}
        onCreateBill={this.onCreateBill.bind(this)}
        onChangeCurrentBill={this.onChangeBill.bind(this)}
        onJoinLocations={(locations, bill?) => this.props.onJoinBills(this.props.customer, locations, bill)}
        onRemoveBill={this.onRemove.bind(this)}
      />
      <InvoiceDateClosedDialog open={this.props.lastInvoiceDate == undefined || this.props.lastInvoiceDate?.status == "CLOSED"}
        onClick={() => { this.props.history.push(AppRoute.CONFIG) }} />
      <CreateBillConfirmDialog location={this.state.selectedLocation!!}
        onAccept={() => this.onAcceptCreateBill(this.state.selectedLocation!!)}
        onCancel={() => this.onCancelBill()} />
      <ErrorSnackbar show={this.state.errorDialogOpened} error={this.props.error} onClose={this.onClearError.bind(this)} />
    </>)

  }

  onChangeBill(bill: Bill) {
    this.props.onSelectBill(bill);
    browserHistory.push(AppRoute.TAKE_ORDER);
  }

  onAcceptCreateBill(location: Location) {
    this.props.onCreateBill(this.props.customer, [location]);
    browserHistory.push(AppRoute.TAKE_ORDER);
    this.onCancelBill()
  }

  onCreateBill(location: Location) {
    // Open CreateBill dialog
    this.setState({
      selectedLocation: location
    })
  }

  onCancelBill() {
    this.setState({
      selectedLocation: undefined
    })
  }

  onRemove(bill: Bill) {
    this.props.removeBill(this.props.customer, bill)
  }

}

function mapStateToProps(state: AppState): LocationsContainerProps {
  return baseMapStateToProps(state, {
    locations: state.locationState.locations,
    bills: state.billState.bills,
    selectedBill: state.billState.selectedBill,
    customer: state.billState.customer,
    lastInvoiceDate: state.invoicesState.lastInvoiceDate,
    route: AppRoute.LOCATIONS
  } as LocationsContainerProps);
}

function mapDispatchToProps(dispatch: Dispatch<AnyAction>) {
  return baseMapDispatchToProps(dispatch,
    {
      onSelectBill: selectBillActionCreator,
      onCreateBill: createOrJoinBillActionCreator,
      onJoinBills: joinBillsAndLocationsActionCreator,
      loadLastInvoiceDate: getLastInvoiceDateCreator,
      removeBill: removeBillActionCreator,
      loadLocations: loadLocationsCreator,
    }
  );
}

export default connect(
  mapStateToProps,
  mapDispatchToProps
)(LocationsContainer);
