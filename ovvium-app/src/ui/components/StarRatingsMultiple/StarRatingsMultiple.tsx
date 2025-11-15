import React from "react";
import { View, ProgressBarAndroid, Platform, ProgressViewIOS } from 'react-native';
import { StarRatings } from '../StarRatings/StarRatings';
import { starRatingMultipleStyles } from './style';

export interface StarRatingRow {
  progressPercentage: number;
  selectedStars: number;
}

interface StarRatingsMultipleProps {
  starRatingsConfig : StarRatingRow[];
}

export class StarRatingsMultiple extends React.Component<StarRatingsMultipleProps> {
  render() {
    return (
      <View>
        {this.props.starRatingsConfig.map((row, key) => 
          <View key={key}>
            <View style={starRatingMultipleStyles.rowView}>
              <StarRatings selectedStars={row.selectedStars}/>
              {this.progressBar(row)}
            </View>
          </View>
        )}
      </View>
    );
  }

  progressBar(row: StarRatingRow) {
      var percentage = row.progressPercentage ? row.progressPercentage : 0;
      if(Platform.OS === 'android')
        return (<ProgressBarAndroid progress={percentage} styleAttr='Horizontal'
         indeterminate={false} style={starRatingMultipleStyles.progressBar} color={'grey'}/>)
      else
        return <ProgressViewIOS progress={percentage}  style={starRatingMultipleStyles.progressBar} progressTintColor='grey'/>
  }
}