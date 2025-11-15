import { InvoiceResponse } from "./InvoiceResponse";

export class InvoicePage { 

    pageOffset: number;
    totalPages: number;
	totalElements: number;
	numberOfElements: number;
	hasNextPage: boolean;
	content: InvoiceResponse[];
    
    constructor(page: InvoicePage) {
        this.pageOffset = page.pageOffset;
        this.totalPages = page.totalPages;
        this.totalElements = page.totalElements;
        this.numberOfElements = page.numberOfElements;
        this.hasNextPage = page.hasNextPage;
        this.content = page.content;
    }
}