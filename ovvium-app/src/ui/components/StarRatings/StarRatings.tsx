import React from "react";
import { View, TextStyle, Text } from "react-native";
import Ionicons from 'react-native-vector-icons/Ionicons';
import { AppColors } from '../../styles/layout/AppColors';
import { starRatingsStyles } from './style';
import { AppFonts } from "../../styles/layout/AppFonts";


interface StarRatingsProps {
  selectedStars : number;
  withEmptyStars?: boolean;
  maxStars?: number;
  selectable? : boolean;
  size?: number;
  color?: string;
  iconStyle?: TextStyle;
  starTexts?: Array<string>;
  onSelect?: (index: number) => void;
}

interface StarRatingsState {
  selectedStars: number;
}

export class StarRatings extends React.Component<StarRatingsProps, StarRatingsState> {

  constructor(props: StarRatingsProps) {
    super(props);
    this.state = {selectedStars : this.props.selectedStars !== undefined ? this.props.selectedStars : 3};
  }

  render() {
    return (
      <View>
        <View style={starRatingsStyles.container}>
          {this.renderStars()}
        </View>
        {this.props.starTexts &&
          <View style={{justifyContent: 'center', alignItems: 'center', marginTop: 15}}>
            <Text style={{fontSize: 28, fontFamily: AppFonts.bold, color: this.getColor(this.state.selectedStars)}}>{this.props.starTexts[this.state.selectedStars-1]}</Text>
          </View>
        }
      </View>
    );
  }

  private getColor(selectedStars: number) {
    switch(selectedStars) {
      case 1:
        return AppColors.red;
      case 2:
        return AppColors.orange;
      case 3:
        return AppColors.ovviumBlue;
      case 4:
        return AppColors.funnyGreen;
      case 5: 
        return "green";
    }
    return AppColors.mainText;
  }

  private renderStars() : JSX.Element[] {
    var initialStars = this.isSelectable() ? this.state.selectedStars : this.props.selectedStars;
    var stars = [...Array(initialStars)].map((i, index) => {
        return this.star(index, true);
      }
    );
    if(this.props.withEmptyStars !== undefined && this.props.withEmptyStars!) {
      var emptyStars = [...Array(this.props.maxStars! - initialStars)].map((j, index) => {
          return this.star(index + initialStars, false);
      });
      stars = stars.concat(emptyStars);
    }
    return stars;
  }

  private star(index: number, filled: boolean): JSX.Element {
    return <Ionicons key={'star-icon-' + index} 
      name="md-star" 
      size={this.props.size ? this.props.size : 11} 
      color={filled ? (this.props.color ? this.props.color : AppColors.ovviumYellow) : AppColors.imagePlaceholderBackground} 
      style={[{ marginLeft: 1}, this.props.iconStyle]} 
      onPress={() => this.updateSelected(index)}/>;

      
  }

  private updateSelected(index: number): void {
    if(this.isSelectable()) {
      let selected = index +1;
      if(this.props.onSelect)
        this.props.onSelect(selected);
      this.setState({selectedStars : selected});
    }
  }
 
  private isSelectable(): boolean {
    return this.props.selectable !== undefined && this.props.selectable!;
  }

}