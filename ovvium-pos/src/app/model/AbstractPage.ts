import { AbstractPagedResponse } from "./response/AbstractPagedResponse";
import { ResourceIdResponse } from "./response/ResourceIdResponse";

export abstract class AbstractPage<T, K extends ResourceIdResponse> {
    pageOffset: number;
    totalPages: number;
    totalElements: number;
    numberOfElements: number;
    hasNextPage: boolean;
    content: T[];

    constructor(pageResponse: AbstractPagedResponse<K>, content: Array<T>) {
        this.pageOffset = pageResponse.pageOffset;
        this.totalPages = pageResponse.totalPages;
        this.totalElements = pageResponse.totalElements;
        this.numberOfElements = pageResponse.numberOfElements;
        this.hasNextPage = pageResponse.hasNextPage;
        this.content = content;
    }
}