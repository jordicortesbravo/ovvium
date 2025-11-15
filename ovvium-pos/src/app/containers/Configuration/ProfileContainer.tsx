import { AppState } from 'app/store/AppState';
import * as React from 'react';
import { connect } from 'react-redux';
import { AnyAction, bindActionCreators, Dispatch } from 'redux';


interface ProfileContainerProps {

}


interface ProfileContainerState {
    
}

class ProfileContainer extends React.Component<ProfileContainerProps, ProfileContainerState> {

    constructor(props) {
        super(props);
        this.state = {};
    }

    render() {
        return <div/>
    }
}

function mapStateToProps(state: AppState): ProfileContainerProps {
    return {
        
    } as ProfileContainerProps;
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
)(ProfileContainer);
  