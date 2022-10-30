
var userLookupData = null;
var policyLookupData = null;
var selectedUsers = [];
var tempSelectedUsers = [];
var selectedResources = [];
var tempSelectedResources = [];
var selectedPolicies = [];
var tempSelectedPolicies = [];
var lookupURL = null;
var userTable = null;
var policyTable = null;

function initializeLookupComponents(URL, errorHandler)
{
	lookupURL = URL;
}

function usersSelected() {
	var selUsers = "";
	selectedUsers = [];

	$.each(tempSelectedUsers, function (i, val) {
		selUsers += val + ", ";
		selectedUsers.push(val);
	});

	selUsers = selUsers.substr(0, selUsers.length - 2);
	tempSelectedUsers = []; // reset temp holder
	$('#rptDefCntInUsrTxt').val(selUsers);
	$("#lookup_users-popup").dialog("close");

	// clear data
	//selectedUsers = [];
}

function usersCanceled() {
	tempSelectedUsers = selectedUsers.slice();

	$("#user_selected_count").html("Total selected users :" + tempSelectedUsers.length);

   	$("#lookup_users-popup").dialog("close");
}


function policiesSelected() {
	var selPolicies = "";
	selectedPolicies = [];

	$.each( tempSelectedPolicies, function( i, val ) {
		var beginIndex = val.lastIndexOf("/");
		var endIndex = val.length;
		var val1 = val.substring(beginIndex + 1, endIndex);
		selPolicies += val1 + ", ";
		selectedPolicies.push(val);
	});

	selPolicies = selPolicies.substr(0, selPolicies.length - 2);
	tempSelectedPolicies = [];  // reset temp holder
	$('#rptDefCntInPlcTxt').val(selPolicies);
	$("#lookup_policy-popup").dialog("close");

	// clear data
	//selectedPolicies = [];
}

handleAjaxError = function (xhr) {
    if (xhr.getResponseHeader('REQUIRE_AUTH')) {
		window.location = "/cas/login?service=" + window.location.origin + ":443" + window.location.pathname;
    }
}

function policiesCanceled() {
	tempSelectedPolicies = selectedPolicies.slice();
	$("#user_selected_count").html("Total selected users :" + tempSelectedPolicies.length);

    $("#lookup_policy-popup").dialog("close");
 }

function lookupUsers(userData, selectedUsers) {

	tempSelectedUsers = selectedUsers.slice();

	userTable = $('#lookup_usersTable').dataTable({
		initComplete : function() {
			var self = this.api();
			var search_input = $('#lookup_users-popup .dataTables_filter input').unbind();
			var search_button = $('<button type="button">Search</button>').click(function() {
				self.search(search_input.val()).draw();
			});
			var clear_button = $('<button type="button">Clear</button>').click(function() {
				search_input.val('');
				search_button.click();
			});

			$(search_input).keypress(function (event) {
				if (event.which == 13) {
					search_button.click();
					event.preventDefault();
				}
			});
			$('#lookup_users-popup .dataTables_filter').append(search_button, clear_button);
		},
		"pagingType" : "simple_numbers",
		"sScrollY" : $("#lookup_users-popup").height()-250 + 'px',   //reserve 250px for buttons OK, Cancel
		//"bAutoWidth" : true,
		"bJQueryUI" : true,
		"bProcessing" : true,
		"bServerSide": true,
		"bDestroy" : true,
		"iDisplayLength" : 50,
		"iDisplayStart": 0,
		"ajax": {
			url: lookupURL,
			type : 'POST',
			dataType : 'json',
			data : {
				"action" : "USER_LOOKUP_PAGINATED"
			},
			error: handleAjaxError
		},
		searchDelay: 1000,
		"aoColumns": [{
			"sTitle": "User Name", "mData":"displayName"
		}],
		"columnDefs": [ {
			"targets": 0,
			"render": function ( data, type, user, meta ) {
				var displayName = user[0];
				var firstName = user[1];
				var lastName = user[2];
				return createUserCheckBoxWLabel(firstName + ' ' + lastName + ' (' + displayName + ')', displayName, tempSelectedUsers);
			}
		} ]

	});

}


function fetchDataForLookupTable(action, postData, errHandler) {
	var responseData;
	var errorHandler = errHandler || function() {window.location.href =  window.location.href;};
	$.ajax({
		type : 'POST',
		dataType : 'json',
		url : lookupURL,
		data : {
			"data" : JSON.stringify(postData),
			"action" : action
		},
		success : function(data) {
			responseData = data;
		},
		error : function(xhr, status) {
			//alert("error fetching lookup");
			errorHandler();
		},
		async:false
	});

	return responseData;
}


function createUserCheckBoxWLabel(dataDisplay, dataValue, selectedValues) {
	dataValue = escapeHTML(dataValue);
	var inputElement = '<input type="checkbox" class="usr_lookup_sel" name="usr_lookup_sel_cbox" value="' + dataValue + '" onclick="selectedUserValues(this)"  /> &nbsp;' + dataDisplay;
	 $.each(selectedValues, function( i, val ) {
		if(val.toUpperCase() == dataValue.toUpperCase()) {
			inputElement = '<input type="checkbox" class="usr_lookup_sel" name="usr_lookup_sel_cbox" value="' + dataValue + '" onclick="selectedUserValues(this)"  checked /> &nbsp;' + dataDisplay;
		}
	});

	return inputElement;
}

function populateUserLookupData() {
	var selUsers = selectedUsers;

	lookupUsers(userLookupData, selectedUsers);
    $("#user_selected_count").html("Total selected users :" + selectedUsers.length);
}

function populatePolicyLookupData() {
	var selPolicies =  selectedPolicies;

	lookupPolicies(policyLookupData, selectedPolicies);
	$("#policy_selected_count").html("Total selected policies :" + selectedPolicies.length);
}


function lookupUsersPopup() {
	//show lookup dialog on button click
	var popup = $("#lookup_users-popup").dialog({
		height : 'auto',
		width : '450px',
		modal : true,
		resizable: false,
		draggable: false,
		resize : function() {
			populateUserLookupData();
			userTable.fnAdjustColumnSizing();
			userTable.fnDraw();
		},
		close : function() {
			usersCanceled();
		}
	});
	$("#lookup_users-popup").css("height","550px");
	populateUserLookupData();
	popup.dialog('option', 'title', 'User Lookup');
	popup.dialog('option', 'position', 'center');
}


function selectedUserValues(event) {
		var selValue = event.value;

		if (event.checked == 1){
          if($.inArray(selValue, tempSelectedUsers) < 0) {
        	  tempSelectedUsers.push(selValue);
			}
		} else {
			var index = $.inArray(selValue, tempSelectedUsers)
			tempSelectedUsers.splice(index, 1);
		}

		$("#user_selected_count").html("Total selected users :" + tempSelectedUsers.length);
}

function selectedPolicyValues(event) {
	var selValue = event.value;

	if (event.checked == 1){
          if($.inArray(selValue, tempSelectedPolicies) < 0) {
        	  tempSelectedPolicies.push(selValue);
			}
		} else {
			var index = $.inArray(selValue, tempSelectedPolicies)
			tempSelectedPolicies.splice(index, 1);
		}

	$("#policy_selected_count").html("Total selected policies :" + tempSelectedPolicies.length);
}

//populate policy lookup 
function setSelectedPolicies(valueArr) {
	selectedPolicies = [];
	for ( var prop in valueArr) { // enumerate its property names
		selectedPolicies.push(unescapeChar(valueArr[prop]));
	}
	lookupPolicies(policyLookupData, selectedPolicies);
}

//populate user lookup
function setSelectedUsers(valueArr) {
	selectedUsers = [];
	for ( var prop in valueArr) { // enumerate its property names
		selectedUsers.push(unescapeChar(valueArr[prop]));
	}
	lookupUsers(userLookupData, selectedUsers);

}

function clearLookupSelectedValues() {
	selectedUsers = [];
	selectedPolicies = [];

	lookupUsers(userLookupData, selectedUsers);
    $("#user_selected_count").html("Total selected users :" + selectedUsers.length);

    lookupPolicies(policyLookupData, selectedPolicies);
	$("#policy_selected_count").html("Total selected policies :" + selectedPolicies.length);

}

function getSelectedPolicies() {
	tempSelectedPolicies = [];
	return selectedPolicies;
}

function getSelectedUsers() {
	tempSelectedUsers = [];
	return selectedUsers;
}

function getSelectedResources() {
	tempSelectedResources = [];

	if(selectedResources.length == 0 && $("#rptDefCntInResTxt").val() != undefined) {
		selectedResources = $("#rptDefCntInResTxt").val().trim().split(",");
	}

	return selectedResources;
}

function lookupPolicies(policyData, selectedPolicies) {
	tempSelectedPolicies = selectedPolicies.slice();

	policyTable = $('#lookup_policyTable')
		.dataTable(
			{
				"initComplete" : function() {
					var self = this.api();
					var search_input = $('#lookup_policy-popup .dataTables_filter input').unbind();
					var search_button = $('<button type="button">Search</button>').click(function() {
						self.search(search_input.val()).draw();
					});
					var clear_button = $('<button type="button">Clear</button>').click(function() {
						search_input.val('');
						search_button.click();
					});

					$(search_input).keypress(function (event) {
						if (event.which == 13) {
							search_button.click();
							event.preventDefault();
						}
					});
					$('#lookup_policy-popup .dataTables_filter').append(search_button, clear_button);
				},
				"pagingType" : "simple_numbers",
				"sScrollY" :$("#lookup_policy-popup").height()-250 + 'px',   //reserve 250 px for OK and Cancel buttons
				//"bAutoWidth" : true,
				"bJQueryUI" : true,
				"bProcessing" : true,
				"bServerSide": true,
				"bDestroy" : true,
				"iDisplayLength" : 50,
				"iDisplayStart": 0,
				"ajax": {
					url: lookupURL,
					type : 'POST',
					dataType : 'json',
					data : {
						"action" : "POLICY_LOOKUP_PAGINATED"
					},
                    error: handleAjaxError
				},
				"aoColumns" : [ {
					"sTitle" : "Policy Name",
					"mData" : "name"
				} ],
				"columnDefs": [ {
					"targets": 0,
					"render": function ( data, type, full, meta ) {
						var displayName =  full[0];
						var policyFullName = full[1];
						return createPolicyCheckBoxWLabel(policyFullName,  displayName, tempSelectedPolicies);
					}
				} ]
			});
}

  function createPolicyCheckBoxWLabel(dataValue, dataDisplay, selectedValues) {
	    var storedDataValue = dataValue;
	    dataValue = escapeHTML(dataValue);
    	var inputElement = '<input type="checkbox" class="ply_lookup_sel" name="ply_lookup_sel_cbox" value="' + dataValue + '" onclick="selectedPolicyValues(this)" /> &nbsp;' + dataDisplay;
    	 $.each(selectedValues, function( i, val ) {
    		if(val.toUpperCase() == storedDataValue.toUpperCase()) {
    			inputElement = '<input type="checkbox" class="ply_lookup_sel" name="ply_lookup_sel_cbox" value="' + dataValue + '" onclick="selectedPolicyValues(this)" checked /> &nbsp;' + dataDisplay;
    		}
    	});

    	return inputElement;
    }


function lookupPoliciesPopup() {
	//show lookup dialog on button click
	var popup = $("#lookup_policy-popup").dialog({
		height : 'auto',
		width : '450px',
		modal : true,
		resizable: false,
		draggable: false,
		resize : function() {
			populatePolicyLookupData();
			policyTable.fnAdjustColumnSizing();
			policyTable.fnDraw();
		},
		close : function() {
			policiesCanceled();
		}
	});
	$("#lookup_policy-popup").css("height","500px");
	populatePolicyLookupData();
	popup.dialog('option', 'title', 'Policy Lookup');
	popup.dialog('option', 'position', 'center');
}
