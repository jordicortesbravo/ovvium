
export class UserResponse {
    id: string;
    name: string;
    email: string;
    imageUri: string;
    customerId: string;
    customerName: string;

    constructor(userResponse: UserResponse = {} as UserResponse) {
        this.id = userResponse.id;
        this.name = userResponse.name;
        this.email = userResponse.email;
        this.imageUri = userResponse.imageUri;
        this.customerId = userResponse.customerId;
        this.customerName = userResponse.customerName;
    }
}