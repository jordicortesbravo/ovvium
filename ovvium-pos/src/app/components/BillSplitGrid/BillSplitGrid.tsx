import { Tile } from 'app/components/Tile/Tile';
import { BillSplit } from 'app/model/BillSplit';
import * as React from 'react';
import * as style from './style.css';

interface BillSplitGridProps {
  splits?: BillSplit[];
  onSelectBillSplit: (billSplit: BillSplit) => void;
  createBillSplit: () => void;
  goToEditBill: () => void;
}


export class BillSplitGrid extends React.Component<BillSplitGridProps, any> {

  render() {
    return (
      <div>
        <Tile value={"Nueva subcuenta "} className={style.tile} onClick={this.props.createBillSplit} />
        {this.props.splits && this.props.splits.map((split, index) => {
              return <Tile value={"Subcuenta " + (index + 1) }  className={style.tile}  key={"split-" + index} onClick={() => this.props.onSelectBillSplit(split)} />
            })
        }
      </div>
    );
  }
}
