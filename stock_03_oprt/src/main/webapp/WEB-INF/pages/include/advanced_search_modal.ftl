<#macro advancedSearchModal  modal_size="">
<#compress>
<div class="modal" id="advSearchModal"  role="dialog" aria-hidden="true">
    <div class="modal-dialog ${modal_size!}">
        <div class="modal-content">
            <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal" aria-label="Close">
                    <span aria-hidden="true">&times;</span>
                </button>
                <h4 class="modal-title">高级查询</h4>
            </div>
            <div class="modal-body">
	            <form class="form-horizontal" id="advSearchForm">
	                <#nested>
	            </form>
            </div>
            <script type="text/javascript">
                function resetAdvSearchForm(){
                	//清空select2的选择
                	$("#srv_commissioner").val("").trigger("change");
                    document.getElementById("advSearchForm").reset(); 
                }
            </script>
            <div class="modal-footer">
                <button type="button" class="btn btn-white btn-sm" onclick="resetAdvSearchForm()">重 置</button>
                <button type="button" class="btn btn-primary btn-sm" id="doAdvSearch"><i class="fa fa-search"></i> 查 询</button>
        	</div>
        </div>
    </div>
</div>
</#compress>
</#macro>