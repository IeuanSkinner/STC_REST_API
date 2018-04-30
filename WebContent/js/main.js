$(document).ready(function() {
	$.ajax({
		url: window.location + 'rest/users/',
		success: function(responseText) {
			console.log("Response Text", responseText);
		}
	});
	
	$('form').submit(function() {
		var me = this;
		
		$.ajax({
			url: window.location + 'rest/users/',
			type: 'post',
			dataType: 'text',
			contentType: 'application/x-www-form-urlencoded',
			data: {
				firstname: me.firstname.value,
				lastname: me.lastname.value,
				age: me.age.value,
				location: me.location.value,
				email: me.email.value
			},
			success: function(response) {
				alert("Successfully submitted: " + me.firstname.value + ", " + me.lastname.value +  "!");
				
				console.log(response);
				
				// Clear the input fields
				me.firstname.value = '';
				me.lastname.value = '';
				me.age.value = '';
				me.location.value = '';
				me.email.value = '';
			}
		});
		return false;
	});
});