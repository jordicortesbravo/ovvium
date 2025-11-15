import { CustomerResponse } from "./response/CustomerResponse";

export class Customer {

    id: string;
    name: string;
    cif: string;
    description?: string;
    address?: string;
    imageUrl?: string;

    constructor(customer: Customer = {} as Customer) {
        this.id = customer.id;
        this.cif = customer.cif;
        this.name = customer.name;
        this.description = customer.description;
        this.address = customer.address;
        this.imageUrl = customer.imageUrl;
    }

    static from(response: CustomerResponse) {
        if(!response) {
            return;
        }
        return {
            id: response.id,
            name: response.name,
            cif: response.cif,
            description: response.description,
            address: response.address,
            imageUrl: response.imageUrl
        } as Customer;
    }
}