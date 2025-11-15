import { browserHistory } from 'app/App';
import { LayoutTemplate } from 'app/components/LayoutTemplate/LayoutTemplate';
import { Bill } from 'app/model/Bill';
import { AppState } from 'app/reducers/RootReducer';
import * as React from 'react';
import { connect } from 'react-redux';
import { RouteComponentProps } from 'react-router';
import { AnyAction, bindActionCreators, Dispatch } from 'redux';


interface LayoutTemplateContainerProps extends RouteComponentProps {
  selectedBill?: Bill;
}

class LayoutTemplateContainer extends React.Component<LayoutTemplateContainerProps, any> {

  render() {
    return <LayoutTemplate
      currentRoute={this.props.location.pathname}
      selectedBill={this.props.selectedBill}
      goToRoute={(route) => browserHistory.push(route)}>
      {this.props.children}
    </LayoutTemplate>

  }

}

function mapStateToProps(state: AppState): LayoutTemplateContainerProps {
  return {
    selectedBill: state.billState.selectedBill,
  } as LayoutTemplateContainerProps;
}

function mapDispatchToProps(dispatch: Dispatch<AnyAction>) {
  return bindActionCreators(
    {
    },
    dispatch
  );
}

export default connect(
  mapStateToProps,
  mapDispatchToProps
)(LayoutTemplateContainer);
