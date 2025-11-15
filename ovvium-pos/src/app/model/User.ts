import { UserResponse } from './response/UserResponse';

export class User {
    id: string;
    email: string;
    name: string;
    imageUri: string;
    customerId: string;
    customerName: string;

    constructor(user: User) {
        this.id = user.id;
        this.email = user.email;
        this.name = user.name;
        this.imageUri = user.imageUri;
        this.customerId = user.customerId;
        this.customerName = user.customerName;
    }

    static from(userResponse: UserResponse | undefined): User | undefined {
        if(userResponse) {
            return {
                id: userResponse.id,
                name: userResponse.name,
                email: userResponse.email,
                imageUri: userResponse.imageUri,
                customerId: userResponse.customerId,
                customerName: userResponse.customerName
            } as User;
        }
        return undefined;
    }
}