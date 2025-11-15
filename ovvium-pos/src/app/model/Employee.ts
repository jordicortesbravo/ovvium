import { EmployeeResponse } from "app/model/response/EmployeeResponse";

export class Employee{
    id: string;
    name: string;

    constructor(id: string, name: string) {
        this.id = id;
        this.name = name;
    }

    static from(employeeResponse: EmployeeResponse) : Employee | undefined {
        if(employeeResponse) {
            return new Employee(employeeResponse.id, employeeResponse.name);
        }
        return;
    }
}