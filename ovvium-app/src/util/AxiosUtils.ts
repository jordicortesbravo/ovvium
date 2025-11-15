import Axios, { AxiosResponse } from 'axios';
import axios from "axios";

export class AxiosUtils  {

    static setGlobalAuthHeader(token?: string) {
        Axios.defaults.headers.common['Authorization'] = token;
    }

    static setGlobalApiKeyHeader(key?: string) {    
        Axios.defaults.headers['X-API-Key'] = key ? key : undefined;        
    }

    static uploadFile<T>(file: any, endpoint: string, fileAttributeName: string) : Promise<AxiosResponse<T>> {         
        var formData = new FormData();  
        if(!file.name) {
            file.name = "unknown.jpeg";
        }       
        formData.append(fileAttributeName, file);         
        return axios.post<T>(endpoint, formData, {             
            headers: {                 
                'Content-Type': 'multipart/form-data'             
            }         
        })     
    }
}
