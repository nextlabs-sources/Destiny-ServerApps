/* global jqueryReady, policyPattern, zxcvbn */
/*eslint-disable no-unused-vars*/
function jqueryReady() {
    var policyPatternRegex = new RegExp(policyPattern);
    var password = document.getElementById('password');
    var confirmed = document.getElementById('confirmedPassword');

    password.addEventListener('input', validate);
    confirmed.addEventListener('input', validate);

    function validate() {
        $('#password-confirm-mismatch-msg').hide();
        var val = password.value;
        var cnf = confirmed.value;
        var disableSubmit = val == '' || cnf == '' || val != cnf || !policyPatternRegex.test(val) || !policyPatternRegex.test(cnf);
        $('#submit').prop('disabled', disableSubmit);
        if (disableSubmit) {
            $('#password').tooltip('show');
            return;
        }
    }
    $('#password').blur(function() {
        validateConfirmPassword();
    });
    $('#confirmedPassword').blur(function() {
        validateConfirmPassword();
    });

    function validateConfirmPassword() {
        $('#password-confirm-mismatch-msg').hide();
            var val = password.value;
            var cnf = confirmed.value;
            if(val && cnf && cnf.length > 0 && val != cnf) {
                $('#password-confirm-mismatch-msg').show();
        }
    }
}
