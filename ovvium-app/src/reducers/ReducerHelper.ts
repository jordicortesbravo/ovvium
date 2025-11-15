import { AnyAction } from 'redux';

export class CaseReducer<S> {
    actionType: string;
    delegate: (state: S, action: AnyAction) => S;

    constructor(actionType: string, delegate: (state: S, action: AnyAction) => S) {
        this.actionType = actionType;
        this.delegate = delegate;
    }

    canHandle(actionType: string) {
        return this.actionType == actionType;
    }

    execute(state: S, action: AnyAction) : S {
        return this.delegate(state, action);
    }
}

export function reduce<S>(state: S, action: AnyAction, caseReducers: CaseReducer<S>[]): S {
    var resultState: S = state;
    caseReducers.forEach((caseReducer : CaseReducer<S>) => {
      if(caseReducer.canHandle(action.type)) {
        resultState = caseReducer.execute(state, action);
      }
    })
    return resultState;
}