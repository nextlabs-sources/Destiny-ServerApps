// JavaScript Document

/*Note: this Javascript is for demonstration purposes only.  More work may be necessary for the final product */

function resetSelectorState(msgid, selectorid) {
  msgElement= document.getElementById(msgid);
  selectorElement= document.getElementById(selectorid);
  selects= selectorElement.getElementsByTagName("select");
  if(selects.length > 0 && selects[0].length>0) {
    msgElement.style.display='none';
	selectorElement.style.display='block';
  } else {
	msgElement.style.display='block';
	selectorElement.style.display='none';  
  }
}

function moveSelected(fromId, toId) {
  from = document.getElementById(fromId);
  to = document.getElementById(toId);
  
  oldOpt = from.options[from.selectedIndex];
  newOpt = document.createElement('option');
  newOpt.text = oldOpt.text;
  newOpt.value= oldOpt.value;
  
  oldOpt.disabled = true;
  oldOpt.selected = false;
  
  try {
    to.add(newOpt, null); // standards compliant; doesn't work in IE
  }
  catch(ex) {
    to.add(newOpt); // IE only
  } 
}

function deleteSelected(fromId, originId) {
  from = document.getElementById(fromId);
  origin = document.getElementById(originId);
  
  for(i=0; i<origin.options.length ;i++) {
    if(origin.options[i].text == from.options[from.selectedIndex].text){
		origin.options[i].disabled=false;
	}
  }
  
  from.remove(from.selectedIndex);
}