// double check to accept URL on UI
function validateForm() {
    var lnk = document.forms["newLink"]["URL"].value;
    // null post isn't allowed
    if (lnk == "") {
        alert("An URL must be filled out");
        return false;
    }
    // accept http:// and https:// formats
    if( !/(http(s?)):\/\//gi.test(lnk) ) {
        alert("Insert a valid http:// or https:// URL");
        return false;
    }
}
