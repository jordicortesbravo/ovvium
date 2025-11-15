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

export function getIssueStatusLabel(issueStatus: IssueStatus) {
    switch(issueStatus) {
        case IssueStatus.PENDING:
            return "Pendiente";
        case IssueStatus.PREPARING:
            return "En preparación";
        case IssueStatus.READY:
            return "Preparado";
        case IssueStatus.ISSUED:
            return "Servido";
        default:
            throw new Error("IssueStatus not labeled: "+ issueStatus);
    }
}

export function getIssueStatusColor(issueStatus: IssueStatus) {
    switch(issueStatus) {
        case IssueStatus.PENDING:
            return "var(--danger)";
        case IssueStatus.PREPARING:
            return "var(--ovviumYellow)";
        case IssueStatus.READY:
            return "var(--linkColor)";
        case IssueStatus.ISSUED:
            return "var(--green)";
        default:
            throw new Error("IssueStatus not colored: "+ issueStatus);
    }
}