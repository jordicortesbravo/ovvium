import { RatingResponse } from './RatingResponse';

export class RatingsPageResponse {

	pageOffset: number;
	totalPages: number;
	numberOfElements: number;
	ratings: RatingResponse[];

    constructor(ratingsPageResponse: RatingsPageResponse) {
        this.pageOffset = ratingsPageResponse.pageOffset;
        this.totalPages = ratingsPageResponse.totalPages;
        this.numberOfElements = ratingsPageResponse.numberOfElements;
        this.ratings = ratingsPageResponse.ratings;
    }
}