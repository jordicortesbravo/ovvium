import React from "react";
import { ListRenderItemInfo, SectionList, SectionListData, Text, View, FlatList } from 'react-native';
import { ProductType } from '../../../model/enum/ProductType';
import { Product } from '../../../model/Product';
import { ProductService } from '../../../services/ProductService';
import { Allergen } from "../../../model/enum/Allergen";
import { Invoice } from "../../../model/Invoice";
import { MenuItem } from "../MenuItem/MenuItem";

interface InvoiceResumeListProps {
    invoices: Invoice[];
    loadMore: (page: number) => Invoice[];
}
interface InvoiceResumeListState {
    page: number;
}

// FIXME Revisar si esto se está usando, sino, eliminar.
export class InvoiceResumeList extends React.Component<InvoiceResumeListProps, InvoiceResumeListState> {

    constructor(props: InvoiceResumeListProps) {
        super(props);
        this.state = { page: 0 }
    }

    render() {
        return <FlatList data={this.props.invoices} renderItem={this.renderItem.bind(this)}
            style={{ marginTop: 10 }}
            keyExtractor={(invoice: Invoice) => 'invoice_' + invoice.id} />
    }

    renderItem = (info: ListRenderItemInfo<Invoice>) => (
        <MenuItem title={info.item.customer!.name} subtitle={info.item.creationDate.toLocaleDateString() + " " + this.appendLeadingZeroes(info.item.creationDate.getHours()) + ":" + this.appendLeadingZeroes(info.item.creationDate.getMinutes())} />
    );

    // FIXME Improve this using a date library
    appendLeadingZeroes(n: number) {
        if (n <= 9) {
            return "0" + n;
        }
        return n
    }
}