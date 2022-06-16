const sass = require('sass');
const autoprefixer = require('autoprefixer');

config.entry.main.push(path.resolve(rootPath, 'web/app/src/jsMain/resources/app.scss'));

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
