<#if warnings?? && warnings?size gt 0>
<div class="alert alert-warning alert-dismissible" role="alert">
	<button type="button" class="close" data-dismiss="alert" aria-label="Close">
		<span aria-hidden="true">×</span>
	</button>
	<ul>
		<#list warnings as warning>
		<li>${warning!''}</li>
		</#list>
	</ul>
</div>
</#if>