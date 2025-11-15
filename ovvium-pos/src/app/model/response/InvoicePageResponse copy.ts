import { AbstractPagedResponse } from './AbstractPagedResponse';
import { InvoiceResponse } from 'app/model/response/InvoiceResponse';

export class InvoicePageResponse extends AbstractPagedResponse<InvoiceResponse> {

	constructor(invoicePageResponse: InvoicePageResponse = {} as InvoicePageResponse) {
        super(invoicePageResponse)
    }
    
}