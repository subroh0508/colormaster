if (config.mode === 'development') {
    config.devServer.historyApiFallback = true;
    config.devServer.host = '0.0.0.0';
    config.devServer.disableHostCheck = true;
}
