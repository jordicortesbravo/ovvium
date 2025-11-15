import React from "react";
import { ScrollView, View, TextInput } from 'react-native';
import { msg, enumMsg } from '../../../services/LocalizationService';
import { AppColors } from '../../styles/layout/AppColors';
import { Header } from '../Header/Header';
import { Product } from "../../../model/Product";
import { Order } from "../../../model/Order";
import { Button } from '../Button/Button';
import { ButtonGroup, Text } from 'react-native-elements';
import { ServiceTime } from "../../../model/enum/ServiceTime";
import { AppFonts } from "../../styles/layout/AppFonts";
import { KeyboardAwareScrollView } from "react-native-keyboard-aware-scroll-view";

interface ConfigureProductViewProps {
    product: Product;
    goBack: () => void;
    onConfirm: (order: Order) => void;
}

interface ConfigureProductViewState {
    order: Order;
    serviceTimeIndex: number;
    shareIndex: number;
    keyboardShown: boolean;
}

export class ConfigureProductView extends React.Component<ConfigureProductViewProps, ConfigureProductViewState> {

    constructor(props: ConfigureProductViewProps) {
        super(props);
        this.state = {
            order: {
                product: props.product,
                serviceTime: ServiceTime.SOONER,
                price: props.product.price
            } as Order,
            serviceTimeIndex: 0,
            shareIndex: 0,
            keyboardShown: false
        }
    }

    render() {
        return <View style={{ backgroundColor: AppColors.white, height: '100%' }}>
            <KeyboardAwareScrollView endFillColor={AppColors.white}
                onKeyboardDidHide={() => this.setState({ keyboardShown: false })}
                onKeyboardDidShow={() => this.setState({ keyboardShown: true })}>
                <Header goBack={this.props.goBack}
                    goBackTitle={msg("actions:back")}
                    title={msg("products:configure:title")}
                    format="big"
                    subtitle={msg("products:configure:subtitle")} />
                <ScrollView style={{ marginBottom: 60, height: '100%', paddingHorizontal: 20 }}>
                    <View style={{ height: '100%', paddingBottom: 40 }}>
                        {this.renderQuestion(msg("products:configure:serviceTime") + this.props.product.name + "?")}
                        {this.renderButtonGroup(this.serviceTimeLabels(), this.state.serviceTimeIndex, this.onChangeServiceTime.bind(this))}
                        {this.renderQuestion(msg("products:configure:share:question"))}
                        {this.renderButtonGroup([msg("products:configure:share:no"), msg("products:configure:share:yes")], this.state.shareIndex, this.onChangeToShare.bind(this))}
                        {this.renderQuestion(msg("products:configure:notes:question"))}
                        <TextInput
                            value={this.state.order.notes}
                            onChangeText={(notes: string) => this.state.order.notes = notes}
                            placeholder={msg("products:configure:notes:examples")}
                            maxLength={200}
                            style={{ marginHorizontal: 10, height: 60, padding: 20, fontFamily: AppFonts.regular, borderWidth: 1, borderColor: '#dfe1e5', borderRadius: 8 }}
                        />
                    </View>
                </ScrollView>
            </KeyboardAwareScrollView>
            {!this.state.keyboardShown && <View style={{ alignItems: 'center', position: 'absolute', bottom: 0, width: '100%', backgroundColor: 'white', zIndex: 2, height: 80, justifyContent: 'center' }}>
                <Button label={msg("bill:cart:add")} onPress={this.onConfirm.bind(this)} />
            </View>}
        </View>
    }

    private onConfirm() {
        if (this.state.shareIndex) {
            if (this.state.order.notes) {
                this.state.order.notes += ". PARA COMPARTIR";
            } else {
                this.state.order.notes = "PARA COMPARTIR";
            }
        }
        this.props.onConfirm(this.state.order);
    }

    private renderQuestion(question: string) {
        return <View style={{ paddingVertical: 20, paddingHorizontal: 20 }}>
            <Text style={{ fontFamily: AppFonts.regular, color: AppColors.listItemDescriptionText }}>{question}</Text>
        </View>
    }

    private renderButtonGroup(options: string[], selectedIndex: number, onPress: (index: number) => void) {
        return <ButtonGroup
            onPress={onPress}
            selectedIndex={selectedIndex}
            buttons={options}
            containerStyle={{ borderRadius: 10, marginBottom: 20 }}
            textStyle={{ fontFamily: AppFonts.regular, textAlign: "center" }}
            selectedTextStyle={{ fontFamily: AppFonts.regular }}
            selectedButtonStyle={{ backgroundColor: AppColors.main }}
            buttonStyle={{ borderColor: AppColors.mainText }}
        />
    }

    private onChangeServiceTime(serviceTimeIndex: number) {
        switch (serviceTimeIndex) {
            case 0:
                this.state.order.serviceTime = ServiceTime.STARTER;
                break;
            case 1:
                this.state.order.serviceTime = ServiceTime.FIRST_COURSE;
                break;
            case 2:
                this.state.order.serviceTime = ServiceTime.SECOND_COURSE;
                break;
            case 3:
                this.state.order.serviceTime = ServiceTime.DESSERT;
                break;
        }
        this.setState({ serviceTimeIndex });
    }

    private onChangeToShare(shareIndex: number) {
        this.setState({ shareIndex });
    }

    private serviceTimeLabels() {
        var key = "products:serviceTime"
        return [
            enumMsg(key, ServiceTime.STARTER),
            enumMsg(key, ServiceTime.FIRST_COURSE),
            enumMsg(key, ServiceTime.SECOND_COURSE),
            enumMsg(key, ServiceTime.DESSERT),
        ]
    }

}
