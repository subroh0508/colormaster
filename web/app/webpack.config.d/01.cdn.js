const path = require('path');
const WebpackCdnPlugin = require('webpack-cdn-plugin');
const HtmlWebpackPlugin = require('html-webpack-plugin');

const rootPath = path.resolve(__dirname, '../../../../')
const webAppPath = path.resolve(rootPath, 'web/app');

config.plugins.push(
  new HtmlWebpackPlugin({
    template: path.resolve(webAppPath, 'build/processedResources/Js/main/index.html'),
    filename: path.resolve(webAppPath, 'build/distributions/index.html'),
  }),
);

if (config.mode === 'production') {
  config.externals = [
    {
      react: 'React',
      'react-dom': 'ReactDOM',
      // 'react-router-dom': 'ReactRouterDom',
      // '@material-ui/core': 'MaterialUI',
    },
  ];
  config.plugins.push(
    new WebpackCdnPlugin({
      modules: [
        {
          name: 'react',
          var: 'React',
          path: 'umd/react.production.min.js',
        },
        {
          name: 'react-dom',
          var: 'ReactDOM',
          path: 'umd/react-dom.production.min.js',
        },
        /*
        {
          name: 'react-router-dom',
          var: 'ReactRouterDOM',
          path: 'umd/react-router-dom.min.js',
        },
        {
          name: '@material-ui/core',
          path: 'umd/material-ui.production.min.js',
        },
        */
      ],
      pathToNodeModules: path.resolve(rootPath, 'build/js'),
    }),
  );
}
