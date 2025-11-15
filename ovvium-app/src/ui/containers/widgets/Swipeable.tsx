
import React from "react";
import { Image, Platform, StyleSheet, Text, View } from 'react-native';
import Modal from 'react-native-modal';
import { connect } from "react-redux";
import { AnyAction, bindActionCreators, Dispatch } from "redux";
import { hideOnboardingTrickActionCreator } from "../../../actions/OnboardingActions";
import { msg } from "../../../services/LocalizationService";
import { AppState } from "../../../store/State";
import { Button } from "../../components/Button/Button";
import { AppColors } from '../../styles/layout/AppColors';
import AsyncStorage from "@react-native-community/async-storage";
import { AppFonts } from "../../styles/layout/AppFonts";

interface SwipeableProps {
    id?: string;
    message?: string;
    hidedSwipeables: string[];

    hideOnboardingTrick: (trickId: string) => void;
}

interface SwipeableState {
    visible: boolean;
}

class Swipeable extends React.Component<SwipeableProps, SwipeableState> {

    modal?: Modal;

    constructor(props: any) {
        super(props);
        this.state = {visible: false};
        // this.state = {visible: true};
    }

    async componentDidMount() {
        await AsyncStorage.getItem("shownTricks")
        .then((item => {
            if(item == null) {
                this.setState({visible: true});
            } else {
                var hidedSwipeables = JSON.parse(item);
                this.UNSAFE_componentWillReceiveProps({hidedSwipeables, id: this.props.id!} as SwipeableProps)
            }
        }))
    }

    UNSAFE_componentWillReceiveProps(props: SwipeableProps) {
        var newVisibleState = props.hidedSwipeables.indexOf(props.id!) == -1;
        if(this.state.visible != newVisibleState) {
            this.setState({visible: newVisibleState});
        }
    }

    render() {
        return <Modal
                ref={(ref: Modal) => this.modal = ref}
                isVisible={this.state.visible}
                onSwipeComplete={this.close.bind(this)}
                swipeDirection={['down']}
                style={{justifyContent: 'flex-end',margin:0}}>
                <View style={styles.content}>
                    {Platform.OS == 'ios' &&
                        <View style={{backgroundColor: AppColors.lightGray, width: '40%', borderRadius:12, height: 5, position: 'absolute', top:5}}/>
                    }
                    <View style={{position: 'absolute', top:20, left: 20, flexDirection:'row'}}>
                        <Image source={require('../../../../assets/images/icons/appIcon.png')} />
                        <Text style={styles.contentTitle}>{msg("onboarding:tricks:title")}</Text>
                    </View>
                    <View style={{position: 'absolute', top:60}}>
                        {this.props.message &&
                            <Text style={swipeableStyle.message}>{this.props.message}</Text>
                        }
                        {this.props.children}
                    </View>
                    <View style={{bottom: Platform.OS == 'ios' ? 50 : 40, right: 20, position: 'absolute'}}>
                        <Button onlyText={true} onPress={this.close.bind(this)} label={msg("actions:close")} textStyle={{color: AppColors.main, fontFamily: AppFonts.regular}}/>
                    </View>
                </View>
            </Modal>            
    }

    close() {
        if(this.modal) {
            this.setState({visible: false});
            this.props.hideOnboardingTrick(this.props.id!);
            
        }
    }
}

export const swipeableStyle = StyleSheet.create({
    message: {
        fontFamily: AppFonts.regular, 
        color: 'rgba(0,0,0,0.9)',
        fontSize: 18
    }
});

const styles = StyleSheet.create({
    content: {
      backgroundColor: AppColors.white,
      padding: 22,
      alignItems: 'center',
      borderRadius: 4,
      height: '30%',
      borderColor: 'rgba(0, 0, 0, 0.1)'
    },
    contentTitle: {
      fontFamily: AppFonts.bold, 
      fontSize:22, 
      color: AppColors.mainText, marginLeft: 15
    },
  });

  function mapStateToProps(state: AppState): SwipeableProps {
    return {
        hidedSwipeables: state.onboardingState.shownTricks
    } as SwipeableProps;
  }
  
  function mapDispatchToProps(dispatch: Dispatch<AnyAction>) {
    return bindActionCreators(
      {
        hideOnboardingTrick: hideOnboardingTrickActionCreator
      },
      dispatch
    );
  }
  
  export default connect(
    mapStateToProps,
    mapDispatchToProps
  )(Swipeable);
  
  
