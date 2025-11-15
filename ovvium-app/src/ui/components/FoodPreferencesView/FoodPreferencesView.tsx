import React from "react";
import { Image, ScrollView, Switch, View, Platform } from 'react-native';
import { withStaticsBaseUrl } from '../../../actions/BaseAction';
import { msg } from '../../../services/LocalizationService';
import { AppColors } from '../../styles/layout/AppColors';
import { Header } from '../Header/Header';
import { MenuItem } from '../MenuItem/MenuItem';

interface FoodPreferencesViewProps {
    foodPreferences: Map<string, boolean>;
    saveFoodPreferences: (preferences?: Map<string, boolean>) => void;
    goBack: () => void;
}

interface FoodPreferencesViewState {
    foodPreferences: Map<string, boolean>;
}

export class FoodPreferencesView extends React.Component<FoodPreferencesViewProps, FoodPreferencesViewState> {

    constructor(props: FoodPreferencesViewProps) {
        super(props);
        this.state = { foodPreferences: props.foodPreferences }
    }

    render() {
        return <View style={{ backgroundColor: AppColors.white }}>
            <Header goBack={this.props.goBack}
                goBackTitle={msg("actions:back")}
                title={msg("profile:foodPreferences:label")}
                titleStyle={{ fontSize: 26 }}
                subtitle={msg("profile:foodPreferences:list")}
                format={"big"} />
            <ScrollView style={{ backgroundColor: AppColors.white, height: '100%' }}>
                {this.renderFoodPreferences()}
                <View style={{ marginBottom: 120 }} />
            </ScrollView>
        </View>
    }

    renderFoodPreferences() {
        var data: JSX.Element[] = [];
        this.props.foodPreferences.forEach((selected: boolean, foodPreference: string) => {
            var uri = withStaticsBaseUrl('/img/foodPreferences/' + foodPreference.toLowerCase() + '.png');
            data.push(<MenuItem
                title={msg("profile:foodPreferences:items:" + foodPreference.toLowerCase())}
                hideArrow={true}
                key={foodPreference}
                leftElement={
                    <Image source={{ uri: uri }} style={{ width: 35, height: 35 }} />
                }
                rightElement={
                    <Switch value={this.state.foodPreferences.get(foodPreference)}
                        thumbColor={Platform.OS == 'ios' ? undefined : this.state.foodPreferences.get(foodPreference) ? AppColors.ovviumYellow : '#DCDCDC'}
                        trackColor={Platform.OS == 'ios' ? undefined : { true: AppColors.userPlaceholderColors[7].soft, false: 'gray' }}
                        onValueChange={(selected: boolean) => {
                            var foodPreferences = new Map(this.state.foodPreferences);
                            foodPreferences.set(foodPreference, selected);
                            this.setState({ foodPreferences: foodPreferences });
                            this.props.saveFoodPreferences(foodPreferences)
                        }} />
                }
            />);
        });
        return data;
    }
}
