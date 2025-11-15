import * as React from 'react';
import { Animated, Dimensions, Easing, TextInput, TouchableHighlight, TouchableOpacity, View } from 'react-native';
import Ionicons from 'react-native-vector-icons/Ionicons';
import { iosSearchBarStyles } from './style';
import { AppColors } from '../../styles/layout/AppColors';

interface IOSSearchBarProps {

  placeholder: string;
  onChangeText: (text: string) => void
  onFocus?: () => void;
  onBlur?: () => void;
  cancelText: string;
}

interface IOSSearchBarState {
  filteredText: string;
}

export class IOSSearchBar extends React.Component<IOSSearchBarProps,IOSSearchBarState>  {

  constructor(props:IOSSearchBarProps) {
    super(props);
    this.state = {filteredText: ''};
  }

  render() {
      return (
          <InternalIOSSearchBar 
            placeholder={this.props.placeholder} 
            cancelText={this.props.cancelText} 
            onValueChange={(text:string) => {
              this.setState({filteredText: text});
              this.props.onChangeText(text);
            }} 
            onSearchFocus={this.props.onFocus}
            onSearchBlur={this.props.onBlur}
            value={this.state.filteredText} />
      );
  }
}

type InternalIOSSearchBarProps = {
  onValueChange?: (text: string) => void,
  placeholder?: string,
  value: string,
  cancelText?: string,
  onSearchFocus?: () => void,
  onSearchBlur?: () => void,
};

type InternalIOSSearchBarState = {
  anim: any,
  cancelWidth: number,
};


class InternalIOSSearchBar extends React.Component<InternalIOSSearchBarProps, InternalIOSSearchBarState> {

  state = {
    anim: new Animated.Value(0),
    cancelWidth: 50
  };

  _input:TextInput|undefined|null = undefined;

  clearInput = (): void =>
    this.props.onValueChange && this.props.onValueChange('');

  cancelInput = (): void => {
    this.props.onValueChange && this.props.onValueChange('');
    if (this._input) this._input.blur();
  };

  focusInput = (): void => {
    if (this._input) this._input.focus();
  };

  handleInputFocus = (): void => {
    this.animateTo(1);
    if (typeof this.props.onSearchFocus === 'function') this.props.onSearchFocus();
  };

  handleInputBlur = (): void => {
    this.animateTo(0);
    if (typeof this.props.onSearchBlur === 'function') this.props.onSearchBlur();
  };

  handleLayout = (event: any) => {
    this.setState({ cancelWidth: event.nativeEvent.layout.width});
  };

  animateTo = (toValue: 1 | 0): void => {
    Animated.timing(this.state.anim, {
      toValue,
      easing: Easing.linear,
      duration: 200,
      useNativeDriver: false
    }).start();
  };

  render() {
    const {
      value,
      placeholder,
      onValueChange,
      cancelText,
    } = this.props;

    const { anim, cancelWidth } = this.state;
    const width:number = Dimensions.get('window').width - 17;
    
    return (
      <View style={[{ width }, iosSearchBarStyles.container]}>
        <TouchableHighlight 
          underlayColor='#F8F8F8' 
          onPress={this.focusInput} 
          onLongPress={this.focusInput}
          style={iosSearchBarStyles.inputTouchWrapper}
        >
          <Animated.View
            style={[
              iosSearchBarStyles.inputWrapper,
              { 
                width: this.state.anim.interpolate({
                      inputRange: [0, 1],
                      outputRange: [width - 20, width - 20 - cancelWidth],
                    })
              },
            ]}
          >
            <Ionicons name="ios-search" color="#8A8A8F" style={iosSearchBarStyles.searchIcon} size={18} />
            <TextInput
              ref={ref => (this._input = ref)}
              style={ iosSearchBarStyles.input}
              value={value}
              onChangeText={onValueChange}
              placeholder={placeholder}
              placeholderTextColor='#8A8A8F'
              onFocus={this.handleInputFocus.bind(this)}
              onBlur={this.handleInputBlur.bind(this)}
              {...this.props}
            />
            {value ? (
              <TouchableOpacity onPress={this.clearInput}>
                <Ionicons name="ios-close-circle" color='#8A8A8F' style={iosSearchBarStyles.clearIcon} size={20} />
              </TouchableOpacity>
            ) : null}
          </Animated.View>
        </TouchableHighlight>
        <View onLayout={this.handleLayout}>
          <TouchableOpacity onPress={this.cancelInput}>
            <Animated.Text
              style={[
                iosSearchBarStyles.cancelText,
                {
                  color: AppColors.main,
                  opacity: anim,
                  transform: [
                    {
                      translateX: anim.interpolate({
                            inputRange: [0, 1],
                            outputRange: [cancelWidth - 20, 0],
                          })
                      ,
                    },
                  ],
                },
              ]}
            >
              {cancelText}
            </Animated.Text>
          </TouchableOpacity>
        </View>
      </View>
    );
  }
}


