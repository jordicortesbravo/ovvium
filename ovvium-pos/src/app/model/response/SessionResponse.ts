export class SessionResponse {
    
    refreshToken: string;
    accessToken: string;
    loggedUntil: string;

    constructor(sessionResponse: SessionResponse = {} as SessionResponse){
        this.refreshToken = sessionResponse.refreshToken;
        this.accessToken = sessionResponse.accessToken;
        this.loggedUntil = sessionResponse.loggedUntil;
    }

}
