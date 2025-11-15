import { ResourceIdResponse } from "app/model/response/ResourceIdResponse";

export class EmployeeResponse extends ResourceIdResponse {

    name: string;

    constructor(employeeResponse:EmployeeResponse) {
        super(employeeResponse.id);
        this.name = employeeResponse.name;
    }
}