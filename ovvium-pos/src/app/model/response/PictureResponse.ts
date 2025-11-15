export class PictureResponse {
    url: string;

    constructor(url: string) {
        this.url = url;
    }
}

export class PictureMapResponse {
    high: PictureResponse;
    low: PictureResponse;
    medium: PictureResponse;
  
    constructor(pictureMap: PictureMapResponse = {} as PictureMapResponse) {
      this.high = pictureMap.high;
      this.low = pictureMap.low;
      this.medium = pictureMap.medium;
    }
  }