// JavaScript Document

function changeEnabledState(containerId, enabled) {

    container = document.getElementById(containerId);
	
	inputs = container.getElementsByTagName("input");

	for(var i=0; i<inputs.length; i++){
		if(enabled){
		  inputs[i].removeAttribute("disabled");
		}else{
  	      inputs[i].setAttribute("disabled", "true");
		}
	}
	
	selects = container.getElementsByTagName("select");
	for(var i=0; i<selects.length; i++){
		if(enabled){
		  selects[i].removeAttribute("disabled");
		}else{
  	      selects[i].setAttribute("disabled", "true");
		}
	}
}


function changeEnabledClass(containerId, enabled){
  var myElem = document.getElementById(containerId);
  if(enabled){
	  myElem.className = 'enabled';
  } else {
	  myElem.className = 'disabled';
  }
}

function resetCustom(containerId, enabled) {
	this.form.reset();
	changeEnabledState(containerId, enabled);
	changeEnabledClass(containerId, enabled);
}

function disablehref(){
	var allLinks = document.getElementsByTagName("a");
	var linkCount = allLinks.length;
	for(var i = 0; i < linkCount; i++){
		allLinks[i].setAttribute("onclickbackup", allLinks[i].getAttribute("onclick"));
		if (navigator.appName == "Microsoft Internet Explorer") {// IE
			allLinks[i].onclick = killEvent;
		}
						
		if (navigator.appName == "Netscape") {// Firefox
			allLinks[i].removeAttribute("onclick");
		}
	}
	return true;
}

function enableparenthref(){
	var allParentLinks = this.window.opener.document.getElementsByTagName("a");
	var linkCount = allParentLinks.length;
	for(var i = 0; i < linkCount; i++){
		if (navigator.appName == "Microsoft Internet Explorer") {// IE
			allParentLinks[i].onclick = allParentLinks[i].onclickbackup;
		}
		
		if (navigator.appName == "Netscape") {// Firefox
			allParentLinks[i].setAttribute("onclick", allParentLinks[i].getAttribute("onclickbackup"));
		}
		allParentLinks[i].removeAttribute("onclickbackup");
	}
	return true;
}

function enablehref(){
	var allParentLinks = document.getElementsByTagName("a");
	var linkCount = allParentLinks.length;
	for(var i = 0; i < linkCount; i++){
		if (navigator.appName == "Microsoft Internet Explorer") {// IE
			allParentLinks[i].onclick = allParentLinks[i].onclickbackup;
		}
		
		if (navigator.appName == "Netscape") {// Firefox
			allParentLinks[i].setAttribute("onclick", allParentLinks[i].getAttribute("onclickbackup"));
		}
		allParentLinks[i].removeAttribute("onclickbackup");
	}
	return true;
}

function killEvent(){
 	return false;
}
