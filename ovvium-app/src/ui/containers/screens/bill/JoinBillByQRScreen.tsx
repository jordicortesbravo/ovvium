import { connect } from 'react-redux';
import { AnyAction, bindActionCreators, Dispatch } from 'redux';
import { createOrJoinBillActionCreator } from '../../../../actions/BillActions';
import { AppState } from '../../../../store/State';
import { JoinBillScreen, JoinBillScreenProps } from './JoinBillScreen';

export class JoinBillByQRScreen extends JoinBillScreen {

    constructor(props: JoinBillScreenProps) {
        super(props);
        this.state = {nfcSupported: false, forcedView: 'qr', joining: false}
    }
}

function mapStateToProps(state: AppState): JoinBillScreenProps {
    return {
      user: state.sessionState.user,
      bill: state.billState.bill
    } as JoinBillScreenProps;
  }

function mapDispatchToProps(dispatch: Dispatch<AnyAction>) {
    return bindActionCreators(
      {
        createOrJoinBill: createOrJoinBillActionCreator
      },
      dispatch
    );
  }
  
  const JoinBillByQRContainer = connect(
    mapStateToProps,
    mapDispatchToProps
  )(JoinBillByQRScreen);
  
  export default JoinBillByQRContainer;
  