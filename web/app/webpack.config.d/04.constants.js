const webpack = require('webpack');

const prod = config.mode === 'production';

const API_KEY = prod ? 'AIzaSyAa9XK8rRymTOsfNpb2Fejn6aAo7A0c13o' : 'AIzaSyAEg1EDJyytx_9M-6r63osIdOvmHW1qr_I';
const AUTH_DOMAIN = prod ? 'imas-colormaster.firebaseapp.com' : 'color-master-2875c.firebaseapp.com';
const DATABASE_URL = prod ? 'https://imas-colormaster.firebaseio.com' : 'https://color-master-2875c.firebaseio.com';
const PROJECT_ID = prod ? 'imas-colormaster' : 'color-master-2875c';
const STORAGE_BUCKET = prod ? 'imas-colormaster.appspot.com' : 'color-master-2875c.appspot.com';
const MESSAGING_SENDER_ID = prod ? '1084041594481' : '1067956718797';
const APP_ID = prod ? '1:1084041594481:web:d21eb204c9ebae39a1bdd0' : '1:1067956718797:web:9c04b4cef92b722d4b2d84';
const MEASUREMENT_ID = prod ? 'G-XV1LEWMH6K' : 'G-JPJRS76T16';

config.plugins.push(
  new webpack.DefinePlugin({
    APP_NAME: '"COLOR M@STER"',
    APP_VERSION: '"v2022.10.10"',
    API_KEY: `"${API_KEY}"`,
    AUTH_DOMAIN: `"${AUTH_DOMAIN}"`,
    DATABASE_URL: `"${DATABASE_URL}"`,
    PROJECT_ID: `"${PROJECT_ID}"`,
    STORAGE_BUCKET: `"${STORAGE_BUCKET}"`,
    MESSAGING_SENDER_ID: `"${MESSAGING_SENDER_ID}"`,
    APP_ID: `"${APP_ID}"`,
    MEASUREMENT_ID: `"${MEASUREMENT_ID}"`,
  }),
);
