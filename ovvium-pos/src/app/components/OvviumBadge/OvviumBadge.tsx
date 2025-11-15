
import * as React from 'react';
import { Badge } from 'react-bootstrap';
import * as style from './style.css';

export interface OvviumBadgeProps {
  value?: number
}

export class OvviumBadge extends React.Component<OvviumBadgeProps> {

  render() {
    return  <Badge pill={true} className={style.badge} style={{display: this.props.value ? '' : 'none'}}>{this.props.value}</Badge>
          
  }

}
