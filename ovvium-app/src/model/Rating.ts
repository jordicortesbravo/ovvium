import { RatingResponse } from './response/RatingResponse';
import { RatingsPageResponse } from './response/RatingsPageResponse';

export class Rating {
    id: string;
    rating: number;
    comment?: string;
    user: string;
    lastUpdate: Date;

    constructor(rating: Rating) {
        this.id = rating.id;
        this.rating = rating.rating;
        this.comment = rating.comment;
        this.user = rating.user;
        this.lastUpdate = rating.lastUpdate;
    }

    static from(ratingResponse: RatingResponse): Rating {
        return {
            id: ratingResponse.id,
            rating: ratingResponse.rating,
            comment: ratingResponse.comment,
            user: ratingResponse.userName,
            lastUpdate: new Date(Date.parse(ratingResponse.updated.toString()))
        }
    }
}