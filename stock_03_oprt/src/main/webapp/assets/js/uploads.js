(function (global, factory) {
  if (typeof define === "function" && define.amd) {
    define('/forms/uploads', ['jquery', 'Site'], factory);
  } else if (typeof exports !== "undefined") {
    factory(require('jquery'), require('Site'));
  } else {
    var mod = {
      exports: {}
    };
    factory(global.jQuery, global.Site);
    global.formsUploads = mod.exports;
  }
})(this, function (_jquery, _Site) {
  'use strict';

  var _jquery2 = babelHelpers.interopRequireDefault(_jquery);

  (0, _jquery2.default)(document).ready(function ($$$1) {
    (0, _Site.run)();
  });
  
/*(0, _jquery2.default)('#input-file-now').fileupload({
        dataType: 'json',
        add: function (e, data) {
        	alert("hhhhh")
            data.context = $('<button/>').text('Upload')
                .appendTo(document.body)
                .click(function () {
                    $(this).replaceWith($('<p/>').text('Uploading...'));
                    data.submit();
                });
        },
        done: function (e, data) {
            data.context.text('Upload finished.');
        }
  });*/

  // Example File Upload
  // -------------------
/*  (0, _jquery2.default)('#exampleUploadForm').fileupload({
    url: ctx + '/attach/upload/doFileUpload',
    dropzone: (0, _jquery2.default)('#exampleUploadForm'),
    filesContainer: (0, _jquery2.default)('.file-list'),
    uploadTemplateId: false,
    downloadTemplateId: false,
    uploadTemplate: tmpl('{% for (var i=0, file; file=o.files[i]; i++) { %}' + '<div class="file-item-wrap template-upload fade col-lg-2 col-md-4 col-sm-6 {%=file.type.search("image") !== -1? "image" : "other-file"%}">' + '<div class="file-item">' + '<div class="preview vertical-align">' + '<div class="file-action-wrap">' + '<div class="file-action">' + '{% if (!i && !o.options.autoUpload) { %}' + '<i class="icon wb-upload start" data-toggle="tooltip" data-original-title="Upload file" aria-hidden="true"></i>' + '{% } %}' + '{% if (!i) { %}' + '<i class="icon wb-close cancel" data-toggle="tooltip" data-original-title="Stop upload file" aria-hidden="true"></i>' + '{% } %}' + '</div>' + '</div>' + '</div>' + '<div class="info-wrap">' + '<div class="title">{%=file.name%}</div>' + '</div>' + '<div class="progress progress-striped active" aria-valuemin="0" aria-valuemax="100" aria-valuenow="0" role="progressbar">' + '<div class="progress-bar progress-bar-success" style="width:0%;"></div>' + '</div>' + '</div>' + '</div>' + '{% } %}'),
    downloadTemplate: tmpl('{% for (var i=0, file; file=o.files[i]; i++) { %}' + '<div class="file-item-wrap template-download fade col-lg-2 col-md-4 col-sm-6 {%=file.type.search("image") !== -1? "image" : "other-file"%}">' + '<div class="file-item">' + '<div class="preview vertical-align">' + '<div class="file-action-wrap">' + '<div class="file-action">' + '<i class="icon wb-trash delete" data-toggle="tooltip" data-original-title="Delete files" aria-hidden="true"></i>' + '</div>' + '</div>' + '<img src="{%=file.url%}"/>' + '</div>' + '<div class="info-wrap">' + '<div class="title">{%=file.name%}</div>' + '</div>' + '</div>' + '</div>' + '{% } %}'),
    forceResize: true,
    previewCanvas: false,
    previewMaxWidth: false,
    previewMaxHeight: false,
    previewThumbnail: false
  }).on('fileuploadprocessalways', function (e, data) {
	  console.log(data)
	  console.log(data.loaded)
	  var progress = parseInt(data.loaded / data.total * 100, 10);  
      $("#progressBar").css('width',progress + '%');  
      $("#progressBar").html(progress + '%');
    var length = data.files.length;

    for (var i = 0; i < length; i++) {
      if (!data.files[i].type.match(/^image\/(gif|jpeg|png|svg\+xml)$/)) {
        data.files[i].filetype = 'other-file';
      } else {
        data.files[i].filetype = 'image';
      }
    }
  }).on('fileuploadadded', function (e) {
    var $this = (0, _jquery2.default)(e.target);
    $('#phf').prepend("<div class='dropify-preview' style='display: block;' >" +
		"	<span class='dropify-render'>" +
		"		<i class='dropify-font-file'></i>" +
		"		<span class='dropify-extension'>doc</span>"+
		"	</span>" +
		"	<div class='dropify-infos'>" +
		"		<div class='dropify-infos-inner'>" +
		"			<p class='dropify-filename'>" +
		"				<span class='file-icon'></span>" +
		"				<span class='dropify-filename-inner'></span>" +
		"			</p>" +
		"			<p class='dropify-infos-message'>" +
		"				Drag and drop or click to replace" +
		"			</p>" +
		"		</div>" +
		"	</div>" +
		"</div>");
    if ($this.find('.file-item-wrap').length > 0) {
      $this.addClass('has-file');
    } else {
      $this.removeClass('has-file');
    }
  }).on('fileuploadfinished', function (e) {
    var $this = (0, _jquery2.default)(e.target);

    if ($this.find('.file-item-wrap').length > 0) {
      $this.addClass('has-file');
      alert("1");
    } else {
      $this.removeClass('has-file');
      alert("2");
    }
  }).on('fileuploaddestroyed', function (e) {
    var $this = (0, _jquery2.default)(e.target);

    if ($this.find('.file-item-wrap').length > 0) {
      $this.addClass('has-file');
    } else {
      $this.removeClass('has-file');
    }
  }).on('click', function (e) {
    if ((0, _jquery2.default)(e.target).parents('.file-item-wrap').length === 0) (0, _jquery2.default)('#inputUpload').trigger('click');
  });

  (0, _jquery2.default)(document).bind('dragover', function (e) {
    var dropZone = (0, _jquery2.default)('#exampleUploadForm'),
        timeout = window.dropZoneTimeout;
    if (!timeout) {
      dropZone.addClass('in');
    } else {
      clearTimeout(timeout);
    }
    var found = false,
        node = e.target;
    do {
      if (node === dropZone[0]) {
        found = true;
        break;
      }
      node = node.parentNode;
    } while (node !== null);
    if (found) {
      dropZone.addClass('hover');
    } else {
      dropZone.removeClass('hover');
    }
    window.dropZoneTimeout = setTimeout(function () {
      window.dropZoneTimeout = null;
      dropZone.removeClass('in hover');
    }, 100);
  });

  (0, _jquery2.default)('#inputUpload').on('click', function (e) {
    e.stopPropagation();
  });
  
	(0, _jquery2.default)('#btnUpload').on('click', function (e) {
		alert("close")
		console.log(document.getElementById("inputUpload").files[0]);
		$.ajax({
			type: "post",
			url: ctx + '/attach/upload/doFileUpload',
			dataType: "json",
			success: function(result){
				if(result.state=="ok"){
					alert("succ")
				}else{
					resultErr(result);
				}
			}
		});
		e.stopPropagation();
	});

  (0, _jquery2.default)('#uploadlink').on('click', function (e) {
    e.stopPropagation();
  });*/
});