import { CSSProperties } from '@material-ui/styles';
import * as classNames from 'classnames';
import * as React from 'react';
import * as style from './style.css';

export interface TileProps {
    value?: string;
    className?: string;
    style?: CSSProperties;
    textStyle?: CSSProperties;
    onClick?: () => void;
    selected?: boolean;
    disabled?: boolean;
    visible?: boolean;
    badge?: number|string;
    clickable? : boolean;
}

export class Tile extends React.Component<TileProps> {

  render() {
    const visible = this.props.visible == undefined ? true : this.props.visible;
    const clickable = this.props.clickable == undefined ? true : this.props.clickable;
    const classesWrapper = classNames(
      this.props.className,
      style.category,
      this.props.disabled ? style.tile_disabled : '',
      style.tile, 
      this.props.selected ? style.tile_selected : '',
      clickable ? style.clickable : ''
    );
    if(!visible) {
      return <></>;
    }
    return (
      <div className={classesWrapper} onClick={this.onClick.bind(this)} style={this.props.style}>
          {this.renderText()}
          {this.props.children}
      </div>
    );
  }

  renderText() {
    if(this.props.badge) {
      return  <span style={this.props.textStyle}>
                {this.props.value}
                <span className={classNames(style.badge, "badge badge-pill")}>{this.props.badge }</span>
              </span>
    }
    return <span style={this.props.textStyle}>{this.props.value}</span>;
  }

  onClick() {
    if(!this.props.disabled && this.props.onClick) {
      this.props.onClick();
    }
  }
}
