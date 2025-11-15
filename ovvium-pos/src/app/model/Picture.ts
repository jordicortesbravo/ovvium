export class Picture {
  url: string;

  constructor(url: string) {
    this.url = url;
  }

  static from(pictures: any): Map<string, Picture> {
    var map = new Map<string, Picture>();
    var crops = Object.keys(pictures);
    for (var crop in crops) {
      map.set(crop, new Picture(pictures[crop].url));
    }
    return map;
  }
}

export class PictureMap {
  high: Picture;
  low: Picture;
  medium: Picture;

  constructor(pictureMap: PictureMap = {} as PictureMap) {
    this.high = pictureMap.high;
    this.low = pictureMap.low;
    this.medium = pictureMap.medium;
  }
}
