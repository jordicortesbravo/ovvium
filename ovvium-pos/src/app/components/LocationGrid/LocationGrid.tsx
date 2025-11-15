import { Bill } from 'app/model/Bill';
import { BillService } from 'app/services/BillService';
import * as classNames from 'classnames';
import * as React from 'react';
import { Tile } from '../Tile/Tile';
import { Location } from './../../model/Location';
import * as style from './style.css';
import { ArrayUtils } from '../../utils/ArrayUtils';
import { Utils } from 'app/utils/Utils';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { faLink, faTrash } from '@fortawesome/free-solid-svg-icons';

interface LocationGridProps {
  locationBillMap: Map<Location, Bill | undefined>;
  selectedLocations: Array<Location>;
  selectedBill?: Bill;
  joiningTables: boolean;
  removingBills: boolean;

  onClickLocation: (location: Location, bill?: Bill) => void;
}

export class LocationGrid extends React.Component<LocationGridProps> {

  render() {
    return <>{this.renderLocations()}</>
  }

  private renderLocations() {
    var comps: Array<JSX.Element> = [];
    this.props.locationBillMap.forEach((bill, location) => {
      var cssClass = bill != undefined ? BillService.hasPendingIssueOrders(bill) ? style.openBillPendingOrders : style.openBill : '';
      comps.push(this.renderLocation(location, bill, cssClass));
    });
    return comps;
  }

  private renderLocation(location: Location, bill: Bill | undefined, cssClass: string) {
    var pendingOrders = BillService.getPendingIssueOrders(bill);
    var oldestOrderTime = BillService.getOldestPendingOrderTime(bill);
    var selected = ArrayUtils.contains(this.props.selectedLocations, location, 'id');
    const tileClassNames = classNames(style.tile,
      cssClass,
      this.props.joiningTables ? (selected ? style.tileSelected : style.tileJoiningTable) : '',
      this.props.removingBills && bill ? (this.props.selectedBill?.id == bill.id ? style.tileSelected : style.tileRemovingBill) : '',
    );
    return <Tile
      key={location.id}
      className={tileClassNames}
      selected={selected}
      onClick={() => this.props.onClickLocation(location, bill)}>
      <div className={style.tileTitle}>{location.description}</div>
      {bill &&
        <div className={style.pendingOrdersCount}>
          <div className={classNames(style.pendingOrders, cssClass)}>
            {!this.props.removingBills && pendingOrders.length}
            {this.props.removingBills && <div className={style.removeIcon}>
              <FontAwesomeIcon icon={faTrash} />
            </div>}
          </div>

        </div>
      }
      {pendingOrders.length > 0 && oldestOrderTime &&
        <div className={style.elapsedOrderTime}>{Utils.getElapsedTime(oldestOrderTime)}</div>
      }


      {bill && bill.locations.length > 1 &&
        <FontAwesomeIcon icon={faLink} className={style.joinedTable} />
      }
    </Tile>
  }
}
