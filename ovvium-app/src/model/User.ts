import { UserResponse } from './response/UserResponse';

export class User {
    id: string;
    email: string;
    name: string;
    enabled: boolean;
    imageUri?: string;

    constructor(user: User) {
        this.id = user.id;
        this.email = user.email;
        this.name = user.name;
        this.enabled = user.enabled;
        this.imageUri = user.imageUri;
    }

    static from(userResponse: UserResponse): User {
        return {
            id: userResponse.id,
            name: userResponse.name,
            email: userResponse.email,
            imageUri: userResponse.imageUri,
            enabled: userResponse.enabled,
        } as User;
    }
}

export const WAITER_GHOST_USER = {
    id: '-1',
    name:  'Camarero',
    email: '',
    enabled: false
} as User
