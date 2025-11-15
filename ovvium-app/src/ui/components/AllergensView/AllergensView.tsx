import React from "react";
import { Image, ScrollView, Switch, View, Platform } from 'react-native';
import { withStaticsBaseUrl } from '../../../actions/BaseAction';
import { msg } from '../../../services/LocalizationService';
import { AppColors } from '../../styles/layout/AppColors';
import { Header } from '../Header/Header';
import { MenuItem } from '../MenuItem/MenuItem';

interface AllergensViewProps {
    allergens: Map<string, boolean>;
    saveAllergens: (allergens?: Map<string, boolean>) => void;
    goBack: () => void;
}

interface AllergensViewState {
    allergens: Map<string, boolean>;
}

export class AllergensView extends React.Component<AllergensViewProps, AllergensViewState> {

    constructor(props: AllergensViewProps) {
        super(props);
        this.state = {allergens: props.allergens}
    }
    
    render() {
        return  <View style={{backgroundColor: AppColors.white}}>
                    <Header goBack={this.props.goBack} 
                        goBackTitle={msg("actions:back")} 
                        title={msg("profile:allergens:label")} 
                        format="big"
                        subtitle={msg("profile:allergens:list")}/>
                    <ScrollView style={{backgroundColor: AppColors.white, height:'100%'}}>
                        {this.renderAllergens()}
                        <View style={{marginBottom:180}}/>
                    </ScrollView>
                </View>   
    }

    renderAllergens() {
        var data: JSX.Element[] = [];
        this.props.allergens.forEach((selected: boolean, allergen: string) => {
            var uri = withStaticsBaseUrl('/img/allergens/' + allergen.toLowerCase() + '.png');
            data.push(<MenuItem 
                    title={msg("profile:allergens:items:" + allergen.toLowerCase())} 
                    hideArrow={true}
                    key={allergen}
                    leftElement={
                        <Image source={{uri:uri}} style={{width:35, height:35}} />
                    } 
                    rightElement={
                        <Switch value={this.state.allergens.get(allergen)} 
                            thumbColor={Platform.OS == 'ios' ? undefined : this.state.allergens.get(allergen) ? AppColors.ovviumYellow : '#DCDCDC'} 
                            trackColor={Platform.OS == 'ios' ? undefined : {true: AppColors.userPlaceholderColors[7].soft, false:'gray'}} 
                            onValueChange={(selected: boolean) => {
                                var allergens = new Map(this.state.allergens);
                                allergens.set(allergen, selected);
                                this.setState({ allergens })
                                this.props.saveAllergens(allergens);
                        }}/>
                    }
                />);
        });
        return data;
    }

}
