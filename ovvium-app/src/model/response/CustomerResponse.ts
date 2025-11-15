import { ResourceIdResponse } from "./ResourceIdResponse";


export class CustomerResponse extends ResourceIdResponse { 

    name: string;
    description: string;
	imageUrl: string;
	address: string;
	cif: string;
    phones: string[];
    
    constructor(customerResponse: CustomerResponse) {
        super(customerResponse.id);
        this.name = customerResponse.name;
        this.description = customerResponse.description;
        this.imageUrl = customerResponse.imageUrl;
        this.address = customerResponse.address;
        this.cif = customerResponse.cif;
        this.phones = customerResponse.phones;
    }
}