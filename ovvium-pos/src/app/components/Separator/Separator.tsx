import * as React from 'react';
import * as style from './style.css';

interface SeparatorProps {
    orientation: 'vertical' | 'horizontal'
}

export class Separator extends React.Component<SeparatorProps> {

    render() {
        if(this.props.orientation == 'vertical') {
            return <div className={style.verticalSeparator}></div>
        }
        return <div className={style.horizontalSeparator}></div>
    }
}