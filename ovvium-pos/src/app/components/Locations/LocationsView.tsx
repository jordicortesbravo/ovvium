import { Bill } from 'app/model/Bill';
import { Location } from 'app/model/Location';
import * as React from 'react';
import { LocationGrid } from 'app/components/LocationGrid/LocationGrid';
import * as classNames from 'classnames';
import * as style from './style.css';
import { Tile } from 'app/components/Tile/Tile';
import { Row } from 'react-bootstrap';
import { ArrayUtils } from 'app/utils/ArrayUtils';
import { Separator } from 'app/components/Separator/Separator';
import { ConfirmButtons } from '../ConfirmButtons/ConfirmButtons';
import { RemoveBillConfirmDialog } from '../Dialog/RemoveBillConfirmDialog';

interface LocationsViewProps {
    bills: Array<Bill>;
    locations: Array<Location>;
    selectedBill?: Bill;

    onCreateBill: (...locations: Array<Location>) => void;
    onChangeCurrentBill: (bill: Bill) => void;
    onJoinLocations: (locations: Array<Location>, bill?: Bill) => void;
    onRemoveBill: (bill: Bill) => void;
}

interface LocationsViewState {
    viewType: 'grid' | 'map';
    zone: string;
    zonedLocations: Array<Location>;
    zones: Array<string>;
    option: 'joining' | 'removing' | 'none';
    selectedLocations: Array<Location>;
    selectedBillToRemove?: Bill;
    showRemoveWarning: boolean;
}

export class LocationsView extends React.Component<LocationsViewProps, LocationsViewState> {

    constructor(props: LocationsViewProps) {
        super(props);
        var zone = props.selectedBill ? props.selectedBill.locations[0].zone : props.locations[0].zone;
        this.state = {
            viewType: 'grid',
            zone: zone,
            zonedLocations: props.locations.filter(l => l.zone == zone),
            zones: Array.from(new Set(props.locations.map(l => l.zone))),
            option: 'none',
            selectedLocations: props.selectedBill ? props.selectedBill.locations : [],
            showRemoveWarning: false
        }
    }

    componentDidUpdate(prevProps: LocationsViewProps, prevState: LocationsViewState) {
        if ((this.props.selectedBill && prevState.selectedLocations.length != this.props.selectedBill.locations.length)
            || (prevProps.selectedBill != undefined && this.props.selectedBill == undefined)) {
            this.setState({
                selectedLocations: this.props.selectedBill ? this.props.selectedBill.locations : [],
            })
        }
    }

    render() {
        var hasMultipleZones = this.state.zones.length > 1;
        let locationsClasses = classNames(
            style.locationsContainer,
            this.state.option != 'none' ? style.withConfirmButtons : ''
        )
        return <div className={classNames('h-100', 'w-100', style.container)}>
            <div className={locationsClasses}>
                <LocationGrid
                    joiningTables={this.isJoining()}
                    removingBills={this.isRemoving()}
                    selectedLocations={this.state.selectedLocations}
                    selectedBill={this.state.selectedBillToRemove}
                    locationBillMap={this.getBillLocationsMap(this.state.zonedLocations)}
                    onClickLocation={this.onSelectLocation.bind(this)}
                />
            </div>
            <Row style={{ height: '0.5%' }}></Row>
            {this.isJoining() && <Row className={style.buttonsContainer}>
                <ConfirmButtons
                    onAccept={this.joinTables.bind(this)}
                    onCancel={this.onCancel.bind(this)}
                />
            </Row>}
            {this.isRemoving() && <Row className={style.buttonsContainer}>
                <ConfirmButtons
                    onAccept={this.onAcceptRemoveBill.bind(this)}
                    onCancel={this.onCancel.bind(this)}
                    acceptDisabled={!this.state.selectedBillToRemove}
                />
            </Row>}
            <div className={style.buttonsContainer}>
                {hasMultipleZones && this.renderZones()}
                {hasMultipleZones &&
                    <Separator orientation='vertical' />
                }
                {this.state.option == 'none' &&
                    <Tile
                        value="Unir mesas"
                        key="join-locations"
                        onClick={this.onClickJoinTablesButton.bind(this)}
                        className={classNames(style.tile, style.billButton)}
                    />
                }
                {this.state.option == 'none' &&
                    <Tile
                        value="Eliminar cuenta"
                        key="remove-bill"
                        onClick={this.onClickRemoveBillsButton.bind(this)}
                        className={classNames(style.tile, style.billButton)}
                    />
                }
            </div>
            <Row style={{ height: '0.5%' }}></Row>
            {this.state.showRemoveWarning && <RemoveBillConfirmDialog
                onCancel={() => this.setState({ showRemoveWarning: false })}
                onAccept={this.removeBill.bind(this)}
                bill={this.state.selectedBillToRemove}
            />}
        </div>
    }


    private isRemoving(): boolean {
        return this.state.option == 'removing';
    }

    private isJoining() {
        return this.state.option == 'joining';
    }

    renderZones() {
        if (this.state.zones.length > 1) {
            return this.state.zones.map(z => this.renderZone(z));
        }
        return <></>;
    }

    renderZone(zone: string) {
        let width = 100 / this.state.zones.length / 2 + "%";
        return <Tile
            value={zone}
            key={zone}
            onClick={() => this.onSelectZone(zone)}
            selected={zone == this.state.zone}
            className={style.tile}
            style={{ width: width }}
        />
    }

    onClickJoinTablesButton() {
        this.setState({ option: 'joining' });
    }

    onClickRemoveBillsButton() {
        this.setState({ option: 'removing' });
    }

    onCancel() {
        this.setState({
            option: 'none',
            selectedLocations: !this.props.selectedBill ? [] : this.props.selectedBill.locations,
            selectedBillToRemove: undefined
        });
    }

    joinTables() {
        if (this.state.selectedLocations.length > 1) {
            var destinationBill: Bill | undefined;
            for (var i in this.state.selectedLocations) {
                var location = this.state.selectedLocations[i];
                for (var j in this.props.bills) {
                    var bill = this.props.bills[j];
                    if (ArrayUtils.contains(bill.locations, location, 'id')) {
                        destinationBill = bill;
                        break;
                    }
                }
            }
            this.props.onJoinLocations(this.state.selectedLocations, destinationBill);
        }
        this.onCancel();
    }

    onAcceptRemoveBill() {
        if (this.state.selectedBillToRemove!!.orders.length > 0) {
            this.setState({ showRemoveWarning: true })
        } else {
            this.removeBill();
        }
    }

    private removeBill() {
        this.props.onRemoveBill(this.state.selectedBillToRemove!!);
        this.onCancel();
    }

    onSelectLocation(location: Location, bill?: Bill) {
        this.setState({ zone: location.zone });
        switch (this.state.option) {
            case 'joining':
                var selectedLocations = this.state.selectedLocations.slice();
                if (ArrayUtils.contains(selectedLocations, location, 'id')) {
                    if (bill) {
                        bill.locations.forEach(l => ArrayUtils.remove(selectedLocations, l, 'id'));
                    } else {
                        ArrayUtils.remove(selectedLocations, location, 'id');
                    }
                } else {
                    if (bill) {
                        bill.locations.forEach(l => selectedLocations.push(l));
                    } else {
                        selectedLocations.push(location);
                    }
                }
                this.setState({ selectedLocations });
                break;
            case 'removing':
                if (bill) {
                    this.setState({ selectedBillToRemove: bill });
                }
                break;
            case 'none':
                if (bill) {
                    this.props.onChangeCurrentBill(bill);
                } else {
                    this.props.onCreateBill(location);
                }
                break;
        }
    }

    onSelectZone(zone: string) {
        this.setState({ zone: zone, zonedLocations: this.props.locations.filter(l => l.zone == zone) })
    }

    private getBillLocationsMap(locations: Array<Location>) {
        var map = new Map<Location, Bill | undefined>();
        locations.forEach(l => {
            map.set(l, this.getBill(this.props.bills, l));
        })
        return map;
    }

    private getBill(bills: Array<Bill>, location: Location) {
        for (let i in bills) {
            var bill = bills[i];
            if (ArrayUtils.contains(bill.locations, location, "id")) {
                return bill;
            }
        }
        return undefined;
    }
}