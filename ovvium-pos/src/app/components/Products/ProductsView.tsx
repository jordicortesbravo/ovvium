import { CategoryGrid } from 'app/components/CategoryGrid/CategoryGrid';
import { ProductGrid } from 'app/components/ProductGrid/ProductGrid';
import { Bill } from 'app/model/Bill';
import { Category } from 'app/model/Category';
import { Location } from 'app/model/Location';
import { Product } from 'app/model/Product';
import * as classNames from 'classnames';
import * as React from 'react';
import { Col, Row } from 'react-bootstrap';
import * as style from './style.css';
import { InvoiceDate } from 'app/model/InvoiceDate';

interface ProductsViewProps {
    categories: Array<Category>;
    locations: Array<Location>;
    products: Array<Product>;
    selectedBill: Bill;
    selectedCategory?: Category;
    lastInvoiceDate?: InvoiceDate;

    onSelectCategory: (category: Category) => void;
    onSelectProduct: (product: Product) => void;
    onSearchProduct: (text: string) => void;

}


export class ProductsView extends React.Component<ProductsViewProps> {

    render() {
        return (<>
            <Col lg="8" className={"h-100 " + style.lightLayout}>
                <Row className={classNames(style.optionsWrapper, style.wrapper)}>
                    <ProductGrid
                        products={this.props.products}
                        onSelectProduct={this.props.onSelectProduct}
                        onSearch={this.props.onSearchProduct}
                    />
                </Row>
                <Row style={{ height: '1%' }}></Row>
                <Row className={classNames(style.categoriesWrapper, style.wrapper)}>
                    <CategoryGrid
                        categories={this.props.categories}
                        onSelectCategory={this.props.onSelectCategory}
                        selectedCategory={this.props.selectedCategory!}
                    />
                </Row>
            </Col>
        </>)
    }
}