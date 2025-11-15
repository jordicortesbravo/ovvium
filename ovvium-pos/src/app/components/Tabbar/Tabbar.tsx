import { IconDefinition } from '@fortawesome/fontawesome-svg-core';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { OvviumBadge } from 'app/components/OvviumBadge/OvviumBadge';
import { AppRoute } from 'app/containers/Router/AppRoute';
import * as classNames from 'classnames';
import * as React from 'react';
import * as style from './style.css';

export interface Tab {
  icon: IconDefinition;
  title: string;
  route: AppRoute;
  badge?: number;
}

export interface TabbarProps {
  tabs: Array<Tab>;
  currentRoute: string;
  goTo: (route: AppRoute) => void;
}

export class Tabbar extends React.Component<TabbarProps> {

  render() {
    var currentRoute = this.props.currentRoute;
    var goTo = this.props.goTo;
    return  <div className={classNames("row", style.tabBar)} >
                  {
                    this.props.tabs.map((tab: Tab, index:number) => {
                      var selected = currentRoute == tab.route;
                      return  <div onClick={() => goTo(tab.route)} className={classNames(style.tab, selected ? style.selected: undefined)} key={'tab-' + index} style={{width:(100/this.props.tabs.length)+'%'}}>
                                <FontAwesomeIcon icon={tab.icon} onClick={() => goTo(tab.route)}/> 
                                <div className={style.tabTitle}>{tab.title}</div>
                                  <OvviumBadge value={tab.badge} />
                              </div>
                    })
                  }
              </div>
          
  }

}