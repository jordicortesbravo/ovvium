import { InvoiceDateResponse } from './InvoiceDateResponse';
import { AbstractPagedResponse } from './AbstractPagedResponse';

export class InvoiceDatePageResponse extends AbstractPagedResponse<InvoiceDateResponse> {

	constructor(invoiceDatePageResponse: InvoiceDatePageResponse = {} as InvoiceDatePageResponse) {
        super(invoiceDatePageResponse)
    }
    
}