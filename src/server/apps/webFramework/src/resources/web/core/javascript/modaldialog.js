// JavaScript Document
/* these functions are necessary because IE improperly puts select components in front of everything else in the window.
   Therefore, when we bring up the modal dialog, we also need to hide any select components*/
function hideSelects(){
    selects = document.getElementsByTagName("select");
	for( i=0; i < selects.length; i++)
	{
		selects[i].style.visibility = 'hidden';
	}
}

function showSelects(){
selects = document.getElementsByTagName("select");
	for( i=0; i < selects.length; i++)
	{
		selects[i].style.visibility = 'visible';
	}
}

/*shortcut for hiding and showing modal dialogs*/

function showModalDialog(curtainId, dialogId) {
	hideSelects();
	document.getElementById(curtainId).style.display = "block";
	document.getElementById(dialogId).style.display = "block";
}

function hideModalDialog(curtainId, dialogId) {
	document.getElementById(curtainId).style.display = "none";
	document.getElementById(dialogId).style.display = "none";
	showSelects();
}