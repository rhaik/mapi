				<div id="pullUp">
					<span class="pullUpIcon"></span><span class="pullUpLabel">上拉加载更多...</span>
				</div>
				
			</div>
		</div>
	</div>
</div>
<#if fromSafari?? && !fromWeixin></div></#if>
<script type="text/javascript" src="${util.jquerymobile}"></script>
<script type="text/javascript"  src="${util.iscroll}"></script>
<script type="text/javascript" charset="utf-8" src="${util.static}js/initScroll.js"></script>
<script type="text/javascript">
$(function(){
 	$('ul').removeClass("ui-listview");
 	$('ul>li').removeClass("ui-li ui-li-static ui-btn-up-c");
 });
</script>
</body>
</html>