
export class StringUtils {

    public static containsIgnoreCase(target: string, searchString: string) : boolean {
        return target.toLowerCase().indexOf(searchString.toLowerCase()) != -1;
    }

    public static isBlank(s: string|undefined|null) {
        return s == '' || s == undefined || s == null;
    }

    public static isNotBlank(s: string|undefined|null) {
        return !this.isBlank(s);
    }

    public static equalsIgnoreCase(source: string, target: string) {
        return source.toLowerCase() == target.toLowerCase();
    }

    public static abbreviate(s: string, maxChars: number) {
        if(s.length > maxChars) {
            return s.substring(0, maxChars) + "...";
        }
        return s
    }

    public static uuidToInt(uuid: string): number {
        for(var i =0; i<uuid.length; i++) {
            try {
                var n: any = parseInt(uuid.substr(i, 1));
                if(n.toString() != 'NaN') {
                    return n
                }
            } catch(err){}
        }
        return 0;
    }
}