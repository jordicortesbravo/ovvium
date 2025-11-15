import { InvoiceDateResponse } from "./response/InvoiceDateResponse";

export class InvoiceDate {
    id: string;
    status: "OPEN" | "CLOSED";
    date: Date;

	constructor(invoiceResponse: InvoiceDateResponse) {
		this.id = invoiceResponse.id
        this.status = invoiceResponse.status;
        this.date = new Date(invoiceResponse.date);
    }
}