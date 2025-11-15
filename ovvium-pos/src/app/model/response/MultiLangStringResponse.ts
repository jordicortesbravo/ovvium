
export class MultiLangStringResponse {
    
    defaultValue: string;
    translations: Map<string,string>;

    constructor(response: MultiLangStringResponse = {} as MultiLangStringResponse){
        this.defaultValue = response.defaultValue;
        this.translations = response.translations;
    }

}