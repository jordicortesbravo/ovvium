
export class TotalRatingResponse {
    productId: string;
    rating: number;
    total: number;
    percentage: number;

    constructor(totalRatingResponse: TotalRatingResponse = {} as TotalRatingResponse) {
        this.productId = totalRatingResponse.productId;
        this.rating = totalRatingResponse.rating;
        this.total = totalRatingResponse.total;
        this.percentage = totalRatingResponse.percentage;
    }
}