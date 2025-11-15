import React from "react";
import { Text, TouchableOpacity, View } from "react-native";
import { circularButtonGroupStyle } from './style';

interface CircularButtonGroupProps {
  values: any[];
  defaultSelection?: string;
  onChangeValue: (value: any) => void;
}

interface CircularButtonGroupState {
    selected?: string;
}

export class CircularButtonGroup<T extends object> extends React.Component<CircularButtonGroupProps, CircularButtonGroupState> {

    constructor(props: CircularButtonGroupProps) {
        super(props);
        this.state = {selected: props.defaultSelection};
    }

    render() {
            return (
                <View style={circularButtonGroupStyle.container}>
                    {this.renderButtons()}
                </View>
        );
    }

    private renderButtons() : JSX.Element[] {
        return this.props.values.map((value: T) => {
            return this.renderButton(value);
        }) 
    }

    private renderButton(value: any): JSX.Element {
        var selected = this.state.selected == value.toString();
        return  <TouchableOpacity key={'buttonGroup-' + value} onPress={() => this.onChangeValue(value)}>
                    <View style={selected ? circularButtonGroupStyle.selectedButton : circularButtonGroupStyle.unselectedButton}>
                        <Text style={selected ? circularButtonGroupStyle.selectedText : circularButtonGroupStyle.unselectedText}>{value.toString()}</Text>
                    </View>
                </TouchableOpacity>
    }

    private onChangeValue(selectedValue: T) {
        this.setState({selected: selectedValue.toString()});
        this.props.onChangeValue(selectedValue);
    }
}
