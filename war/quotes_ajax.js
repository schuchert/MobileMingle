$(function() {
	$('.error').hide();
	$(".button").click(function() {
		$('.error').hide();
		var symbols = $("input#symbols").val();
		if (symbols == "") {
			$("label#symbols_error").show();
			$("input#symbols").focus();
			return false;
		}
		$("#grid").GridUnload();
        $("input#symbols").val('');
		var dataString = '?q=' + symbols;
        var theUrl = 'jsonstockdata' + dataString;
		jQuery("#grid").jqGrid({
		      url: theUrl,
		      datatype: "json",
		      mtype: 'GET',
		      colNames:['symbol', 'price', 'change'],
		      colModel:[
		                {name:'symbol', width: 110, sortable:true, editable:false},
		                {name:'price', width:250,editable:false, sortable: true},
		                {name:'change', width:250,editable:false, sortable: true},
		           ],
		     jsonReader : {
		          repeatitems: false,
                  root: "rows",
                  page: "page",
                  total: "total",
                  id: "id",
		     },
		    rowNum:10,
		    altRows     : true,
		    altclass    : 'oddRow',
		    gridComplete: function() {
				$(".jqgrow:odd").hover(function() {
					$(this).removeClass("oddRow");
				}, function(event) {
					$(this).addClass("oddRow");
				});
			},
			pager : jQuery('#gridpager'),
			sortname : 'Symbol',
			viewrecords : true,
			sortorder : "asc",
			caption : "Quotes",
			editurl : theUrl
		}).navGrid('#gridpager');
		return false;
	});

});