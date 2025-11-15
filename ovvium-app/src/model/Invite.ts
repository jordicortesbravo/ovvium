import { User } from "@react-native-community/google-signin";
import { Customer } from "./Customer";


export class Invite {

    invited: boolean;
    customer: Customer;
    inviter: User;
    
    constructor(invite: Invite) {
        this.invited = invite.invited;
        this.customer = invite.customer;
        this.inviter = invite.inviter;
    }
}