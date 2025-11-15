import { StringUtils } from '../../util/StringUtils';

export enum BillStatus {
    OPEN = 'OPEN',
    CLOSED = 'CLOSED'

    
}

export function asBillStatus(value: string): BillStatus {
    switch(value.toUpperCase()) {
        case BillStatus.OPEN:
            return BillStatus.OPEN;
        case BillStatus.CLOSED:
            return BillStatus.CLOSED
        default:
            throw new Error("BillStatus with value " + value + " doesn't exist");
    }
}