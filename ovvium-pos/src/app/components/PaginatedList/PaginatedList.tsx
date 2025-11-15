import * as React from 'react';
import { AbstractPage } from 'app/model/AbstractPage';
import * as style from './style.css';
import * as classNames from 'classnames';
import ArrowLeft from '@material-ui/icons/KeyboardArrowLeft';
import ArrowRight from '@material-ui/icons/KeyboardArrowRight';
import InboxIcon from '@material-ui/icons/Inbox';

export interface PaginatedListProps {
  onNext: () => void;
  onPrevious: () => void;
  onItemClick: (any) => void;
  page: AbstractPage<any, any>;
  children: React.ReactElement<any>;
  childClassName?: string;
}

export class PaginatedList extends React.Component<PaginatedListProps> {

  constructor(props) {
    super(props);
  }

  render() {
    const nextArrowClasses = classNames(
      style.arrow,
      this.props.page.hasNextPage ? '' : style.disabled
    );
    const previousArrowClasses = classNames(
      style.arrow,
      this.props.page.pageOffset > 0 ? '' : style.disabled
    );
    return (
      <div className="h-100 w-100">
        <div className={style.list}>
          {this.props.page.totalElements > 0 && this.props.page.content.map((it, index) => {
            return this.createChildren(it, index)
          })}
          {this.props.page.totalElements == 0 && <p className={style.vcenter}><span className="d-block">No hay elementos.</span><InboxIcon className={style.emptyIcon}/></p>}
        </div>
        <div className={style.arrowContainer}>
          <div onClick={this.onClickPrevious.bind(this)} className={previousArrowClasses}><span><ArrowLeft /></span></div>

          {this.props.page.totalElements > 0 &&
            <div className={style.pageCounter}><p className={style.vcenter}>{this.props.page.pageOffset + 1 + '/' + this.props.page.totalPages}</p></div>}

          <div onClick={this.onClickNext.bind(this)} className={nextArrowClasses}><span><ArrowRight /></span></div>
        </div>
      </div>
    );
  }

  private createChildren(it: any, index: number): React.ReactElement<any, string | ((props: any) => React.ReactElement<any, string | any | (new (props: any) => React.Component<any, any, any>)> | null) | (new (props: any) => React.Component<any, any, any>)> {
    return <div className={this.props.childClassName} onClick={() => this.onClick(it)} key={index}>
      {React.cloneElement(this.props.children, { item: it })}
    </div>
  }

  onClickNext() {
    if (this.props.page.hasNextPage) {
      this.props.onNext()
    }
  }

  onClickPrevious() {
    if (this.props.page.pageOffset > 0) {
      this.props.onPrevious()
    }
  }

  onClick(it: any) {
    this.props.onItemClick(it)
  }

}
