
export class UserResponse {
    id: string;
    name: string;
    email: string;
    imageUri: string;
    enabled: boolean;

    constructor(userResponse: UserResponse = {} as UserResponse) {
        this.id = userResponse.id;
        this.name = userResponse.name;
        this.email = userResponse.email;
        this.imageUri = userResponse.imageUri;
        this.enabled = userResponse.enabled;
    }
}