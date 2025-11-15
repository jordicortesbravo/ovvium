import { TotalRatingResponse } from './response/TotalRatingResponse';

export class TotalRating {

    productId: string;
    rating: number;
    total: number;
    percentage: number;

    constructor(totalRating: TotalRating) {
        this.productId = totalRating.productId;
        this.rating = totalRating.rating;
        this.total = totalRating.total;
        this.percentage = totalRating.percentage;
    }

    static from(totalRatingResponse: TotalRatingResponse): TotalRating {
        return {
            productId: totalRatingResponse.productId,
            rating: totalRatingResponse.rating,
            total: totalRatingResponse.total,
            percentage: totalRatingResponse.percentage,
        } as TotalRating;
    }

}