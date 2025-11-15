import PrintersContainer from 'app/containers/Configuration/PrintersContainer';
import * as classNames from 'classnames';
import * as React from 'react';
import { Nav, Navbar, NavDropdown } from 'react-bootstrap';
import * as style from './style.css';
import Avatar from '@material-ui/core/Avatar';
import PersonIcon from '@material-ui/icons/Person';
import InvoicesContainer from 'app/containers/Configuration/InvoicesContainer';
import { Customer } from 'app/model/Customer';


interface ConfigurationViewProps {
    customer: Customer;
    avatar?: string;
    logout: () => void;
}

enum ConfigTab {
    PROFILE = "PROFILE",
    USERS = "USERS",
    INVOICES = "INVOICES",
    PRINTERS = "PRINTERS",
    KITCHEN = "KITCHEN"
}

interface ConfigurationViewState {
    currentView: ConfigTab;
    currentSubView?: 'editPrinter';
}

export class ConfigurationView extends React.Component<ConfigurationViewProps, ConfigurationViewState> {

    constructor(props) {
        super(props);
        this.state = { currentView: ConfigTab.INVOICES };
    }

    render() {
        return <div className="h-100">
            {this.renderTabbar()}
            {this.state.currentView === ConfigTab.PRINTERS && <PrintersContainer customer={this.props.customer} />}
            {/* {this.state.currentView === ConfigTab.PROFILE && <ProfileContainer />} */}
            {this.state.currentView === ConfigTab.INVOICES && <InvoicesContainer customer={this.props.customer} />}
        </div>
    }

    private renderTabbar() {

        return (
            <div className="container-fluid">
                <Navbar className={classNames(style.navbar)} expand="lg">
                    <Navbar.Toggle aria-controls="config-navbar-nav" className={classNames(style.toggle)} />
                    <Navbar.Collapse id="config-navbar-nav">
                        <Nav onSelect={eventKey => this.changeTab(eventKey as ConfigTab)}>
                            <Nav.Item className={classNames(style.tab)}>
                                <Nav.Link className={this.isSelectedTab(ConfigTab.INVOICES)} eventKey={ConfigTab.INVOICES}>{"Facturación"}</Nav.Link>
                            </Nav.Item>
                            <Nav.Item className={classNames(style.printerNavItem, style.tab)}>
                                <Nav.Link className={this.isSelectedTab(ConfigTab.PRINTERS)} eventKey={ConfigTab.PRINTERS}>{"Impresoras"}</Nav.Link>
                            </Nav.Item>
                            {/* <Nav.Item className={classNames(style.tab)}>
                                <Nav.Link className={this.isSelectedTab(ConfigTab.USERS)} eventKey={ConfigTab.USERS}>{"Personal"}</Nav.Link>
                            </Nav.Item>
                            <Nav.Item className={style.tab}>
                                <Nav.Link className={this.isSelectedTab(ConfigTab.PROFILE)} eventKey={ConfigTab.PROFILE}>{"Perfil"}</Nav.Link>
                            </Nav.Item> */}
                        </Nav>
                    </Navbar.Collapse>
                    <p className={style.customerName}>{this.props.customer.name}</p>
                </Navbar>
                <Nav className="ml-auto">
                    <NavDropdown title={this.avatar(this.props.avatar)} id="nav-dropdown" className={style.avatar}>
                        <NavDropdown.Item href="/kitchen">Modo cocina</NavDropdown.Item>
                        <NavDropdown.Item eventKey="logout" onClick={() => this.props.logout()}>Cerrar sesión</NavDropdown.Item>
                    </NavDropdown>
                </Nav>
            </div>


        )
    }

    private avatar(avatar: string | undefined) {
        return (
            <Avatar src={avatar}>
                {!avatar && <PersonIcon />}
            </Avatar>
        )
    }

    private isSelectedTab(tab: ConfigTab): string | undefined {
        return classNames(this.state.currentView === tab ? style.selected : '');
    }

    private changeTab(tab: ConfigTab) {
        this.setState({ currentView: tab });
    }
}