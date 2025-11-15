import { User } from './../User';

export class RecoverPasswordResponse {
    
    user: User;

    constructor(recoverPasswordResponse: RecoverPasswordResponse = {} as RecoverPasswordResponse){
        this.user = recoverPasswordResponse.user;
    }

}
