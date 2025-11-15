import { Tile } from 'app/components/Tile/Tile';
import { ServiceTime } from 'app/model/enum/ServiceTime';
import * as classNames from 'classnames';
import * as React from 'react';
import { getServiceTimeLabel } from '../../model/enum/ServiceTime';
import { Order } from '../../model/Order';
import { EnumUtils } from '../../utils/EnumUtils';
import * as style from './style.css';
import { Utils } from '../../utils/Utils';
import { ProductType } from 'app/model/enum/ProductType';
import { ProductGroupConfigurerView, ProductGroupConfigurerViewState } from 'app/components/ProductGroupConfigurer/Products/ProductGroupConfigurerView';
import { ProductGroup } from 'app/model/ProductGroup';
import { Product } from 'app/model/Product';
import { Row } from 'react-bootstrap';

interface EditableOrderProps {
    order: Order;
    product: Product;
    onChangeOrder: (order: Order) => void;
}

interface EditableOrderState {
    serviceTime: ServiceTime;
    notes: string;
}

export class EditableOrder extends React.Component<EditableOrderProps, EditableOrderState> {

    constructor(props) {
        super(props);
        this.state = { serviceTime: !props.order.serviceTime ? ServiceTime.SOONER : props.order.serviceTime, notes: props.order.notes ? props.order.notes : '' };
    }

    render() {
        return <Row className={classNames("w-100", style.groupOrderItem)}>
            <Tile value={this.props.order.product.name}  clickable={false} className={classNames(style.tile, style.editableOrderTile)}>
                <div className={style.editableOrderDescription}>{this.getOrderResume()}</div>
            </Tile>
            {this.props.order.product.type !== ProductType.GROUP && <div className="w-100">
                <div>
                    {this.renderServiceTimeOptions()}
                </div>
                <input placeholder="Agrega tu comentario aquí..." className={style.comments}
                    defaultValue={this.state.notes} onChange={event => this.onChangeNotes(event.target.value)} />
            </div>}
            {this.props.order.product.type === ProductType.GROUP &&
                <ProductGroupConfigurerView
                    key={this.props.order.id}
                    product={this.props.product as ProductGroup}
                    groupChoices={this.props.order.groupChoices}
                    notes={this.props.order.notes}
                    onChange={this.onChangeProductGroup.bind(this)}
                />
            }
        </Row>
    }

    private renderServiceTimeOptions(): JSX.Element[] {
        var orderServiceTime = this.state.serviceTime;
        var enums = EnumUtils.values(ServiceTime);
        return enums.map(serviceTime => {
            return <Tile key={this.props.order.id + "-" + serviceTime} value={getServiceTimeLabel(serviceTime)} selected={serviceTime == orderServiceTime}
                className={classNames(style.tile, style.serviceTimeTile)} onClick={() => this.onChangeServiceTime(serviceTime as ServiceTime)} />
        })
    }

    private getOrderResume() {
        return "Pedido a las " + Utils.parseDate(this.props.order.orderTime)!.toLocaleTimeString() +
            " por " + (this.props.order.user ? this.props.order.user.name.split(" ")[0] : "un camarero");
    }

    private onChangeNotes(notes: string) {
        this.setState({ notes });
        var order = Object.assign({}, this.props.order);
        order.notes = notes;
        this.props.onChangeOrder(order);
    }

    private onChangeServiceTime(serviceTime: ServiceTime) {
        this.setState({ serviceTime });
        var order = Object.assign({}, this.props.order);
        order.serviceTime = serviceTime;
        this.props.onChangeOrder(order);
    }

    private onChangeProductGroup(state: ProductGroupConfigurerViewState) {
        var order = Object.assign({}, this.props.order);
        order.groupChoices = order.groupChoices!!.map(gc => Object.assign({}, gc));
        order.notes = state.notes;
        [...state.productsSelected.keys()].map(k => {
            let product = state.productsSelected.get(k)!!
            order.groupChoices!!.find(gc => gc.serviceTime === k)!!.product = product
        });
        this.props.onChangeOrder(order);
    }
}