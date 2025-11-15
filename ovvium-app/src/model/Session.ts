
export class Session {
    accessToken?: string;
    refreshToken?: string;
    loggedUntil?: Date;
    
    isLoggedIn(): boolean {
        return Session.isLoggedIn(this);
    }

    static isLoggedIn(session: Session| undefined): boolean {
        return session != undefined && session.loggedUntil != undefined && session.loggedUntil.getTime() > Date.now();
    }

    static from(object: any) : Session {
        var session = new Session();
        session.accessToken = object.accessToken;
        session.refreshToken = object.refreshToken;
        session.loggedUntil = new Date(object.loggedUntil);
        return session;
    }

}