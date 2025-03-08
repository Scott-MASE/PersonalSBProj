$(document).ready(function() {
	

	
	
	$('#create-note').off('click').on('click', function(e) {
	    e.preventDefault(); // Prevent default behavior
	    console.log("Opening modal...");
	    $('#createNoteModal').modal('show'); // Open Bootstrap modal
	});
	
	
});
