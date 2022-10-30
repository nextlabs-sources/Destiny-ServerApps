// JavaScript Document

function collapse(divId, arrowId) {
  document.getElementById(divId).style.display = 'none';
  document.getElementById(arrowId).className = 'closed';
}

function expand(divId, arrowId) {
  document.getElementById(divId).style.display = 'block';
  document.getElementById(arrowId).className = 'open';
}

function toggleDisplay(divId, arrowId) {
  if (document.getElementById(arrowId).className == 'open')
  {
	  collapse(divId, arrowId);
  }else{
	  expand(divId, arrowId);
  }
}