import { Tile } from 'app/components/Tile/Tile';
import { Bill } from 'app/model/Bill';
import { User } from 'app/model/User';
import { ArrayUtils } from 'app/utils/ArrayUtils';
import * as React from 'react';
import * as style from './style.css';

interface UserGridProps {
  selectedBill?: Bill;
  selectedUsers: Array<User>;
  onSelectUser: (User) => void;
}

export const WAITER_FAKE_USER = {
  id: '-1',
  email: '',
  name: 'Pedido por camareros',
  imageUri: '',
  customerId: '',
  customerName: ''
} as User;

export class UserGrid extends React.Component<UserGridProps, any> {

  render() {
    return (
      <div className={style.userWrapper}>
        {this.getUsers().map(user => {
          return <Tile value={user.name} className={style.tile}  key={user.id}
                    selected={ArrayUtils.contains(this.props.selectedUsers, user, 'id')}
                    onClick={() => this.props.onSelectUser(user)}  
                  />
        })
        }
      </div>
    );
  }

  private getUsers(): Array<User> {
    var users = new Array<User>();
    
    if(this.props.selectedBill) {
      this.props.selectedBill.orders.forEach(order => {
        var user = order.user;
        if(user && !ArrayUtils.contains(users, user, 'id')) {
          users.push(user);
        }
      })
      users.sort((u1,u2) => u1.name < u2.name ? -1 : 1);
      users.push(WAITER_FAKE_USER);
    }


    return users;
  }
}
