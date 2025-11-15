import { SessionResponse } from './SessionResponse';

export class RegisterResponse {
    
    userId: string;
    session: SessionResponse;

    constructor(registerResponse: RegisterResponse = {} as RegisterResponse){
        this.userId = registerResponse.userId;
        this.session = registerResponse.session;
    }

}
