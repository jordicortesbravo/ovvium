import { ResourceIdResponse } from './ResourceIdResponse';

export class InvoiceDateResponse extends ResourceIdResponse {
    status: "OPEN" | "CLOSED";
    date: Date;

	constructor(invoiceResponse: InvoiceDateResponse) {
		super(invoiceResponse.id);
        this.status = invoiceResponse.status;
        this.date = invoiceResponse.date;
    }
}