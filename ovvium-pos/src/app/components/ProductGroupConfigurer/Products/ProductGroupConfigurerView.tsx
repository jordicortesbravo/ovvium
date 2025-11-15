import * as classNames from 'classnames';
import * as React from 'react';
import { Row } from 'react-bootstrap';
import * as style from './style.css';
import { ProductGroup } from 'app/model/ProductGroup';
import { Tile } from 'app/components/Tile/Tile';
import { getServiceTimeLabel, ServiceTime } from 'app/model/enum/ServiceTime';
import { Checkbox } from '@material-ui/core';
import { Product } from 'app/model/Product';
import { OrderGroupChoice } from 'app/model/OrderGroupChoice';


interface ProductGroupConfigurerViewProps {
    product: ProductGroup;
    notes?: string;
    groupChoices?: Array<OrderGroupChoice>;
    onChange: (productsSelected: ProductGroupConfigurerViewState) => void;
}

export interface ProductGroupConfigurerViewState {
    productsSelected: Map<ServiceTime, Product>;
    notes: string;
}

export class ProductGroupConfigurerView extends React.Component<ProductGroupConfigurerViewProps, ProductGroupConfigurerViewState> {

    constructor(props) {
        super(props);
        this.state = {
            productsSelected: props.groupChoices
                ?.reduce((map: Map<ServiceTime, Product>, obj: OrderGroupChoice) => (map.set(obj.serviceTime, obj.product), map), new Map())
                ?? new Map(),
            notes: props.notes ?? ''
        };
    }

    render() {
        return (<>
            <Row className={classNames(style.optionsWrapper, style.wrapper)}>
                {[...this.props.product.products.keys()].map((k) => {
                    let v = this.props.product.products.get(k) ?? [];
                    return (<>
                        <Tile key={k} clickable={false} value={getServiceTimeLabel(k)} className={style.serviceTime} onClick={() => { }} />
                        {v.map((product) => {
                            let selectedProduct = this.state.productsSelected.get(k);
                            return (<>
                                <Tile className={classNames(style.productCheckbox)} key={k + product.id + '_checkbox_tile'} onClick={() => { }}>
                                    <Checkbox checked={selectedProduct?.id == product.id}
                                        key={k + product.id + '_checkbox'}
                                        onChange={() => {
                                            this.state.productsSelected.set(k, product)
                                            this.setState({
                                                productsSelected: this.state.productsSelected
                                            })
                                            this.props.onChange(this.state)
                                        }} />
                                </Tile>
                                <Tile clickable={false} value={product.name} className={style.productName} key={k + product.id + '_product'} onClick={() => { }} />
                            </>)
                        })}
                    </>)
                })}
                <Row key={"_commentWrapper"} className={classNames('w-100', style.commentWrapper)}>
                    <input key={"_comment"} placeholder="Agrega un comentario para cocina aquí..." className={style.comments}
                        defaultValue={this.state.notes}
                        onChange={event => this.onChangeNotes(event.target.value)}
                        onBlur={event => this.onChangeNotes(event.target.value)} />
                </Row>
            </Row>
        </>)
    }


    private onChangeNotes(notes: string) {
        this.setState({ notes });
        this.props.onChange(this.state)
    }
}