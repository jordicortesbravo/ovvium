const ENV = {
  local: {
    debug: true,
    //baseUrl: 'https://api.ovvium.cloud/public/api/v1',
    //staticsBaseUrl: "https://ovvium.cloud/rsc",
    baseUrl: 'http://localhost:8080/public/api/v1',
    staticsBaseUrl: "https://www-dev.ovvium.cloud/rsc",
    apiKey: {
      ovviumApi: "uzU5qp7y5lpSmhKOJpaZRMRYOc7DDaYO",
      bugSnag: "a92a969256d6795183b064a1c16457bf"
    },
    customer: {
      get: '/customers/{customerId}'
    },
    products: {
      list: '/customers/{customerId}/products',
      addPhoto: '/customers/{customerId}/products/{productId}/pictures',
      get: '/customers/{customerId}/products/{productId}'
    },
    categories: {
      list: '/customers/{customerId}/categories'
    },
    user: {
      login: '/account/login',
      refreshToken: '/account/token/renew',
      recoverPassword: '/account/recover'
    },
    locations: {
      list: '/customers/{customerId}/locations'
    },
    bill: {
      create: '/customers/{customerId}/bills',
      delete: '/customers/{customerId}/bills/{billId}',
      list: '/customers/{customerId}/bills',
      get: '/customers/{customerId}/bills/{billId}',
      update: '/customers/{customerId}/bills/{billId}',
      order: {
        create: '/customers/{customerId}/bills/{billId}/orders',
        remove: '/customers/{customerId}/bills/{billId}/orders/{orderId}',
        get: '/customers/{customerId}/bills/{billId}/orders/{orderId}',
      },
      join: '/customers/{customerId}/bills/{billId}/join'
    },
    ratings: {
      ratings: '/ratings',
      rating: '/ratings/{id}',
      totals: '/ratings/totals',
      page: '/ratings/page'
    },
    invoice: {
      create: '/customers/{customerId}/invoices',
      page: '/customers/{customerId}/invoices',
      get: '/customers/{customerId}/invoices/{invoiceId}',
    },
    invoiceDate: {
      page: '/customers/{customerId}/invoice-dates',
      last: '/customers/{customerId}/invoice-dates/last',
      create: '/customers/{customerId}/invoice-dates',
      update: '/customers/{customerId}/invoice-dates/{invoiceDateId}',
    },
    payment: {
      cash: '/payments/cash',
      card: '/payments/card'
    },
    social: {
      facebook: {
        authUrl: 'https://www.facebook.com/v3.2/dialog/oauth',
        appId: '238726070344520'
      }
    },
    jobs: {
      loadBills: {
        delay: 10000
      }
    }
  },
  dev: {
    debug: false,
    baseUrl: 'https://api-dev.ovvium.cloud/public/api/v1',
    staticsBaseUrl: "https://www-dev.ovvium.cloud/rsc",
    apiKey: {
      ovviumApi: "uzU5qp7y5lpSmhKOJpaZRMRYOc7DDaYO",
      bugSnag: "a92a969256d6795183b064a1c16457bf"
    },
    customer: {
      get: '/customers/{customerId}'
    },
    products: {
      list: '/customers/{customerId}/products',
      addPhoto: '/customers/{customerId}/products/{productId}/pictures',
      get: '/customers/{customerId}/products/{productId}'
    },
    categories: {
      list: '/customers/{customerId}/categories'
    },
    user: {
      login: '/account/login',
      refreshToken: '/account/token/renew',
      recoverPassword: '/account/recover'
    },
    locations: {
      list: '/customers/{customerId}/locations'
    },
    bill: {
      create: '/customers/{customerId}/bills',
      delete: '/customers/{customerId}/bills/{billId}',
      list: '/customers/{customerId}/bills',
      get: '/customers/{customerId}/bills/{billId}',
      update: '/customers/{customerId}/bills/{billId}',
      order: {
        create: '/customers/{customerId}/bills/{billId}/orders',
        remove: '/customers/{customerId}/bills/{billId}/orders/{orderId}',
        get: '/customers/{customerId}/bills/{billId}/orders/{orderId}',
      },
      join: '/customers/{customerId}/bills/{billId}/join'
    },
    ratings: {
      ratings: '/ratings',
      rating: '/ratings/{id}',
      totals: '/ratings/totals',
      page: '/ratings/page'
    },
    invoice: {
      create: '/customers/{customerId}/invoices',
      page: '/customers/{customerId}/invoices',
      get: '/customers/{customerId}/invoices/{invoiceId}',
    },
    invoiceDate: {
      page: '/customers/{customerId}/invoice-dates',
      last: '/customers/{customerId}/invoice-dates/last',
      create: '/customers/{customerId}/invoice-dates',
      update: '/customers/{customerId}/invoice-dates/{invoiceDateId}',
    },
    payment: {
      cash: '/payments/cash',
      card: '/payments/card'
    },
    social: {
      facebook: {
        authUrl: 'https://www.facebook.com/v3.2/dialog/oauth',
        appId: '238726070344520'
      }
    },
    jobs: {
      loadBills: {
        delay: 5000
      }
    }
  },
  staging: {
    debug: false,
    baseUrl: 'https://api.ovvium.cloud/public/api/v1',
    staticsBaseUrl: "https://ovvium.cloud/rsc",
    apiKey: {
      ovviumApi: "uzU5qp7y5lpSmhKOJpaZRMRYOc7DDaYO",
      bugSnag: "a92a969256d6795183b064a1c16457bf"
    },
    customer: {
      get: '/customers/{customerId}'
    },
    products: {
      list: '/customers/{customerId}/products',
      addPhoto: '/customers/{customerId}/products/{productId}/pictures',
      get: '/customers/{customerId}/products/{productId}'
    },
    categories: {
      list: '/customers/{customerId}/categories'
    },
    user: {
      login: '/account/login',
      refreshToken: '/account/token/renew',
      recoverPassword: '/account/recover'
    },
    locations: {
      list: '/customers/{customerId}/locations'
    },
    bill: {
      create: '/customers/{customerId}/bills',
      delete: '/customers/{customerId}/bills/{billId}',
      list: '/customers/{customerId}/bills',
      get: '/customers/{customerId}/bills/{billId}',
      update: '/customers/{customerId}/bills/{billId}',
      order: {
        create: '/customers/{customerId}/bills/{billId}/orders',
        remove: '/customers/{customerId}/bills/{billId}/orders/{orderId}',
        get: '/customers/{customerId}/bills/{billId}/orders/{orderId}',
      },
      join: '/customers/{customerId}/bills/{billId}/join'
    },
    ratings: {
      ratings: '/ratings',
      rating: '/ratings/{id}',
      totals: '/ratings/totals',
      page: '/ratings/page'
    },
    invoice: {
      create: '/customers/{customerId}/invoices',
      page: '/customers/{customerId}/invoices',
      get: '/customers/{customerId}/invoices/{invoiceId}',
    },
    invoiceDate: {
      page: '/customers/{customerId}/invoice-dates',
      last: '/customers/{customerId}/invoice-dates/last',
      create: '/customers/{customerId}/invoice-dates',
      update: '/customers/{customerId}/invoice-dates/{invoiceDateId}',
    },
    payment: {
      cash: '/payments/cash',
      card: '/payments/card'
    },
    social: {
      facebook: {
        authUrl: 'https://www.facebook.com/v3.2/dialog/oauth',
        appId: '238726070344520'
      }
    },
    jobs: {
      loadBills: {
        delay: 10000
      }
    }
  },
  prod: {
    debug: false,
    baseUrl: 'https://api.ovvium.com/public/api/v1',
    staticsBaseUrl: "https://ovvium.com/rsc",
    apiKey: {
      ovviumApi: "uzU5qp7y5lpSmhKOJpaZRMRYOc7DDaYO",
      bugSnag: "a92a969256d6795183b064a1c16457bf"
    },
    customer: {
      get: '/customers/{customerId}'
    },
    products: {
      list: '/customers/{customerId}/products',
      addPhoto: '/customers/{customerId}/products/{productId}/pictures',
      get: '/customers/{customerId}/products/{productId}'
    },
    categories: {
      list: '/customers/{customerId}/categories'
    },
    user: {
      login: '/account/login',
      refreshToken: '/account/token/renew',
      recoverPassword: '/account/recover'
    },
    locations: {
      list: '/customers/{customerId}/locations'
    },
    bill: {
      create: '/customers/{customerId}/bills',
      delete: '/customers/{customerId}/bills/{billId}',
      list: '/customers/{customerId}/bills',
      get: '/customers/{customerId}/bills/{billId}',
      update: '/customers/{customerId}/bills/{billId}',
      order: {
        create: '/customers/{customerId}/bills/{billId}/orders',
        remove: '/customers/{customerId}/bills/{billId}/orders/{orderId}',
        get: '/customers/{customerId}/bills/{billId}/orders/{orderId}',
      },
      join: '/customers/{customerId}/bills/{billId}/join'
    },
    ratings: {
      ratings: '/ratings',
      rating: '/ratings/{id}',
      totals: '/ratings/totals',
      page: '/ratings/page'
    },
    invoice: {
      create: '/customers/{customerId}/invoices',
      page: '/customers/{customerId}/invoices',
      get: '/customers/{customerId}/invoices/{invoiceId}',
    },
    invoiceDate: {
      page: '/customers/{customerId}/invoice-dates',
      last: '/customers/{customerId}/invoice-dates/last',
      create: '/customers/{customerId}/invoice-dates',
      update: '/customers/{customerId}/invoice-dates/{invoiceDateId}',
    },
    payment: {
      cash: '/payments/cash',
      card: '/payments/card'
    },
    social: {
      facebook: {
        authUrl: 'https://www.facebook.com/v3.2/dialog/oauth',
        appId: '238726070344520'
      }
    },
    jobs: {
      loadBills: {
        delay: 10000
      }
    }
  }
};

function getProperties(releaseChannel: string) {
  let props = ENV.local;
  if(releaseChannel) {
    if(releaseChannel === 'development') {
      props = ENV.dev;
    } else if(releaseChannel === 'staging') {
      props = ENV.staging;
    } else if(releaseChannel === 'production') {
      props = ENV.prod;
    }
  }
  return props;
}

export function getEnv(): string {
  return process.env.NODE_ENV ? process.env.NODE_ENV : 'local';
}

const properties = getProperties(getEnv());
export { properties };
