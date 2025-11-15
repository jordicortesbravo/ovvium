export class Customer {

    id: string;
    name: string;
    description?: string;
    imageUrl?: string;
    address?: string;
    cif?: string;
    phones?: string[];

    constructor(customer: Customer = {} as Customer) {
        this.id = customer.id;
        this.name = customer.name;
        this.description = customer.description;
        this.imageUrl = customer.imageUrl;
        this.address = customer.address;
        this.cif = customer.cif;
        this.phones = customer.phones;
    }
}