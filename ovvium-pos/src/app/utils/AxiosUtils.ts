import Axios from 'axios';


export class AxiosUtils  {

    static setGlobalAuthHeader(token?: string) {
        AxiosUtils.setAuthHeaderToConfig(Axios.defaults.headers.common, token);
    }

    static setAuthHeaderToConfig(config: any, token?: string) {    
        config['Authorization'] = token ? 'Bearer ' +token : undefined;        
    }

    static setGlobalApiKeyHeader(key?: string) {    
        Axios.defaults.headers['X-API-Key'] = key ? key : undefined;        
    }
   

}
