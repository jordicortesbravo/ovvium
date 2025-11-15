import { createOrderActionCreator } from 'app/actions/BillActions';
import { loadLocationsCreator } from 'app/actions/LocationActions';
import { loadProductsCreator, selectCategoryCreator } from 'app/actions/ProductActions';
import { browserHistory } from 'app/App';
import { ProductsView } from 'app/components/Products/ProductsView';
import { BaseContainerProps, BaseContainerState, baseMapDispatchToProps } from 'app/containers/BaseContainer';
import { AppRoute } from 'app/containers/Router/AppRoute';
import { Bill } from 'app/model/Bill';
import { Category } from 'app/model/Category';
import { Customer } from 'app/model/Customer';
import { ServiceTime } from 'app/model/enum/ServiceTime';
import { Location } from 'app/model/Location';
import { Product } from 'app/model/Product';
import { AppState } from 'app/reducers/RootReducer';
import { ArrayUtils } from 'app/utils/ArrayUtils';
import { SerializationUtils } from 'app/utils/SerializationUtils';
import * as React from 'react';
import { connect } from 'react-redux';
import { AnyAction, Dispatch } from 'redux';
import { BaseContainer, baseMapStateToProps } from './../BaseContainer';
import { InvoiceDate } from 'app/model/InvoiceDate';
import { getLastInvoiceDateCreator } from 'app/actions/InvoiceActions';
import { InvoiceDateClosedDialog } from 'app/components/Dialog/InvoiceDateClosedDialog';
import { ProductType } from 'app/model/enum/ProductType';
import { Col, Row } from 'react-bootstrap';
import { OrdersGrid } from 'app/components/OrdersGrid/OrdersGrid';
import * as classNames from 'classnames';
import * as style from './style.css';
import { ProductGroupConfigurerView, ProductGroupConfigurerViewState } from 'app/components/ProductGroupConfigurer/Products/ProductGroupConfigurerView';
import { ProductGroup } from './../../model/ProductGroup';
import { Loading } from 'app/components/Loading/Loading';
import { CreateOrderRequest } from './../../model/request/CreateOrderRequest';
import { OrderGroupChoiceRequest } from 'app/model/request/OrderGroupChoiceRequest';
import { ConfirmButtons } from 'app/components/ConfirmButtons/ConfirmButtons';
import { ErrorSnackbar } from 'app/components/ErrorSnackbar/ErrorSnackbar';


interface ProductsContainerProps extends BaseContainerProps {
  categories: Array<Category>;
  locations: Array<Location>;
  bills: Array<Bill>;
  selectedCategory: Category;
  selectedBill: Bill;
  customer: Customer;
  lastInvoiceDate?: InvoiceDate;

  loadProducts: (customer: Customer) => void;
  loadLocations: (customer: Customer) => void;
  onSelectCategory: (category: Category) => void;
  onSelectProduct: (order: CreateOrderRequest, bill: Bill, customer: Customer) => void;
  loadLastInvoiceDate: (customer: Customer) => void;
}

interface ProductContainerState extends BaseContainerState {
  products: Array<Product>;
  searchText?: string;
  selectedProduct?: Product;
  configureProductGroup: boolean;
  groupConfigurerState?: ProductGroupConfigurerViewState
}

class ProductsContainer extends BaseContainer<ProductsContainerProps, ProductContainerState> {

  constructor(props) {
    super(props, {
      products: props.selectedCategory ? props.selectedCategory.products : [],
      errorDialogOpened: false,
      configureProductGroup: false,
    });
  }

  static getDerivedStateFromProps(props, state) {
    var products = new Array<Product>();
    if (!state.searchText || state.searchText == "") {
      products = props.selectedCategory ? props.selectedCategory.products : [];
    } else {
      props.categories.forEach(category => category.products
        .filter(product => SerializationUtils.normalize(product.name.toLowerCase()).indexOf(SerializationUtils.normalize(state.searchText.toLowerCase())) != -1)
        .forEach(product => products.push(product)));
    }
    let errorDialogOpened = props.error && !state.errorDialogOpened;
    return { ...state, errorDialogOpened, products }
  }

  componentDidMount() {
    const customer = this.props.customer;
    if (ArrayUtils.isEmpty(this.props.locations) || ArrayUtils.isEmpty(this.props.categories)) {
      this.props.loadProducts(customer);
      this.props.loadLocations(customer);
    }
    this.props.loadLastInvoiceDate(customer)
  }

  render() {
    if (ArrayUtils.isEmpty(this.props.categories) || ArrayUtils.isEmpty(this.props.locations)) {
      return <Loading />
    }
    return (<>
      <div className="w-100 h-100" style={{ zIndex: 10 }}>
        <Row className="h-100">
          {!this.state.configureProductGroup && <ProductsView
            categories={this.props.categories}
            locations={this.props.locations}
            selectedBill={this.props.selectedBill}
            products={this.state.products}
            selectedCategory={this.props.selectedCategory}
            lastInvoiceDate={this.props.lastInvoiceDate}
            onSelectCategory={this.props.onSelectCategory}
            onSelectProduct={this.onSelectProduct.bind(this)}
            onSearchProduct={this.onSearchProduct.bind(this)}
          />}
          {this.state.configureProductGroup && <Col lg="8" className={"h-100 " + style.lightLayout}>
            <Row className={classNames(style.wrapper, 'h-100')}>
              <Col lg="12" className="h-100 w-100">
                <ProductGroupConfigurerView
                  product={this.state.selectedProduct as ProductGroup}
                  onChange={this.onChangeProductGroupConfigurer.bind(this)}
                />
                <Row style={{ height: '1%' }}></Row>
                <Row className={classNames(style.confirmWrapper, style.wrapper)}>
                  <ConfirmButtons
                    onCancel={this.onCancelProductGroup.bind(this)}
                    onAccept={this.onAcceptProductGroup.bind(this)}
                    acceptDisabled={this.state.groupConfigurerState!.productsSelected.size !== Object.keys(ServiceTime).length}
                  />
                </Row>
              </Col>
            </Row>
          </Col>}
          <Col lg="4" className={classNames("h-100", style.lightLayout, style.ordersGrid)}>
            <Row className={classNames(style.wrapper, 'h-100')}>
              <OrdersGrid bill={this.props.selectedBill}
                goToLocations={() => this.goTo(AppRoute.LOCATIONS)}
                onClickTotalButton={() => this.goTo(AppRoute.BILL)}
                selectedOrders={this.props.selectedBill ? this.props.selectedBill.orders : []}
                lastInvoiceDate={this.props.lastInvoiceDate}
              />
            </Row>
          </Col>
        </Row>
      </div>
      <InvoiceDateClosedDialog open={this.props.lastInvoiceDate == undefined || this.props.lastInvoiceDate?.status == "CLOSED"}
        onClick={() => { this.props.history.push(AppRoute.CONFIG) }} />
      <ErrorSnackbar show={this.state.errorDialogOpened} error={this.props.error} onClose={this.onClearError.bind(this)} />
    </>)
  }

  goTo(route: AppRoute) {
    browserHistory.push(route);
  }

  onSearchProduct(text: string) {
    this.setState({ searchText: text });
  }

  onSelectProduct(product: Product) {
    if (!this.props.selectedBill) {
      browserHistory.push(AppRoute.LOCATIONS);
    } else {
      this.setState({ selectedProduct: product })
      if (product.type === ProductType.GROUP) {
        this.setState({
          configureProductGroup: true,
          groupConfigurerState: {
            productsSelected: new Map(),
            notes: ''
          }
        })
      } else {
        let order = {
          productId: product.id,
        } as CreateOrderRequest
        this.props.onSelectProduct(order, this.props.selectedBill, this.props.customer);
      }
    }
  }

  onCancelProductGroup() {
    this.setState({
      configureProductGroup: false,
      selectedProduct: undefined
    })
  }

  onAcceptProductGroup() {
    let configurerState = this.state.groupConfigurerState!!
    let order = {
      productId: this.state.selectedProduct!!.id,
      notes: configurerState.notes,
      groupChoices: [...configurerState.productsSelected.values()].map(v => {
        return { productId: v.id } as OrderGroupChoiceRequest
      })
    } as CreateOrderRequest
    this.props.onSelectProduct(order, this.props.selectedBill, this.props.customer);
    this.onCancelProductGroup()
  }

  onChangeProductGroupConfigurer(configurerState: ProductGroupConfigurerViewState) {
    this.setState({ groupConfigurerState: configurerState })
  }

}

function mapStateToProps(state: AppState): ProductsContainerProps {
  return baseMapStateToProps(state, {
    categories: state.productState.categories,
    selectedCategory: state.productState.selectedCategory,
    locations: state.locationState.locations,
    bills: state.billState.bills,
    selectedBill: state.billState.selectedBill,
    customer: state.billState.customer!,
    lastInvoiceDate: state.invoicesState.lastInvoiceDate,
    route: AppRoute.TAKE_ORDER
  } as ProductsContainerProps);
}

function mapDispatchToProps(dispatch: Dispatch<AnyAction>) {
  return baseMapDispatchToProps(dispatch,
    {
      loadProducts: loadProductsCreator,
      loadLocations: loadLocationsCreator,
      onSelectCategory: selectCategoryCreator,
      onSelectProduct: createOrderActionCreator,
      loadLastInvoiceDate: getLastInvoiceDateCreator,
    }
  );
}

export default connect(
  mapStateToProps,
  mapDispatchToProps
)(ProductsContainer);
