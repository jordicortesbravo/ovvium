import FormControl from '@material-ui/core/FormControl';
import Input from '@material-ui/core/Input';
import InputAdornment from '@material-ui/core/InputAdornment';
import Search from '@material-ui/icons/Search';
import Cancel from '@material-ui/icons/Cancel';
import { Product } from 'app/model/Product';
import * as React from 'react';
import { Tile } from '../Tile/Tile';
import * as style from './style.css';

interface ProductsGridProps {
  products: Array<Product>;
  onSelectProduct: (product: Product) => void;
  onSearch: (text: string) => void;
}

interface ProductsGridState {
  searchText?: string | null;
}

export class ProductGrid extends React.Component<ProductsGridProps, ProductsGridState> {

  constructor(props) {
    super(props);
    this.state = {
      searchText: ""
    };
  }

  render() {
    return (
      <div className={style.productsWrapper}>

        <FormControl className={style.searchInput}>
          <Input
            placeholder="Buscar productos"
            value={this.state.searchText}
            onChange={(event: React.ChangeEvent<HTMLInputElement>) => this.onSearch(event.target.value)}
            className={style.searchInputInner}
            startAdornment={
              <InputAdornment position="start">
                <Search />
              </InputAdornment>
            }
            endAdornment={
              <>{this.state.searchText && this.state.searchText != "" &&
                <InputAdornment position="end" onClick={() => this.onSearch("")}>
                  <Cancel fontSize="small" />
                </InputAdornment>}
              </>
            }
          />
        </FormControl>
        {
          this.props.products.map((key, index) => {
            let product = this.props.products[index];
            return this.renderProduct(product);
          })
        }
      </div>
    );
  }

  private renderProduct(product: Product) {
    return <Tile
      value={product.name}
      key={product.id}
      className={style.tile}
      onClick={() => this.props.onSelectProduct(product)}
    />
  }

  private onSearch(text: string) {
    this.setState({ searchText: text });
    this.props.onSearch(text);
  }
}
