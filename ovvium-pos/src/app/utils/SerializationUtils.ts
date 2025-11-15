
export class SerializationUtils {

    static getTime(date: string|Date) {
        if(date instanceof Date) {
            return date.getTime();
        }
        return new Date(date).getTime();
    }

    static normalize(s: string) {
        return s.normalize('NFD')
        .replace(/([^n\u0300-\u036f]|n(?!\u0303(?![\u0300-\u036f])))[\u0300-\u036f]+/gi,"$1")
        .normalize()
        .replace(/ñ/gi,"n");
    }
}