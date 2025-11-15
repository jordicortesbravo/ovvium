export class ArrayUtils {
    static first(array: any[] | undefined | null): any {
      if (!array || array.length == 0) {
        return null;
      }
      return array[0];
    }
  
    static contains(array: any[] |undefined, elem: any, keyToCompare: string): boolean {
      return ArrayUtils.get(array, elem, keyToCompare) != undefined;
    }

    static get(array: any[] |undefined, elem: any, keyToCompare: string): any {
      if (array && array.length > 0) {
        for (var i in array) {
          var item = array[i];
          if (item && item[keyToCompare] == elem[keyToCompare]) {
            return item;
          }
        }
      }
      return undefined;
    }

    static replace<T>(array: T[], newElem: any, keyToCompare: string): T[] {
      var replaced: boolean = false;
      for (var i in array) {
        var item = array[i];
        if (item && item[keyToCompare] == newElem[keyToCompare]) {
          array[i] = newElem;
          replaced = true;
        } else {
          array[i] = item;
        }
      }
      if(!replaced) {
        array.push(newElem);
      }
      return array;
    }
  
    static remove<T>(array: T[] |undefined, elem: any, keyToCompare: string) {
      if (array && array.length > 0) {
        for (var i = 0; i< array.length; i++) {
          var item = array[i];
          if (item[keyToCompare] == elem[keyToCompare]) {
              array.splice(i,1);   
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

    static intersection(array1: any[]|undefined, array2: any[]|undefined, keyToCompare: string) {
      var intersection: any[] = [];
      if(array1 && array2) {
        array1.forEach(elem => {
          if(ArrayUtils.contains(array2, elem, keyToCompare)) {
            intersection.push(elem);
          }
        });
      }
      return intersection;
    }
  }
  