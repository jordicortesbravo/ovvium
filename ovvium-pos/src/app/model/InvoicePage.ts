import { AbstractPage } from "./AbstractPage";
import { Invoice } from 'app/model/Invoice';
import { InvoiceResponse } from "./response/InvoiceResponse";
import { InvoicePageResponse } from "./response/InvoicePageResponse copy";

export class InvoicePage extends AbstractPage<Invoice, InvoiceResponse> {

    constructor(invoicePageResponse: InvoicePageResponse) {
        super(
            invoicePageResponse,
            invoicePageResponse.content.map(it => new Invoice(it))
        )
    }
}