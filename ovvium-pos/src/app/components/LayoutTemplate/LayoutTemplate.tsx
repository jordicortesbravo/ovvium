import { faCog, faList, faMapMarker, faPenSquare } from '@fortawesome/free-solid-svg-icons';
import { Tabbar } from 'app/components/Tabbar/Tabbar';
import { AppRoute } from 'app/containers/Router/AppRoute';
import { Bill } from 'app/model/Bill';
import { BillService } from 'app/services/BillService';
import * as classNames from 'classnames';
import * as React from 'react';
import * as style from './style.css';
import { ThemeProvider } from '@material-ui/core';
import { ovviumTheme } from 'app/config/MaterialUiConfig';


interface LayoutTemplateProps {
  currentRoute: string;
  selectedBill?: Bill;
  goToRoute: (route: AppRoute) => void;
}

export class LayoutTemplate extends React.Component<LayoutTemplateProps> {
  
  render() {
    if(this.props.currentRoute == AppRoute.LOGIN || this.props.currentRoute == AppRoute.FORGOT_PASSWORD) {
      return  <div className={classNames('h-100', style.body)}>
                {this.props.children}
              </div>
    }
    var showTabbar = this.props.currentRoute.indexOf("kitchen") == -1;

    return  <div className={classNames('h-100', style.body)}>
              <div className={showTabbar ? style.container : style.fullContainer}>
                <ThemeProvider theme={ovviumTheme}>
                  {this.props.children}
                </ThemeProvider>
              </div>
              {showTabbar&&
                <Tabbar tabs={[
                      {icon: faMapMarker, title: "Mesas", route: AppRoute.LOCATIONS},
                      {icon: faList, title: "Cuenta", route: AppRoute.BILL, badge: BillService.getPendingIssueOrders(this.props.selectedBill).length},
                      {icon: faPenSquare, title:'Productos', route: AppRoute.TAKE_ORDER}, 
                      {icon: faCog, title: "Herramientas", route: AppRoute.CONFIG},
                  ]} 
                  currentRoute={this.props.currentRoute}
                  goTo={this.props.goToRoute}
                  />
                }
            </div>
  }
}
