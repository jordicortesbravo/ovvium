import * as React from 'react';
import * as style from './style.css';
import { withStaticBaseUrl } from 'app/actions/BaseAction';


export class Header extends React.Component<any> {

  render() {
    return (
      <div className={style.header}>
        <img src={withStaticBaseUrl('/img/logo-pos.png')} className={style.logo} />
      </div>
    )

  }

}