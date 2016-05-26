(function($){

    $.webUpload = function(picker, url, options){
         options = options || {};
        // 初始化Web Uploader
        var uploader = new WebUploader.Uploader({

            // 选完文件后，是否自动上传。
            auto: options.auto || false,

            // 文件接收服务端。
            server: url,

            pick: picker,
            fileNumLimit: options.max || undefined,
            fileSingleSizeLimit: 5242880,
            formData: options.data,

            // 只允许选择图片文件。
            accept: {
                title: '图片',
                extensions: 'gif,jpg,jpeg,bmp,png',
                mimeTypes: 'image/*'
            },
            compress: {
                width: 800,
                height: 800,
                quality: 90,
                allowMagnify: false,
                crop: false,
                preserveHeaders: true,
                noCompressIfLarger: true,
                compressSize: 0
            }
        });

        if(options.pickedCallback){
            uploader.on( 'fileQueued', function(file) {
                 uploader.makeThumb(file, function( error, src ) {
                     options.pickedCallback(file, src);
                 }, 120, 120 );
            });
        }

        if(options.uploadedCallback){
            uploader.on('uploadSuccess', function(file, resp) {
                options.uploadedCallback(file, resp);
            });
        }

        if(options.uploadError){
             uploader.on('uploadError', function(file) {
                options.uploadError(file);
             });
        }

        if(options.uploadFinished){
            uploader.on('uploadFinished', function() {
                options.uploadFinished();
            });
        }

        return uploader;
    };

})(window.$ || window.jQuery || window.Zepto);