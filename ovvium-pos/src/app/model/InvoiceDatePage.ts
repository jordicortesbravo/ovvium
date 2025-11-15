import { InvoiceDatePageResponse } from "./response/InvoiceDatePageResponse";
import { InvoiceDate } from './InvoiceDate';
import { AbstractPage } from "./AbstractPage";
import { InvoiceDateResponse } from './response/InvoiceDateResponse';

export class InvoiceDatePage extends AbstractPage<InvoiceDate, InvoiceDateResponse> {

    constructor(invoiceDatePageResponse: InvoiceDatePageResponse) {
        super(
            invoiceDatePageResponse,
            invoiceDatePageResponse.content.map(it => new InvoiceDate(it))
        )
    }
}