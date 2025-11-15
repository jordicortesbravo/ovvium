
export class RatingResponse {
    id: string;
    rating: number;
    comment: string;
    userName: string;
    updated: Date;

    constructor(ratingResponse: RatingResponse = {} as RatingResponse) {
        this.id = ratingResponse.id;
        this.comment = ratingResponse.comment;
        this.userName = ratingResponse.userName;
        this.rating = ratingResponse.rating;
        this.updated = ratingResponse.updated;
    }
}