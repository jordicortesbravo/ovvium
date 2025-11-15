

export interface IStorageItem {
    key: string;
    value: any;
}

export class StorageItem {
    key: string;
    value: any;

    constructor(data: IStorageItem) {
        this.key = data.key;
        this.value = data.value;
    }
}

export class LocalStorageUtils {

    private static localStorageSupported: boolean =typeof window['localStorage'] != "undefined" && window['localStorage'] != null;


    // add value to storage
    static add(key: string, item: string) {
        if (this.localStorageSupported) {
            localStorage.setItem(key, item);
        }
    }

    // get all values from storage (all items)
    static getAllItems(): Array<StorageItem> {
        var list = new Array<StorageItem>();

        for (var i = 0; i < localStorage.length; i++) {
            var key = localStorage.key(i)!!;
            var value = localStorage.getItem(key);

            list.push(new StorageItem({
                key: key,
                value: value
            }));
        }

        return list;
    }

    // get only all values from localStorage
    static getAllValues(): Array<any> {
        var list = new Array<any>();

        for (var i = 0; i < localStorage.length; i++) {
            var key = localStorage.key(i);
            var value = localStorage.getItem(key!!);

            list.push(value);
        }

        return list;
    }

    // get one item by key from storage
    static get(key: string): string | null {
        if (this.localStorageSupported) {
            var item = localStorage.getItem(key);
            return item;
        } else {
            return null;
        }
    }

    // remove value from storage
    static remove(key: string) {
        if (this.localStorageSupported) {
            localStorage.removeItem(key);
        }
    }

    // clear storage (remove all items from it)
    static clear() {
        if (this.localStorageSupported) {
            localStorage.clear();
        }
    }

}