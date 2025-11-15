export enum IssueStatus {

    PENDING = "PENDING", 
    PREPARING = "PREPARING", 
    READY = "READY", 
    ISSUED = "ISSUED"
}

export function asIssueStatus(value: string) {
    switch(value.toUpperCase()) {
        case IssueStatus.PENDING:
            return IssueStatus.PENDING;
        case IssueStatus.PREPARING:
            return IssueStatus.PREPARING;
        case IssueStatus.READY:
            return IssueStatus.READY;
        case IssueStatus.ISSUED:
            return IssueStatus.ISSUED;
        default:
            throw new Error("IssueStatus with value " + value + " doesn't exist");
    }
}