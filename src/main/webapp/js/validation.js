
function ToastrValidator() {
	
    this.checkFieldEmpty = function (field){
        if(field.val() == ""){
            field.focus();
            return false;
        }
        return true;
    },

    this.validateLoginForm = function () {
        var self = this;
        if(!self.checkFieldEmpty($("#logusername"))){
            toastr.error("Username is required.");
            return false;
        }
        if(!self.checkFieldEmpty($("#logpassword"))){
            toastr.error("Password is required.");
            return false;
        }
        return true;
    }
}