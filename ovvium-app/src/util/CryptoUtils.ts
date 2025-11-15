import CryptoJS from 'crypto-js';
import { properties } from "../../resources/Properties";

export class CryptoUtils {

    // The encription is not deterministic on AES by default for security reasons
    static encrypt(text: string, key: string) : string {
        return CryptoJS.AES.encrypt(text, key).toString();
    }
    
    static decrypt(text: string, key: string) :string {
        var bytes = CryptoJS.AES.decrypt(text, key);
        return bytes.toString(CryptoJS.enc.Utf8);

    }
}
