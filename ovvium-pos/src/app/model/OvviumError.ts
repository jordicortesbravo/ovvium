import { ERRORS } from "app/localization/errors";

export class OvviumError extends Error {

    config: any;
    message: string;
    response?: XMLHttpRequest;
    status: number;
    code: number;
    localizedMessage?: string;

    constructor(error:any) {
        super(error.stack)
        this.config = error.config;
        this.message = error.response && error.response.data ? error.response.data.message : error.message;
        this.response = error.response;
        this.status = error.response ? error.response.status : 0;
        this.code = error.response && error.response.data ? error.response.data.errorCode : 0;
        this.localizedMessage = ERRORS[error.code]
    }
}