export class ArrayUtils {
  static first(array: any[] | undefined | null): any {
    if (!array || array.length == 0) {
      return null;
    }
    return array[0];
  }

  static contains(array: any[] |undefined, elem: any, keyToCompare: string): boolean {
    var contains = false;
    if (array && array.length > 0) {
      for (var i in array) {
        var item = array[i];
        if (item && item[keyToCompare] == elem[keyToCompare]) {
          contains = true;
          break;
        }
      }
    }
    return contains;
  }

  static replace<T>(array: T[], newElem: any, keyToCompare: string): T[] {
    var replaced: boolean = false;
    var newArray:any[] = [];
    for (var i in array) {
      var item = array[i];
      if (item && item[keyToCompare] == newElem[keyToCompare]) {
        newArray[i] = newElem;
        replaced = true;
      } else {
        newArray[i] = item;
      }
    }
    if(!replaced) {
      newArray.push(newElem);
    }
    return newArray;
  }

  static remove<T>(array: T[] |undefined, elem: any, compare: string | ((e1:any, e2: any) => boolean)) {
    if (array && array.length > 0) {
      for (var i = 0; i< array.length; i++) {
        var item = array[i];
        if(typeof compare === 'string') {
          if (item[compare] == elem[compare]) {
              array.splice(i,1);   
          }
        } else {
          if (compare(item, elem)) {
            array.splice(i,1);   
          }
        }
      }
    }
  }

  static isEmpty(array: any[]|undefined|null) {
    return array == undefined || array == null || array.length == 0;
  }

  static isNotEmpty(array: any[]|undefined|null) {
    return !ArrayUtils.isEmpty(array);
  }
}
