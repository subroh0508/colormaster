if (config.mode === 'development') {
  config.devServer = {
    historyApiFallback: true,
    host: '0.0.0.0',
    disableHostCheck: true,
  };
}
