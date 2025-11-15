import React from "react";
import Icon from "react-native-vector-icons/FontAwesome";
import MaterialIcon from 'react-native-vector-icons/MaterialCommunityIcons';
import EvilIcon from "react-native-vector-icons/EvilIcons";
import IonIcon from 'react-native-vector-icons/Ionicons';
import FeatherIcon from 'react-native-vector-icons/Feather';
import OctIcon from 'react-native-vector-icons/Octicons';
import SimpleLineIcon from 'react-native-vector-icons/SimpleLineIcons';
import { AppColors } from '../../styles/layout/AppColors';
import { TextStyle, ImageStyle } from "react-native";

export enum IconFamily {
    FONT_AWESOME = 'FontAwesome',
    MATERIAL_COMMUNITY = 'MaterialCommunity',
    EVIL = 'Evil',
    ION = "Ion",
    FEATHER = "Feather",
    OCT = "Oct",
    SIMPLE_LINE = "SimpleLine"
}

export interface MultifamilyIconProps {
    family?: IconFamily; //Incorporar nuevas familias en caso de necesidad. Por defecto es FontAwesome
    name: string;
    size?: number;
    color?: string;
    style?: TextStyle[] | TextStyle | ImageStyle | Array<TextStyle|ImageStyle|{ color: string | undefined} | undefined> | { color: string | undefined} | undefined[];
    onPress?: () => void; 
}

export default class MultifamilyIcon extends React.Component<MultifamilyIconProps> {

    render() {
        var color = this.props.color ? this.props.color : AppColors.white;
        var size = this.props.size ? this.props.size : 24;
        switch(this.props.family) {
            case IconFamily.FONT_AWESOME:
                return <Icon name={this.props.name}  size={size} color={color} onPress={this.props.onPress} style={this.props.style}/>;
            case IconFamily.MATERIAL_COMMUNITY:
                return <MaterialIcon name={this.props.name} color={color} size={size} onPress={this.props.onPress} style={this.props.style} />
            case IconFamily.EVIL:
                return <EvilIcon size={size} color={color} name={this.props.name} onPress={this.props.onPress} style={this.props.style}/>
            case IconFamily.ION:
                return <IonIcon size={size} color={color} name={this.props.name} onPress={this.props.onPress} style={this.props.style}/>
            case IconFamily.FEATHER:
                return <FeatherIcon size={size} color={color} name={this.props.name} onPress={this.props.onPress} style={this.props.style}/>
            case IconFamily.OCT:
                return <OctIcon size={size} color={color} name={this.props.name} onPress={this.props.onPress} style={this.props.style}/>
            case IconFamily.SIMPLE_LINE:
                return <SimpleLineIcon size={size} color={color} name={this.props.name} onPress={this.props.onPress} style={this.props.style}/>
            default: 
                return <Icon name={this.props.name}  size={size} color={color} onPress={this.props.onPress} style={this.props.style}/>;
        }
    }
}