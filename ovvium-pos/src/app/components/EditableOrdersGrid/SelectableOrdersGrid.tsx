import { Tile } from 'app/components/Tile/Tile';
import { Order } from 'app/model/Order';
import { Product } from 'app/model/Product';
import * as classNames from 'classnames';
import * as React from 'react';
import * as style from './style.css';
import { ArrayUtils } from '../../utils/ArrayUtils';


interface SelectableOrdersGridProps {
  selectedOrders: Array<Order>;
  availableOrders: Array<Order>;

  onAddOrder: (order: Order) => void;
  onRemoveOrder: (order: Order) => void;
}

interface SelectableOrdersGridState {
    selectedOrders: Map<string, Array<Order>>; //La key es el id de producto
}

export class SelectableOrderGroup{
    product: Product;
    orders: Array<Order>;
  
    constructor(product: Product, orders: Array<Order>) {
      this.product = product;
      this.orders = orders;
    }
  }

export class SelectableOrdersGrid extends React.Component<SelectableOrdersGridProps, SelectableOrdersGridState> {

    constructor(props) {
        super(props);
        this.state = {selectedOrders: this.selectedOrdersAsMap(props.selectedOrders)};
    }

    render() {
        var orders = this.listOrderGroups(this.props.availableOrders);
        return  <>{orders.map(orderGroup => this.renderOrder(orderGroup))}</>
    }

    renderOrder(orderGroup: SelectableOrderGroup) {
        return  <div key={orderGroup.product.id} className={style.editableOrderGroup}>
                    <Tile value="-" className={classNames(style.editOrderGroupButton)} onClick={() => this.onRemoveOrder(orderGroup)}/>
                    <Tile value={orderGroup.product.name} className={style.selectableOrderTile}/>
                    <Tile value={orderGroup.orders.length.toString()} className={classNames(style.tile, style.fixedQuantityTile)}/>
                    <Tile value={this.countSelectedOrders(orderGroup).toString()} className={classNames(style.tile, style.quantityTile)}/>
                    <Tile value="+" className={classNames(style.editOrderGroupButton)} onClick={() => this.onAddOrder(orderGroup)}/>
                </div>
    }

    private onAddOrder(orderGroup: SelectableOrderGroup) {
        var selectedOrders = this.state.selectedOrders.get(orderGroup.product.id);
        if(!selectedOrders) {
            selectedOrders = [];
            this.state.selectedOrders.set(orderGroup.product.id, selectedOrders);
        }
        if(selectedOrders.length < orderGroup.orders.length) {
            for(var i in orderGroup.orders) {
                var order = orderGroup.orders[i];
                if(!ArrayUtils.contains(selectedOrders, order, 'id')) {
                    selectedOrders.push(order);
                    this.props.onAddOrder(order);
                    break;
                }
            }
        }
    }

    private onRemoveOrder(orderGroup: SelectableOrderGroup) {
        var selectedOrders = this.state.selectedOrders.get(orderGroup.product.id);
        if(selectedOrders && selectedOrders.length > 0) {
            for(var i in orderGroup.orders) {
                var order = orderGroup.orders[i];
                if(ArrayUtils.contains(selectedOrders, order, 'id')) {
                    ArrayUtils.remove(selectedOrders, order, 'id');
                    this.props.onRemoveOrder(order);
                    break;
                }
            }
        }
    }

    private countSelectedOrders(orderGroup: SelectableOrderGroup) {
        return ArrayUtils.intersection(orderGroup.orders, this.state.selectedOrders.get(orderGroup.product.id), 'id').length;
    }

    private listOrderGroups(orders: Array<Order>) {
        var map = new Map<string, SelectableOrderGroup>();
        orders.forEach(o => {
            var productId = o.product.id;
            if(map.has(productId)) {
                map.get(productId)!.orders.push(o);
            } else {
                map.set(productId, new SelectableOrderGroup(o.product, [o]))
            }
        });
        return  Array.from(map.values()).sort((og1, og2) => {
                    return og1.product.name >= og2.product.name ? 1 : -1;
                });
    }

    private selectedOrdersAsMap(orders: Order[]) {
        var map = new Map<string, Array<Order>>()
        orders.forEach(order => {
            var productId = order.product.id;
            if(!map.has(productId)) {
                map.set(productId, []);
            }
            map.get(productId)!.push(order);
        })
        return map;
    }
}