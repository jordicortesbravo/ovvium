import * as React from 'react';
import { Tile } from '../Tile/Tile';
import { Category } from './../../model/Category';
import * as style from './style.css';
import * as classNames from 'classnames';

interface CategoryGridProps {
  categories: Array<Category>;
  selectedCategory: Category;
  onSelectCategory: (Category) => void;
}

export class CategoryGrid extends React.Component<CategoryGridProps> {

  render() {
    let categories = this.props.categories;
    let width = this.getCategoryWidth();
    return (
      <div className={style.categoryWrapper}>
        {categories.map((key, index) => {
          var selected = this.isSelected(categories[index])
          return <Tile
                    value={categories[index].name}
                    key={categories[index].id}
                    onClick={() => this.onClick(categories[index])}
                    className={classNames(style.tile, selected ? style.tile_selected : '')}
                    style={{width: width}}
                    selected={selected}
                  />
        }
            )}
      </div>
    );
  }

  private onClick(category: Category) {
    this.props.onSelectCategory(category);
  }

  private isSelected(category: Category): boolean {
    return this.props.selectedCategory != undefined && this.props.selectedCategory.id === category.id;
  }

  private getCategoryWidth() {
    let categories = this.props.categories;
    let innerWidth = window.innerWidth;
    let categoryWidth = 100/(Math.round(categories.length/2));
    if(innerWidth <= 479 && categoryWidth < 25) {
      categoryWidth = 25;
    }
    return categoryWidth + '%'
  }
}
