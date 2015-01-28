$(function() {
	$('.sidebar-link').click(
		function() {
			var url=$(this).attr('href');
			$('#content').load(url);
			return false;
		});
});