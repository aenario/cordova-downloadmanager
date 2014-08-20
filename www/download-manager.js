module.exports = window.DownloadManager = DownloadManager = {

    startDownload: function (url, path, headers, progressback, callback) {

        success = function(list) {callback(null, list);}
        error = function(err) {callback(err);}

        args = [url, path, headers]

        return cordova.exec(success, error, "DownloadManagerPlugin", "download", args);
    }
};