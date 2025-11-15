import React from "react";
import { ActivityIndicator, Text, View } from 'react-native';
import { WebView, WebViewMessageEvent } from 'react-native-webview';
import { PaymentMethod } from '../../../model/PaymentMethod';
import { msg } from '../../../services/LocalizationService';
import { AppColors } from '../../styles/layout/AppColors';
import MultifamilyIcon, { IconFamily } from '../MultiFamilyIcon/MultifamilyIcon';
import { PaymentMethodResume } from '../PaymentMethodResume/PaymentMethodResume';
import { WebViewNavigationEvent } from "react-native-webview/lib/WebViewTypes";
import { properties } from "../../../../resources/Properties";
import { AppFonts } from "../../styles/layout/AppFonts";

interface PaymentMethodFormProps {
    paymentMethod: PaymentMethod;
    editable?:boolean;
    addRunConfirmInterceptor: (interceptor: () => void) => void;
    onConfirm: (paymentMethod: PaymentMethod) => void;
}

interface PaymentMethodFormState {
    loading: boolean;
}

export class PaymentMethodForm extends React.Component<PaymentMethodFormProps, PaymentMethodFormState> {

    webView?: WebView;

    constructor(props: PaymentMethodFormProps) {
        super(props);
        this.state = {loading:true}
    }
    
    render() {
        if(!this.props.editable) {
            return  <View style={{marginTop: 20, marginBottom: 50}}>
                        <PaymentMethodResume paymentMethod={this.props.paymentMethod} onPick={()=>{}} />
                    </View>
        }
        return  <View style={{marginTop: 30, marginBottom: 50}}>
                   {this.state.loading &&
                        <View style={{position:'absolute', top: 10, height:350, zIndex:10, justifyContent:'center', alignContent:'center', width:'100%'}}>
                            <ActivityIndicator color={AppColors.main} size="large"/>
                        </View>
                    }
                    <WebView source={{uri: properties.paycometFormUrl}} 
                        ref={(webView:WebView) => this.webView = webView}
                        onLoad={this.onLoad.bind(this)}
                        onMessage={this.onMessage.bind(this)}
                        style={{height:350, width: '93%', marginHorizontal:20, borderColor: AppColors.white}}/>
                    <View style={{width:'100%', justifyContent:'center', alignItems:'center'}}>
                        <View style={{marginTop:30, justifyContent:'center', alignItems:'center', marginHorizontal:'20%'}}>
                            <MultifamilyIcon family={IconFamily.MATERIAL_COMMUNITY} name="security" size={16} color={AppColors.listItemDescriptionText}/>
                            <Text style={{fontFamily: AppFonts.regular, fontSize:12, color: AppColors.listItemDescriptionText, textAlign:'center'}}>{msg("profile:paymentMethod:dataSecured")}</Text>
                        </View>
                    </View>
                </View>
    }

    componentDidMount() {
        this.props.addRunConfirmInterceptor(this.postPaymentForm.bind(this));
    }

    postPaymentForm() {
        if(!this.props.editable) {
            this.props.onConfirm(this.props.paymentMethod);
        } else if(this.webView) {
            this.webView.injectJavaScript(`postPaycometForm()`)
        }
    }

    onLoad(event: WebViewNavigationEvent) {
        this.setState({loading:false});
    }

    onMessage(event: WebViewMessageEvent) {
        var token = event.nativeEvent.data;
        var paymentMethod = new PaymentMethod(this.props.paymentMethod);
        paymentMethod.pciTemporalToken = token;
        this.props.onConfirm(paymentMethod);
    }
}