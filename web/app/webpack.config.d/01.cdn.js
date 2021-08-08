const path = require('path');
const WebpackCdnPlugin = require('webpack-cdn-plugin');
const HtmlWebpackPlugin = require('html-webpack-plugin');

const rootPath = path.resolve(__dirname, '../../../../')
const webAppPath = path.resolve(rootPath, 'web/app');

config.output.publicPath = '/';

config.plugins.push(
  new HtmlWebpackPlugin({
    template: path.resolve(webAppPath, 'build/processedResources/js/main/index.html'),
    filename: path.resolve(webAppPath, 'build/distributions/index.html'),
  }),
);

if (config.mode === 'production') {
  config.externals = [
    {
      react: 'React',
      'react-dom': 'ReactDOM',
      'react-router-dom': 'ReactRouterDOM',
      '@material-ui/core': 'MaterialUI',
      'i18next': 'i18next',
      'i18next-http-backend': 'i18nextHttpBackend',
      'react-i18next': 'ReactI18next',
      'firebase': 'firebase',
      'firebase/app': 'firebase',
      'firebase/auth': 'firebase',
      'firebase/firestore': 'firebase',
    },
  ];
  config.plugins.push(
    new WebpackCdnPlugin({
      modules: [
        {
          name: 'react',
          path: 'umd/react.production.min.js',
        },
        {
          name: 'react-dom',
          path: 'umd/react-dom.production.min.js',
        },
        {
          name: 'react-router-dom',
          path: 'umd/react-router-dom.min.js',
        },
        {
          name: '@material-ui/core',
          path: 'umd/material-ui.production.min.js',
        },
        {
          name: 'i18next',
          path: 'dist/umd/i18next.min.js',
        },
        {
          name: 'i18next-http-backend',
          path: 'i18nextHttpBackend.min.js',
        },
        {
          name: 'react-i18next',
          path: 'react-i18next.min.js',
        },
        {
          name: 'firebase',
          paths: [
            'firebase-app.js',
            'firebase-auth.js',
            'firebase-firestore.js',
          ],
        },
      ],
      pathToNodeModules: path.resolve(rootPath, 'build/js'),
    }),
  );
}
