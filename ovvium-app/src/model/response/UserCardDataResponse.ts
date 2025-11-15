export class UserCardDataResponse {

	pciDetailsId: string;
	pan: string;
	brand: string;
	type: string;
	country: string;
	expiryDate: string;
	hash: string;
	category: string;
	sepa: string;

	constructor(response: UserCardDataResponse) {
		this.pciDetailsId = response.pciDetailsId;
		this.pan = response.pan;
		this.brand = response.brand;
		this.type = response.type;
		this.country = response.country;
		this.expiryDate = response.expiryDate;
		this.hash = response.hash;
		this.category = response.category;
		this.sepa = response.sepa;
	}

}
