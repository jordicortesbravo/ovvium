import * as moment from "moment";

export class Utils {

  static getElapsedTime(when: any): string | undefined {
    var diff = (new Date().getTime() - Date.parse(when)) / 1000;
    var minutes = Math.round(diff / 60);
    if (isNaN(minutes)) {
      return "0min";
    }
    if (minutes > 59) {
      return Math.round((minutes / 60)) + "h " + (minutes % 60) + "min";
    } else {
      return minutes + "min";
    }
  }

  static parseDate(when: any) {
    return when ? new Date(Date.parse(when.toString())) : undefined;
  }

  static formatDate(date: Date, format: string) {
    return moment(date).format(format)
  }

  static joinAsString(array: any[] | undefined, separator: string) {
    if (array == undefined || array.length == 0) {
      return undefined;
    }
    var s = "";
    array.forEach(o => s += o.toString() + ", ");
    return s.substring(0, s.length - 2);
  }

  static formatPrice(amount: number, currency: string) {
    let symbol;
    switch (currency.toUpperCase()) {
      case "EUR": symbol = '€'; break;
      case "GBP": symbol = '£'; break;
      case "USD": symbol = '$'; break;
      case "CZK": symbol = 'Kč'; break;
      default: symbol = '?';
    }
    return amount.toFixed(2) + symbol
  }

  static groupBy = <T, K extends keyof any>(list: T[], getKey: (item: T) => K) =>
    list.reduce((previous, currentItem) => {
      const group = getKey(currentItem);
      if (!previous[group]) previous[group] = [];
      previous[group].push(currentItem);
      return previous;
    }, {} as Record<K, T[]>);

}
