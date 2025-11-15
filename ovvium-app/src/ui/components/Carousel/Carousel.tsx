import React from "react";
import { Dimensions, Image, ImageBackground, ImageStyle, NativeScrollEvent, NativeSyntheticEvent, ScrollView, Text, View } from "react-native";
import Icon from "react-native-vector-icons/FontAwesome";
import { Picture } from '../../../model/Picture';
import { msg } from '../../../services/LocalizationService';
import { ArrayUtils } from '../../../util/ArrayUtils';
import { AppColors } from '../../styles/layout/AppColors';

interface CarouselProps {
    imageStyle: ImageStyle;
    pictures: Picture[];
    title?: string;
    showPhotoButton?: boolean;
    showTitle?: boolean;
    height?: number;
    openPickPhoto?: () => void;
}

interface CarouselState {
    pictures: Picture[];
    currentPictureIndex: number;
}

export class Carousel extends React.Component<CarouselProps, CarouselState> {

  constructor(props: CarouselProps) {
      super(props);
      this.state = {pictures: props.pictures.length == 0 ? [] : [props.pictures[0]], currentPictureIndex: 0}
  }

    static getDerivedStateFromProps(nextProps: CarouselProps, previousState: CarouselState) {
        if(nextProps.pictures.length != previousState.pictures.length) {
            return {...previousState, pictures: nextProps.pictures};
        }
        return previousState;
    }

  componentDidMount() {
      setTimeout(() => {
          //TODO Mejorar la carga de pictures. Cuando estamos en la 0, cargamos la 1 y la 2, cuando estamos en el 1 cargamos la 3 y la 4... 
          var pictures = this.props.pictures.length > 10 ? this.props.pictures.slice(0,10) : this.props.pictures
          this.setState({pictures: pictures});
      }, 100)
  }

  render() {
    var width = Dimensions.get('window').width;
    var topPosition = this.props.height ? this.props.height - 20 : 380;
    var height = this.props.height ? this.props.height : Dimensions.get('screen').height * 0.45
      if(ArrayUtils.isNotEmpty(this.props.pictures)) {
          
        return  <View>
                    <ScrollView horizontal={true} style={{height: height, width: width}} pagingEnabled={true} showsHorizontalScrollIndicator={false} 
                        onScroll={this.refreshCurrentImageIndex.bind(this)} scrollEventThrottle={16}>
                        {this.state.pictures.map((picture, index) => (
                            <Image key={picture.url + "_" + index} style={{height: height, width: width, backgroundColor:AppColors.gray}} source={{uri:picture.url}}  />
                        ))}
                    </ScrollView>
                    {this.props.pictures && this.props.pictures.length > 1 &&
                        <View style={{position:'absolute', top: topPosition, zIndex: 5, flexDirection:'row', justifyContent:'center', alignContent:'center', width:'100%'}}>
                            {this.state.pictures.map((picture, index) => (
                                <Icon name="circle" style={{marginHorizontal:3}} size={7} color={index == this.state.currentPictureIndex ? AppColors.main : AppColors.configPageBackground}/>
                            ))}
                        </View>
                    }
                </View>
                    
      }

      return(
        <View style={[{backgroundColor: AppColors.imagePlaceholderBackground, height: 400, justifyContent:'center', alignItems:'center'}, this.props.imageStyle]}>
                <Icon name="photo"  size={100} color={AppColors.white} onPress={this.props.openPickPhoto}/>
                <Text style={{color:AppColors.white, fontWeight:'500', marginTop:10, fontSize:14}}>{msg("products:actions:takeFirstPhoto")}</Text>
            {this.props.showTitle &&(
                <Text numberOfLines={2} style={{fontSize:20, fontWeight:'500', color:AppColors.white, maxWidth: '80%', position:'absolute', left: 15, bottom:15}}>{this.props.title}</Text>
            )}
      </View>
      );
  }

  renderImages() {
      return 
        {this.props.pictures.forEach(picture => (
            <ImageBackground source={{ uri: picture.url}} style={this.props.imageStyle}>
                {this.props.showTitle &&(
                    <Text numberOfLines={2} style={{fontSize:20, fontWeight:'500', color:AppColors.white, maxWidth: '80%', position:'absolute', left: 15, bottom:15}}>{this.props.title}</Text>
                )}
            </ImageBackground>
        ))}
  }

  refreshCurrentImageIndex(event: NativeSyntheticEvent<NativeScrollEvent>) {
    var width = Dimensions.get('window').width;
    let offset = event.nativeEvent.contentOffset.x;
    if(offset%width == 0) {
        var currentPictureIndex = offset/width;
        this.setState({currentPictureIndex: currentPictureIndex});
    }
}
}
