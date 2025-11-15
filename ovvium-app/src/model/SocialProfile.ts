import { SocialProvider } from "./enum/SocialProvider";

export class SocialProfile {
    socialProvider: SocialProvider;
    id?: string;
    token: string;
    email?: string;
    name: string;

    constructor(profile: SocialProfile) {
        this.socialProvider = profile.socialProvider;
        this.id = profile.id;
        this.token = profile.token;
        this.email = profile.email;
        this.name = profile.name;
    }
}