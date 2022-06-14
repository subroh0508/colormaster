const path = require('path');
const sass = require('sass');
const autoprefixer = require('autoprefixer');

const rootPath = path.resolve(__dirname, '../../../../');
const nodeModulePath = path.resolve(rootPath, 'build/js/node_modules');

config.entry.main.push(path.resolve(rootPath, 'web/material/src/jsMain/resources/app.scss'));

config.module.rules.push({
  test: /\.scss$/,
  use: [
    {
      loader: 'file-loader',
      options: {
        name: 'app.css',
      },
    },
    { loader: 'extract-loader' },
    { loader: 'css-loader' },
    {
      loader: 'postcss-loader',
      options: {
        postcssOptions: {
          plugins: [
            autoprefixer(),
          ],
        },
      },
    },
    {
      loader: 'sass-loader',
      options: {
        implementation: sass,
        webpackImporter: false,
        sassOptions: {
          includePaths: [nodeModulePath],
        },
      },
    },
  ],
});
