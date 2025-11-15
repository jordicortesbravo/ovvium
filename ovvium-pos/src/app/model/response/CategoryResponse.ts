import { ResourceIdResponse } from './ResourceIdResponse';
import { MultiLangStringResponse } from './MultiLangStringResponse';

export class CategoryResponse extends ResourceIdResponse {
    name: MultiLangStringResponse;

    constructor(categoryResponse: CategoryResponse = {} as CategoryResponse) {
        super(categoryResponse.id);
        this.name = categoryResponse.name;
    }
}