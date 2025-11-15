
export class EnumUtils {

    static values(enumType : any) : string[] {
        return Object.keys(enumType).map(key => enumType[key]);
    };
}