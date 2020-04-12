if (config.devServer) {
    config.devServer.historyApiFallback = true;
    config.devServer.host = '0.0.0.0';
    config.devServer.disableHostCheck = true;
} else {
    config.devServer = {
        historyApiFallback: true,
        host: '0.0.0.0',
        disableHostCheck: true,
    };
}
