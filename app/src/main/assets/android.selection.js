
/**
 *	Handles the long touch action by selecting the last touched element.
 */
longTouchSelected = function() {
            if(window.getSelection) {
        		 window.TextSelection.seletedWord(window.getSelection().toString());
            } else if(document.selection && document.selection.createRange) {
           		  window.TextSelection.seletedWord(document.selection.createRange().text());
            }
};
