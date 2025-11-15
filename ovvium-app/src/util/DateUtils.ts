export class DateUtils {

    static equals(d1: Date | undefined, d2: Date | undefined) {
        return d1 == d2 || (d1 == undefined && d2 == undefined) || (d1 != undefined && d2 != undefined && d1.getTime() == d2.getTime());
    }

    static unwrap(d1: Date | string) {
        if (typeof d1 == 'string') {
            return new Date(d1);
        }
        return d1;
    }

    static toISODate(d1: Date) {
        return d1.toLocaleDateString() + " " + this.appendLeadingZeroes(d1.getHours()) + ":" + this.appendLeadingZeroes(d1.getMinutes());
    }

    // FIXME Improve this using a date library
    private static appendLeadingZeroes(n: number) {
        if (n <= 9) {
            return "0" + n;
        }
        return n
    }
}