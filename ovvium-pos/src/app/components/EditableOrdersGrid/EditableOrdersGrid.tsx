import { Tile } from 'app/components/Tile/Tile';
import { Order } from 'app/model/Order';
import { Product } from 'app/model/Product';
import * as classNames from 'classnames';
import * as React from 'react';
import * as style from './style.css';
import { OrderGroup } from '../../model/enum/OrderGroup';
import { BillService } from '../../services/BillService';
import { IssueStatus } from 'app/model/enum/IssueStatus';
import { Checkbox } from '@material-ui/core';
import { ArrayUtils } from 'app/utils/ArrayUtils';


interface EditableOrdersGridProps {
  selectedOrders: Array<Order>;
  showSelectable?: boolean;

  onClickPlusProduct:(product: Product) => void;
  onClickMinusProduct:(order: Order) => void;
  onClickOrderGroup: (orderGroup: OrderGroup) => void;
  onChangeCheckedSelectableOrders: (orders: Array<Order>) => void;
}

interface EditableOrderGridState {
  checkedSelectableOrders: Array<Order>;
}

export class EditableOrdersGrid extends React.Component<EditableOrdersGridProps, EditableOrderGridState> {

  constructor(props) {
    super(props);
    this.state = {checkedSelectableOrders: []};
  }

  static getDerivedStateFromProps(props: EditableOrdersGridProps, state: EditableOrderGridState) {
    if(!props.showSelectable) {
      return {checkedSelectableOrders: []};
    }
    return state;
  }

  render() {
    var orders = BillService.listOrderGroups(this.props.selectedOrders);
    return  <>{orders.map(orderGroup => this.renderOrder(orderGroup))}</>
  }

  renderOrder(orderGroup: OrderGroup) {
    const norders = orderGroup.orders.filter(o => o.issueStatus == IssueStatus.PENDING || o.issueStatus == IssueStatus.PREPARING).length;
    return  <div key={orderGroup.product.id} className={style.editableOrderGroup}>
    
                    {!this.props.showSelectable && <Tile value="-" className={classNames(style.editOrderGroupButton)} onClick={() => this.props.onClickMinusProduct(orderGroup.orders[0])}/>}
                    {this.props.showSelectable && 
                      <Tile className={classNames(style.editOrderGroupButton)} onClick={() => {}}>
                        <Checkbox onChange={(event) => {
                            var stateOrders = this.state.checkedSelectableOrders;
                            orderGroup.orders.forEach(o => {
                              if(event.target.checked && !ArrayUtils.contains(stateOrders, o, 'id')) {
                                  stateOrders.push(o);
                              } else if(!event.target.checked && ArrayUtils.contains(stateOrders, o, 'id')){
                                ArrayUtils.remove(stateOrders, o, 'id');
                              }
                            });
                            var checkedSelectableOrders = stateOrders.slice();
                            this.setState({checkedSelectableOrders});
                            this.props.onChangeCheckedSelectableOrders(checkedSelectableOrders);
                        }}/>
                      </Tile>
                    }
                    <Tile value={orderGroup.product.name} className={style.tile} onClick={() => {
                      if(!this.props.showSelectable) {
                        this.props.onClickOrderGroup(orderGroup)
                      }
                    }} badge={norders ? norders : undefined}/>
                    <Tile value={orderGroup.orders.length.toString()} className={classNames(style.tile, style.quantityTile)} />
                    {!this.props.showSelectable && <Tile value="+" className={classNames(style.editOrderGroupButton)} onClick={() => this.props.onClickPlusProduct(orderGroup.product)}/>}
                    {this.props.showSelectable && <Tile value="" className={classNames(style.editOrderGroupButton)} onClick={() => {}}/>}
            </div>
  }
}