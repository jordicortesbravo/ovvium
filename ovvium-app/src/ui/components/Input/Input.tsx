import React from "react";
import { Animated, KeyboardTypeOptions, Text, TextInput, View, Easing } from 'react-native';
import { ViewStyle, TextStyle } from 'react-native';
import { StringUtils } from '../../../util/StringUtils';
import { AppColors } from '../../styles/layout/AppColors';
import MultifamilyIcon, { IconFamily } from '../MultiFamilyIcon/MultifamilyIcon';
import { AppFonts } from "../../styles/layout/AppFonts";

export interface InputProps {
    keyboardType?: KeyboardTypeOptions;
    placeholder: string;
    value?: string;
    defaultValue?: string;
    containerStyle?:ViewStyle;
    style?: TextStyle;
    placeholderTextColor?: string;
    selectionColor?: string;
    maxLength?: number;
    editable?: boolean;
    multiline?:boolean;
    secureText?: boolean;
    autoFocus?: boolean;
    showEditIcon?: boolean;
    validator?: (text?: string) => void;
    onChangeText?: (text: string) => void
    onFocus?: () => void;
    onBlur?: () => void;
    onSubmitEditing?: () => void;
    showValidationErrors?: boolean;
}

interface InputState {
    hintOpacity: Animated.Value;
    validationOpacity: Animated.Value;
    hintColor: string;
    value?: string;
    isFocused: boolean;
    autoFocus: boolean;
    validationErrorMessage?: string;
}

export class Input extends React.Component<InputProps, InputState> {

    textInput?: TextInput;

    constructor(props: InputProps) {
        super(props);
        var hintOpacity = new Animated.Value(1);
        var validationOpacity = new Animated.Value(0);
        if(StringUtils.isBlank(props.value)) {
            hintOpacity = new Animated.Value(0);
        }
        this.state = {hintOpacity, validationOpacity, hintColor: AppColors.secondaryText, value: props.value, isFocused: false, autoFocus: this.props.autoFocus == true};
    }

    render() {
        const keyboardType = this.props.keyboardType ? this.props.keyboardType : "default";
        const style = this.props.containerStyle ? this.props.containerStyle : {marginLeft: '10%'}
        const color = this.state.validationErrorMessage ? AppColors.red : this.state.isFocused ? AppColors.secondaryText : AppColors.ovviumYellow;
        const placeholderTextColor = this.props.placeholderTextColor ?  this.props.placeholderTextColor : AppColors.placeholderAndroid;
        const selectionColor = this.props.selectionColor ? this.props.selectionColor : undefined;
        return (
            <View style={style}>
                <Animated.View style={{marginBottom: -7, zIndex:2,opacity: this.state.hintOpacity}}>
                    <Text style={{color:this.state.hintColor, fontSize:12, paddingLeft:15, fontFamily: AppFonts.medium}}>{this.props.placeholder}</Text>
                </Animated.View>
                <TextInput
                    ref={(input: TextInput) => this.textInput = input}
                    style={this.props.style ? this.props.style : {marginTop: 10, paddingVertical:15, paddingHorizontal: 15, justifyContent:"flex-end",alignContent:'flex-end', color: AppColors.mainText, fontSize:16, fontFamily: AppFonts.regular, backgroundColor: this.state.isFocused ? AppColors.white : '#F3F2F9' , borderWidth:2, borderColor:'#F3F2F9', borderRadius: 8}}
                    autoCapitalize="none"
                    autoCorrect={false}
                    autoFocus={this.state.autoFocus}
                    onSubmitEditing={this.props.onSubmitEditing}
                    placeholderTextColor={placeholderTextColor}
                    selectionColor={selectionColor}
                    keyboardType={keyboardType}
                    onFocus={this.onFocus.bind(this)}
                    onBlur={this.onBlur.bind(this)}
                    onChangeText={this.onChangeText.bind(this)}
                    editable={this.props.editable}
                    multiline={this.props.multiline}
                    secureTextEntry={this.props.secureText}
                    underlineColorAndroid={"transparent"}
                    returnKeyType="next"
                    maxLength={this.props.maxLength}
                    placeholder={this.state.isFocused ? '' : this.props.placeholder}
                    value={this.state.value}
                    defaultValue={this.props.defaultValue}
                />
                {this.props.showEditIcon && !this.state.isFocused && 
                    <MultifamilyIcon family={IconFamily.EVIL} name="pencil" color={AppColors.secondaryText} size={40} style={{position: 'absolute', right: 7, top: 10}} onPress={this.focus.bind(this)} />
                }
                <Animated.View style={{opacity: this.state.validationOpacity}}>
                    <Text style={{color:color, fontSize:12, paddingLeft:5, marginBottom:10, fontFamily: AppFonts.regular}}>{this.state.validationErrorMessage}</Text>
                </Animated.View>
            </View>
        )
    }

    focus() {
        if(this.textInput) {
            this.textInput.focus();
        }
        
    }

    UNSAFE_componentWillReceiveProps(newProps: InputProps) {
        if(newProps.showValidationErrors) {
            this.validate(this.state.value);
        }
    }

    onChangeText(text:string) {
        this.setState({value:text});
        this.animateHeader(this.state.hintOpacity, 1);
        if(this.props.onChangeText) {
            this.props.onChangeText(text);
        }
    }

    onFocus() {
        if(StringUtils.isBlank(this.state.value)) {
            this.animateHeader(this.state.hintOpacity, 1);
        }
        this.setState({hintColor: AppColors.ovviumYellow, isFocused: true})
        if(this.props.onFocus) {
            this.props.onFocus();
        }
    }

    onBlur() {  
        this.validate(this.state.value);
        if(this.props.onBlur) {
            this.props.onBlur();
        }
    }

    validate(text: string|undefined) {
        var validationErrorMessage = undefined;
        if(this.props.validator) {
            try {
                this.props.validator(text);
                this.setState({validationErrorMessage: undefined})
            } catch(error) {
                validationErrorMessage = error.message;
                this.setState({validationErrorMessage: error.message})
            }
            if(validationErrorMessage) {
                this.animateHeader(this.state.validationOpacity, 1);
            }
        }
        if(StringUtils.isBlank(text)) {
            this.animateHeader(this.state.hintOpacity, 0);
        }
        this.setState({hintColor: validationErrorMessage ? AppColors.red : AppColors.secondaryText, isFocused: false})
    }

    animateHeader(anim: Animated.Value, toValue:0|1) {
        Animated.timing(anim, {
            toValue:toValue,
            easing: Easing.linear,
            duration: 50,
            useNativeDriver: true
        }).start();
    }
}