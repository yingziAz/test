jQuery(function() {
    var $ = jQuery,    // just in case. Make sure it's not an other libaray.
        $wrap = $('#uploader'),
        // 图片容器
        $queue = $('<ul class="filelist"></ul>').appendTo( $wrap.find('.queueList') ),
        // 状态栏，包括进度和控制按钮
        $statusBar = $wrap.find('.statusBar'),
        // 文件总体选择信息。
        $info = $statusBar.find('.info'),
        // 上传按钮
        $upload = $wrap.find('.uploadBtn'),
        // 没选择文件之前的内容。
        $placeHolder = $wrap.find('.placeholder'),
        // 总体进度条
        $progress = $statusBar.find('.progress').hide(),
        // 添加的文件数量
        fileCount = 0,
        // 添加的文件总大小
        fileSize = 0,
        // 优化retina, 在retina下这个值是2
        ratio = window.devicePixelRatio || 1,
        // 缩略图大小
        thumbnailWidth = 110 * ratio,
        thumbnailHeight = 110 * ratio,
        // 可能有pedding, ready, uploading, confirm, done.
        state = 'pedding',
        // 所有文件的进度信息，key为file id
        percentages = {},
        supportTransition = (function(){
            var s = document.createElement('p').style,
                r = 'transition' in s ||
                      'WebkitTransition' in s ||
                      'MozTransition' in s ||
                      'msTransition' in s ||
                      'OTransition' in s;
            s = null;
            return r;
        })(),
        // WebUploader实例
        uploader,
    //add by su 20190123 begin
        largeFile= {};
    //add by su 20190123 end
    

    ////////////////////////////////////////////////////////////////////////////////////////
    //add by su 20190123 begin
 // HOOK 这个必须要再uploader实例化前面
    WebUploader.Uploader.register({
        'before-send-file': 'beforeSendFile',
        'before-send': 'beforeSend',
        "after-send-file":"afterSendFile",  //分片上传完毕  
    }, {
        beforeSendFile: function (file) {
        	largeFile.name= file.name; 
        	largeFile.size = file.size;
            // Deferred对象在钩子回掉函数中经常要用到，用来处理需要等待的异步操作。
            var deferred = WebUploader.Deferred();
            // 根据文件内容来查询MD5
            uploader.md5File(file,0,5*1024*1024).progress(function(percentage){
            	
                getProgressBar(file, percentage, "MD5", "MD5");
                
            }).then(function (val) { // 完成
            	largeFile.md5 = val;
                // 通过服务器进行md5的判断
            	$.ajax({ 
                	type:"POST",    
                	url:ctx+'/attach/large/file/upload/checkFileMd5',
                	data:{    
                		fileMd5: largeFile.md5
                	},    
                	cache: false,  
                	dataType:"json",
                	success:function(result){
                		if(result.state = "ok"){
                			var _data = result.data;
                        	console.log(_data);
                			if(_data && _data.attachId != ''){
                				largeFile.attachId = _data.attachId;
                				largeFile.name = _data.fileName;
                				largeFile.suffix = _data.fileSuffix;
                    			uploader.skipFile(file);
                			}
                			deferred.resolve();
                		} else {
                			resultErr(result);
                			deferred.reject();
                		}
                	}
                });
            });
            return $.when(deferred);
        },
        beforeSend: function (block) {
        	var deferred = WebUploader.Deferred();  
        	$.ajax({  
        		type:"POST",  
        		url:ctx+'/attach/large/file/upload/checkChunk',  
        		data:{ 
            		fileMd5:largeFile.md5,  
            		chunk: block.chunk,  
            		chunkSize:block.end-block.start  
            	}, 
            	dataType:"json",
            	success:function(result){
            		if(result.state = "ok"){
            			var alreadUploaded = result.data;
            			if(alreadUploaded){
                			deferred.reject();
            			}else{
                			deferred.resolve();
            			}
            			//deferred.resolve();
            		} else {
            			resultErr(result);
            			deferred.reject();
            		}
            	}
            });
        	this.owner.options.formData.fileMd5 = largeFile.md5;
        	deferred.resolve();  
            return deferred.promise();  
        },
        //时间点3：所有分块上传成功后调用此函数    
        afterSendFile:function(){
        	//console.log("-------------->111");
        	if(!isNullOrUndefined(largeFile.attachId)){
            	//console.log("-------------->2222");
            	//console.log(largeFile);
        		/**
        		 * 文件上传回调
        		 * @param attachField  附件对应的id名称
        		 * @param attachDivId  附件上一层最大的divId
        		 * @param attachId  本次上传获取的附件id
        		 * @param attachFileName  本次上传获取的附件名称
        		 * @param attachFileSuffix   本次上传获取的附件后缀名
        		 * @param attachNum  附件的最大个数 如果值为1，则默认是单张上传
        		 * @param fileType  Y:是图片 N：不是图片  ，不填默认为文件（非图片）
        		 * @param position  位置  m:中部  b:底部  默认中部
        		 * @returns
        		 */
        		parent.uploadFileComplete(attachField, attachDivId,largeFile.attachId,largeFile.name,largeFile.suffix,attachNum,fileType,position);
        		$("#largeFileUploadModal").modal('hide');  //手动关闭
        		return false;
        	}else{
                $.ajax({    
                    type:"POST",    
                    url:ctx+'/attach/large/file/upload/mergeChunks', 
                    data:{
                        fileMd5: largeFile.md5,
                        fileName: largeFile.name,
                        fileSize: largeFile.size
                    },
                    success:function(result){
                    	if(result.state=="ok"){
                    		var _data = result.data;
                    		console.log(result);
                    		/**
                    		 * 文件上传回调
                    		 * @param attachField  附件对应的id名称
                    		 * @param attachDivId  附件上一层最大的divId
                    		 * @param attachId  本次上传获取的附件id
                    		 * @param attachFileName  本次上传获取的附件名称
                    		 * @param attachFileSuffix   本次上传获取的附件后缀名
                    		 * @param attachNum  附件的最大个数 如果值为1，则默认是单张上传
                    		 * @param fileType  Y:是图片 N：不是图片  ，不填默认为文件（非图片）
                    		 * @param position  位置  m:中部  b:底部  默认中部
                    		 * @returns
                    		 */
                    		parent.uploadFileComplete(attachField, attachDivId,_data.attachId,_data.fileName,_data.fileSuffix,attachNum,fileType,position);
                    		$("#largeFileUploadModal").modal('hide');  //手动关闭
                    	}else{
                			resultErr(result);
                    	}
                    }
                });
        	}
        }    
    });
    //add by su 20190123 end
    ////////////////////////////////////////////////////////////////////////////////////////
    
    if (!WebUploader.Uploader.support() ) {
        alert( 'Web Uploader 不支持您的浏览器！如果你使用的是IE浏览器，请尝试升级 flash 播放器');
        throw new Error( 'WebUploader does not support the browser you are using.' );
    }

    // 实例化 a:无限制 w:word i:image e:excel 默认无限制：a
	if (fileType == 'i') {
		uploader = WebUploader.create({
			pick: {
				id: '#filePicker',
				label: '点击选择文件'
			},
			dnd: '#uploader .queueList',
			paste: document.body,
			accept : {
				title : 'Images',
				extensions : 'gif,jpg,jpeg,bmp,png',
				mimeTypes : 'image/gif,image/jpg,image/jpeg,image/bmp,image/png'
			},
			// swf文件路径
			swf: ctx + '/assets/js/plugins/webuploader/Uploader.swf',
			
			disableGlobalDnd: true,
			chunked: true,
			chunkSize: 5 * 1024 * 1024, //每片5M
			threads:'2',        //同时运行5个线程传输
			server: ctx+'/attach/large/file/upload/doUpload',
			fileNumLimit: 1,
			fileSizeLimit: 5 * 1024 * 1024 * 1024,    // 5G
			fileSingleSizeLimit: 5 * 1024 * 1024 * 1024    // 5G
		});
	} else if(fileType == 'w'){
		uploader = WebUploader.create({
			pick: {
				id: '#filePicker',
				label: '点击选择文件'
			},
			dnd: '#uploader .queueList',
			paste: document.body,
			accept : {
				title : 'Doc',
				extensions : 'doc,docx',
				mimeTypes : 'application/msword,application/vnd.openxmlformats-officedocument.wordprocessingml.document'
			},
			// swf文件路径
			swf: ctx + '/assets/js/plugins/webuploader/Uploader.swf',
			
			disableGlobalDnd: true,
			chunked: true,
			chunkSize: 5 * 1024 * 1024, //每片5M
			threads:'2',        //同时运行5个线程传输
			server: ctx+'/attach/large/file/upload/doUpload',
			fileNumLimit: 1,
			fileSizeLimit: 5 * 1024 * 1024 * 1024,    // 5G
			fileSingleSizeLimit: 5 * 1024 * 1024 * 1024    // 5G
		});
	}else if(fileType == 'e'){
		uploader = WebUploader.create({
			pick: {
				id: '#filePicker',
				label: '点击选择文件'
			},
			dnd: '#uploader .queueList',
			paste: document.body,
			accept : {
				title : 'Excel',
				extensions : 'xls,xlsx',
				mimeTypes : 'application/vnd.ms-excel,application/vnd.openxmlformats-officedocument.spreadsheetml.sheet'
			},
			// swf文件路径
			swf: ctx + '/assets/js/plugins/webuploader/Uploader.swf',
			
			disableGlobalDnd: true,
			chunked: true,
			chunkSize: 5 * 1024 * 1024, //每片5M
			threads:'2',        //同时运行5个线程传输
			server: ctx+'/attach/large/file/upload/doUpload',
			fileNumLimit: 1,
			fileSizeLimit: 5 * 1024 * 1024 * 1024,    // 5G
			fileSingleSizeLimit: 5 * 1024 * 1024 * 1024    // 5G
		});
	}
	else if(fileType == 'pv'){
		uploader = WebUploader.create({
			pick: {
				id: '#filePicker',
				label: '点击选择文件'
			},
			dnd: '#uploader .queueList',
			paste: document.body,
			accept : {
				title : 'PV',
				extensions : 'ppt,pptx,mp4,ogg,webm',
				mimeTypes : 'application/powerpoint,application/vnd.openxmlformats-officedocument.presentationml.presentation,video/mp4,video/webm,video/ogg'
			},
			// swf文件路径
			swf: ctx + '/assets/js/plugins/webuploader/Uploader.swf',
			
			disableGlobalDnd: true,
			chunked: true,
			chunkSize: 5 * 1024 * 1024, //每片5M
			threads:'2',        //同时运行5个线程传输
			server: ctx+'/attach/large/file/upload/doUpload',
			fileNumLimit: 1,
			fileSizeLimit: 5 * 1024 * 1024 * 1024,    // 5G
			fileSingleSizeLimit: 5 * 1024 * 1024 * 1024    // 5G
		});
	}
	else{
		uploader = WebUploader.create({
			pick: {
				id: '#filePicker',
				label: '点击选择文件'
			},
			dnd: '#uploader .queueList',
			paste: document.body,
			// swf文件路径
			swf: ctx + '/assets/js/plugins/webuploader/Uploader.swf',
			
			disableGlobalDnd: true,
			chunked: true,
			chunkSize: 5 * 1024 * 1024, //每片5M
			threads:'2',        //同时运行5个线程传输
			server: ctx+'/attach/large/file/upload/doUpload',
			fileNumLimit: 1,
			fileSizeLimit: 5 * 1024 * 1024 * 1024,    // 5G
			fileSingleSizeLimit: 5 * 1024 * 1024 * 1024    // 5G
		});
	}
    

    // 添加“添加文件”的按钮，
//    uploader.addButton({
//        id: '#filePicker2',
//        label: '继续添加'
//    });

    // 当有文件添加进来时执行，负责view的创建
    function addFile( file ) {
        var $li = $( '<li id="' + file.id + '">' +
                '<p class="title">' + file.name + '</p>' +
                '<p class="imgWrap"></p>'+
                '<p class="progress"><span></span></p>' +
                '</li>' ),

            $btns = $('<div class="file-panel">' +
                '<span class="cancel">删除</span>' +
                '<span class="rotateRight">向右旋转</span>' +
                '<span class="rotateLeft">向左旋转</span></div>').appendTo( $li ),
            $prgress = $li.find('p.progress span'),
            $wrap = $li.find( 'p.imgWrap' ),
            $info = $('<p class="error"></p>'),

            showError = function( code ) {
                switch( code ) {
                    case 'exceed_size':
                        text = '文件大小超出';
                        break;

                    case 'interrupt':
                        text = '上传暂停';
                        break;

                    default:
                        text = '上传失败，请重试';
                        break;
                }

                $info.text( text ).appendTo( $li );
            };

        if ( file.getStatus() === 'invalid' ) {
            showError( file.statusText );
        } else {
            // @todo lazyload
            $wrap.text( '预览中' );
            uploader.makeThumb( file, function( error, src ) {
                if ( error ) {
                    $wrap.text( '非图片不能预览' );
                    return;
                }

                var img = $('<img src="'+src+'">');
                $wrap.empty().append( img );
            }, thumbnailWidth, thumbnailHeight );

            percentages[ file.id ] = [ file.size, 0 ];
            file.rotation = 0;
        }

        file.on('statuschange', function( cur, prev ) {
            if ( prev === 'progress' ) {
                $prgress.hide().width(0);
            } else if ( prev === 'queued' ) {
                $li.off( 'mouseenter mouseleave' );
                $btns.remove();
            }

            // 成功
            if ( cur === 'error' || cur === 'invalid' ) {
                //console.log( file.statusText );
                showError( file.statusText );
                percentages[ file.id ][ 1 ] = 1;
            } else if ( cur === 'interrupt' ) {
                showError( 'interrupt' );
            } else if ( cur === 'queued' ) {
                percentages[ file.id ][ 1 ] = 0;
            } else if ( cur === 'progress' ) {
                $info.remove();
                $prgress.css('display', 'block');
            } else if ( cur === 'complete' ) {
                $li.append( '<span class="success"></span>' );
            }

            $li.removeClass( 'state-' + prev ).addClass( 'state-' + cur );
        });

        $li.on( 'mouseenter', function() {
            $btns.stop().animate({height: 30});
        });

        $li.on( 'mouseleave', function() {
            $btns.stop().animate({height: 0});
        });

        $btns.on( 'click', 'span', function() {
            var index = $(this).index(),
                deg;

            switch ( index ) {
                case 0:
                    uploader.removeFile( file );
                    return;

                case 1:
                    file.rotation += 90;
                    break;

                case 2:
                    file.rotation -= 90;
                    break;
            }

            if ( supportTransition ) {
                deg = 'rotate(' + file.rotation + 'deg)';
                $wrap.css({
                    '-webkit-transform': deg,
                    '-mos-transform': deg,
                    '-o-transform': deg,
                    'transform': deg
                });
            } else {
                $wrap.css( 'filter', 'progid:DXImageTransform.Microsoft.BasicImage(rotation='+ (~~((file.rotation/90)%4 + 4)%4) +')');
                // use jquery animate to rotation
                // $({
                //     rotation: rotation
                // }).animate({
                //     rotation: file.rotation
                // }, {
                //     easing: 'linear',
                //     step: function( now ) {
                //         now = now * Math.PI / 180;

                //         var cos = Math.cos( now ),
                //             sin = Math.sin( now );

                //         $wrap.css( 'filter', "progid:DXImageTransform.Microsoft.Matrix(M11=" + cos + ",M12=" + (-sin) + ",M21=" + sin + ",M22=" + cos + ",SizingMethod='auto expand')");
                //     }
                // });
            }


        });

        $li.appendTo( $queue );
    }

    // 负责view的销毁
    function removeFile( file ) {
        var $li = $('#'+file.id);

        delete percentages[ file.id ];
        updateTotalProgress();
        $li.off().find('.file-panel').off().end().remove();
    }

    function updateTotalProgress() {
        var loaded = 0,
            total = 0,
            spans = $progress.children(),
            percent;

        $.each( percentages, function( k, v ) {
            total += v[ 0 ];
            loaded += v[ 0 ] * v[ 1 ];
        } );

        percent = total ? loaded / total : 0;

        spans.eq( 0 ).text( Math.round( percent * 100 ) + '%' );
        spans.eq( 1 ).css( 'width', Math.round( percent * 100 ) + '%' );
        updateStatus();
    }

    function updateStatus() {
        var text = '', stats;

        if ( state === 'ready' ) {
            text = '选中' + fileCount + '个文件，共' +
                    WebUploader.formatSize( fileSize ) + '。';
        } else if ( state === 'confirm' ) {
            stats = uploader.getStats();
            if ( stats.uploadFailNum ) {
                text = '已成功上传' + stats.successNum+ '个文件，'+
                    stats.uploadFailNum + '个文件上传失败，<a class="retry" href="#">重新上传</a>失败文件或<a class="ignore" href="#">忽略</a>'
            }

        } else {
            stats = uploader.getStats();
            text = '共' + fileCount + '个（' +
                    WebUploader.formatSize( fileSize )  +
                    '），已上传' + stats.successNum + '个';

            if ( stats.uploadFailNum ) {
                text += '，失败' + stats.uploadFailNum + '个';
            }
        }

        $info.html( text );
    }

    function setState( val ) {
        var file, stats;

        if ( val === state ) {
            return;
        }

        $upload.removeClass( 'state-' + state );
        $upload.addClass( 'state-' + val );
        state = val;

        switch ( state ) {
            case 'pedding':
                $placeHolder.removeClass( 'element-invisible' );
                $queue.parent().removeClass('filled');
                $queue.hide();
                $statusBar.addClass( 'element-invisible' );
                uploader.refresh();
                break;

            case 'ready':
                $placeHolder.addClass( 'element-invisible' );
                $( '#filePicker2' ).removeClass( 'element-invisible');
                $queue.parent().addClass('filled');
                $queue.show();
                $statusBar.removeClass('element-invisible');
                uploader.refresh();
                break;

            case 'uploading':
                $( '#filePicker2' ).addClass( 'element-invisible' );
                $progress.show();
                $upload.text( '暂停上传' );
                break;

            case 'paused':
                $progress.show();
                $upload.text( '继续上传' );
                break;

            case 'confirm':
                $progress.hide();
                $upload.text( '开始上传' ).addClass( 'disabled' );

                stats = uploader.getStats();
                if ( stats.successNum && !stats.uploadFailNum ) {
                    setState( 'finish' );
                    return;
                }
                break;
            case 'finish':
            	//console.log();
                stats = uploader.getStats();
                //console.log(stats);
                if ( stats.successNum ) {
                    //alert( '上传成功' );
                	
                	
                } else {
                    // 没有成功的图片，重设
                    state = 'done';
                    location.reload();
                }
                break;
        }

        updateStatus();
    }

    uploader.onUploadProgress = function( file, percentage ) {
        var $li = $('#'+file.id),
            $percent = $li.find('.progress span');

        $percent.css( 'width', percentage * 100 + '%' );
        percentages[ file.id ][ 1 ] = percentage;
        updateTotalProgress();
    };

    uploader.onFileQueued = function( file ) {
        fileCount++;
        fileSize += file.size;

        if ( fileCount === 1 ) {
            $placeHolder.addClass( 'element-invisible' );
            $statusBar.show();
        }

        addFile( file );
        setState( 'ready' );
        updateTotalProgress();
    };

    uploader.onFileDequeued = function( file ) {
        fileCount--;
        fileSize -= file.size;
        if ( !fileCount ) {
            setState( 'pedding' );
        }
        removeFile( file );
        updateTotalProgress();

    };

    uploader.on( 'all', function( type ) {
        var stats;
        switch( type ) {
            case 'uploadFinished':
                setState( 'confirm' );
                break;

            case 'startUpload':
                setState( 'uploading' );
                break;

            case 'stopUpload':
                setState( 'paused' );
                break;

        }
    });
    /*获取服务器返回的数据*/
    uploader.on('uploadAccept', function(file, result ) {
    	if(result.state=="ok"){
    		parent.uploadFileComplete(attachField, attachDivId,
				result.attachId,result.fileName,result.fileSuffix,1);
    		//关闭弹窗
    		$("largeFileUploadModal").modal('hide');  //手动关闭
    	}
    });
    
    uploader.onError = function( code ) {
    	if(code == 'Q_EXCEED_NUM_LIMIT'){
    		alert("超出可上传文件数！")
    	}
    };

    $upload.on('click', function() {
        if ( $(this).hasClass( 'disabled' ) ) {
            return false;
        }

        if ( state === 'ready' ) {
            uploader.upload();
        } else if ( state === 'paused' ) {
            uploader.upload();
        } else if ( state === 'uploading' ) {
            uploader.stop();
        }
    });

    $info.on( 'click', '.retry', function() {
        uploader.retry();
    } );

    $info.on( 'click', '.ignore', function() {
        alert( 'todo' );
    } );

    $upload.addClass( 'state-' + state );
    updateTotalProgress();
});