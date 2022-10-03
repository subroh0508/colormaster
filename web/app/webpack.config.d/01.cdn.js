const WebpackCdnPlugin = require('webpack-cdn-plugin');
const HtmlWebpackPlugin = require('html-webpack-plugin');

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
      'i18next': 'i18next',
      'i18next-http-backend': 'i18nextHttpBackend',
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
          name: 'i18next',
          path: 'dist/umd/i18next.min.js',
        },
        {
          name: 'i18next-http-backend',
          path: 'i18nextHttpBackend.min.js',
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
