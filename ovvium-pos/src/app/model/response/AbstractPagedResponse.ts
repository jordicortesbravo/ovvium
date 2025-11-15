
export abstract class AbstractPagedResponse<T> {
    pageOffset: number;
    totalPages: number;
    totalElements: number;
    numberOfElements: number;
    hasNextPage: boolean;
    content: T[];

    constructor(pageResponse: AbstractPagedResponse<T>) {
        this.pageOffset = pageResponse.pageOffset;
        this.totalPages = pageResponse.totalPages;
        this.totalElements = pageResponse.totalElements;
        this.numberOfElements = pageResponse.numberOfElements;
        this.hasNextPage = pageResponse.hasNextPage;
        this.content = pageResponse.content;
    }
}