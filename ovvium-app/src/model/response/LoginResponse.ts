import { User } from './../User';
import { SessionResponse } from './SessionResponse';

export class LoginResponse {
    
    user: User;
    session: SessionResponse;

    constructor(loginResponse: LoginResponse = {} as LoginResponse){
        this.user = loginResponse.user;
        this.session = loginResponse.session;
    }

}
