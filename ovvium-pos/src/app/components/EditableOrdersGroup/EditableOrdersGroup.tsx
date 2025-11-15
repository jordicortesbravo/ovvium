import { EditableOrder } from 'app/components/EditableOrder/EditableOrder';
import * as React from 'react';
import { OrderGroup } from 'app/model/enum/OrderGroup';
import { ArrayUtils } from 'app/utils/ArrayUtils';
import { Order } from 'app/model/Order';
import { Col } from 'react-bootstrap';

interface EditableOrdersGroupProps {
    orderGroup: OrderGroup;

    onChangeOrderGroup: (orderGroup: OrderGroup) => void;
}

export class EditableOrdersGroup extends React.Component<EditableOrdersGroupProps> {

    render() {
        return <Col className="h-100">
            {this.props.orderGroup.orders
                .sort((o1, o2) => o1.orderTime > o2.orderTime ? -1 : 1)
                .map((order, index) => <EditableOrder
                    key={index}
                    order={order}
                    product={this.props.orderGroup.product}
                    onChangeOrder={updatedOrder => this.onChangeOrder(updatedOrder)}
                />)
            }
        </Col>
    }

    private onChangeOrder(order: Order) {
        var orderGroup = Object.assign({}, this.props.orderGroup);
        orderGroup.orders = ArrayUtils.replace(orderGroup.orders, order, 'id');
        this.props.onChangeOrderGroup(orderGroup);
    }
}
