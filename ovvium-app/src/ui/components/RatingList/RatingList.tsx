import React from "react";
import { FlatList, ListRenderItemInfo, Text, View } from 'react-native';
import { Rating } from '../../../model/Rating';
import { AppColors } from '../../styles/layout/AppColors';
import { msg } from '../../../services/LocalizationService';
import { StarRatings } from '../StarRatings/StarRatings';
import { ratingItemStyles } from './style';

interface RatingListProps {
    ratings: Rating[];
}

export class RatingList extends React.Component<RatingListProps> {

    render() {
        return <FlatList
            data={this.props.ratings.filter(r => r.comment)}
            renderItem={this.renderItem}
            keyExtractor={(item,index) => item.id + index}
          /> 
    }

    private renderItem = (info: ListRenderItemInfo<Rating>) => (
        <View style={ratingItemStyles.container}>
            <Text style={ratingItemStyles.lastUpdate}>{this.getDateDescription(info.item.lastUpdate)}</Text>
            <Text style={ratingItemStyles.user}>{info.item.user}</Text>
            <StarRatings color={AppColors.ovviumYellow} selectedStars={info.item.rating} size={14}/>
            <Text style={ratingItemStyles.comment}>{info.item.comment}</Text>
        </View>
    );

    private getDateDescription(date: Date) {
        var timeDiff = Math.abs(new Date().getTime() - date.getTime());
        var dayDifference = Math.floor(timeDiff / (1000 * 3600 * 24));
        if(dayDifference == 0) {
            return msg("elapsedTime:today")
        } else if(dayDifference == 1) {
            return msg("elapsedTime:yesterday")
        } else if(dayDifference < 7) {
            return msg("elapsedTime:lessThanWeek",{0: dayDifference});
        } else if(dayDifference < 12) {
            return msg("elapsedTime:oneWeek");
        } else if(dayDifference < 20) {
            return msg("elapsedTime:lessThanMonth", {0: 2});
        } else if(dayDifference < 25) {
            return msg("elapsedTime:lessThanMonth", {0: 3});
        }  else if(dayDifference < 40) {
            return msg("elapsedTime:oneMonth");
        } else {
            return date.toISOString().substr(0, 10);
        }

    }
}