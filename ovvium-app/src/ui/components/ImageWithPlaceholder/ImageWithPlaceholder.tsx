import React from "react";
import { ImageBackground, ImageStyle, Text, View, TouchableOpacity, Image } from "react-native";
import Icon from "react-native-vector-icons/FontAwesome";
import { StringUtils } from '../../../util/StringUtils';
import { AppColors } from '../../styles/layout/AppColors';
import { msg } from '../../../services/LocalizationService';
import MultifamilyIcon, { IconFamily } from '../MultiFamilyIcon/MultifamilyIcon';

interface ImageWithPlaceholderProps {
    source?: string;
    imageStyle: ImageStyle;
    title?: string;
    imagePlaceholderSize?: number;
    showPhotoButton?: boolean;
    showPickPhotoPlaceholder?: boolean;
    showTitle?: boolean;
    touchable?: boolean;
    asBackground?: boolean;
    openPickPhoto?: () => void;
}

interface ImageWithPlaceholderState {
    imagePlaceholderSize: number;
    showPickPhotoPlaceholder: boolean;
    touchable: boolean;
    asBackground: boolean;
}

export class ImageWithPlaceholder extends React.Component<ImageWithPlaceholderProps,ImageWithPlaceholderState> {

    constructor(props: ImageWithPlaceholderProps) {
        super(props);
        this.state = {
            showPickPhotoPlaceholder: props.showPickPhotoPlaceholder ? props.showPickPhotoPlaceholder : true,
            imagePlaceholderSize: props.imagePlaceholderSize ? props.imagePlaceholderSize : 90,
            touchable: props.touchable !== undefined ? props.touchable : false,
            asBackground: props.asBackground !== undefined ? props.asBackground : true
        }
    }

    render() {
      if(StringUtils.isNotBlank(this.props.source)) {
        return  <TouchableOpacity activeOpacity={1} onPress={this.doPress.bind(this)}>
                    {this.state.asBackground &&
                        <ImageBackground source={{ uri: this.props.source}} style={[this.props.imageStyle]}>
                            {this.props.showTitle &&(
                                <Text numberOfLines={2} style={{fontSize:20, fontWeight:'500', color:AppColors.white, maxWidth: '80%', position:'absolute', left: 15, bottom:15}}>{this.props.title}</Text>
                            )}
                            {this.props.showPhotoButton &&(
                                <Icon name="camera"  size={24} color={AppColors.white} style={{position:'absolute', right:15, bottom: 15}} onPress={this.props.openPickPhoto}/>
                            )}
                        </ImageBackground>
                    }
                    {!this.state.asBackground &&
                            <Image source={{ uri: this.props.source}} style={this.props.imageStyle} />
                    }
                </TouchableOpacity>
      }

      return(
        <View style={[{backgroundColor: AppColors.imagePlaceholderBackground, justifyContent:'center', alignItems:'center'}, this.props.imageStyle]}>
                <MultifamilyIcon name="ios-images" size={this.state.imagePlaceholderSize} family={IconFamily.ION} color={AppColors.white} onPress={this.props.openPickPhoto} />
            {this.props.showPickPhotoPlaceholder &&
                <Text style={{color:AppColors.white, fontWeight:'500', marginTop:10, fontSize:14}}>{msg("products:actions:takeFirstPhoto")}</Text>
            }
            {this.props.showTitle &&(
                <Text numberOfLines={2} style={{fontSize:20, fontWeight:'500', color:AppColors.white, maxWidth: '80%', position:'absolute', left: 15, bottom:15}}>{this.props.title}</Text>
            )}
            {this.props.showPhotoButton &&(
                <Icon name="camera"  size={24} color={AppColors.white} style={{position:'absolute', right:15, bottom: 15}} onPress={this.props.openPickPhoto}/>
            )}
      </View>
      );
  }

  doPress() {
    if(this.props.touchable && this.props.openPickPhoto) {
        this.props.openPickPhoto();
    }
  }
}
