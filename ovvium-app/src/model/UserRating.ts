import { RatingResponse } from './response/RatingResponse';

export class UserRating {

    id: string;
    rating: number;
    comment: string;
    userName: string;
    updated: Date;

    constructor(userRating: UserRating) {
        this.id = userRating.id;
        this.rating = userRating.rating;
        this.comment = userRating.comment;
        this.userName = userRating.userName;
        this.updated = userRating.updated;
    }

    static from(ratingResponse: RatingResponse): RatingResponse {
        return {
            id: ratingResponse.id,
            rating: ratingResponse.rating,
            comment: ratingResponse.comment,
            userName: ratingResponse.userName,
            updated: ratingResponse.updated
        } as UserRating;
    }
}