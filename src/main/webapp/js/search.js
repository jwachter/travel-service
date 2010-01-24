// Add segment button functionality
$(document).ready(
		function() {
			$("#addSegment").click(
					function() {
						var orig = $("#originSequence");
						var dest = $("#destinationSequence");
						var dept = $("#departureSequence");

						var newOrig = $("#multiFrom").val();
						var newDest = $("#multiTo").val();
						var newDept = $("#multiDeparture").val();

						var _orig = orig.val().split(";");
						var _dest = dest.val().split(";");
						var _dept = dept.val().split(";");

						if (_orig.length == 1 && _orig[0] == "") {
							_orig = new Array(0)
						}
						if (_dest.length == 1 && _dest[0] == "") {
							_dest = new Array(0)
						}
						if (_dept.length == 1 && _dept[0] == "") {
							_dept = new Array(0)
						}

						if (newOrig != "" && newDest != "" && newDept != "") {
							_orig.push(newOrig);
							_dest.push(newDest);
							_dept.push(newDept);
						} else {
							alert("At least one field is missing!");
						}

						orig.val(_orig.join(";"));
						dest.val(_dest.join(";"));
						dept.val(_dept.join(";"));

						$("#currentSegmentsTable").append(
								"<tr><td>" + newOrig + "</td><td>" + newDest + "</td><td>"
										+ newDept + "</td></tr>");

						$("#multiFrom").val(newDest);
						$("#multiTo").val("");
						$("#multiDeparture").val(newDept);
						
						// Prevent from submit
						return false;
					});
		});
// Hide the roundtrip returnDate
$(document).ready(function() {
	$('#returnDateContainer').hide();
});

// React on mode change
$(document).ready(function() {
	$('#simplesearch #type').change(function() {
		var selection = $('#simplesearch #type option:selected').val();

		if (selection == "roundtrip") {
			$('#returnDateContainer').show();
		} else {
			$('#returnDateContainer').hide();
		}
	});
});

$(document).ready(function(){
	$('#multisubmit').click(function(){
		var orig = $("#originSequence").val();
		var dest = $("#destinationSequence").val();
		var dept = $("#departureSequence").val();
		window.location.href = window.location.href + 'search.html?type=multisegment&originSequence='+orig+'&destinationSequence='+dest+'&departureSequence='+dept;
		return false;
	});
});
