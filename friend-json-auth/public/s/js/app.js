/*global jQuery alert console*/
(function ($) {
    "use strict";
    var username, password, dologin, dologout, getAuthDataButton, log;


    log = (typeof console !== "undefined" && console.log) ?
          console.log
        : function () {};

    function login(username, password) {
        var data = JSON.stringify({"username": username, "password": password});

        return $.ajax({
            type: "POST",
            url: "../api/session",
            dataType: "json",
            contentType: "application/json",
            data: data
        });
    }

    function logout() {
        return $.ajax({
            type: "DELETE",
            url: "../api/session"
        });
    }

    function onLoginOk() {
        $("#login-form").hide();
        $("#logout-form").show();
        onGetAuthData();
    }

    function onLoginError(response) {
        alert("login error");
        log(response);
        onGetAuthData();
    }

    function onLogoutOk() {
        $("#logout-form").hide();
        $("#login-form").show();
        onGetAuthData();
    }

    function onLogoutError(response) {
        alert("logout error");
        log(response);
        onGetAuthData();
    }

    function onLoginClick() {
        var
            user = $.trim(username.val()),
            pass = $.trim(password.val());

        return login(user, pass)
            .done(onLoginOk)
            .fail(onLoginError);
    }

    function onLogoutClick() {
        return logout()
            .done(onLogoutOk)
            .fail(onLogoutError);
    }

    function onGetAuthDataOk(authData) {
        $("#auth-data").text(JSON.stringify(authData));
    }

    function onGetAuthDataError(response) {
        alert("error getting auth data");
        log(response);
    }

    function onGetAuthData() {
        return $.ajax({
            type: "GET",
            dataType: "json",
            url: "../api/auth"
        })
            .done(onGetAuthDataOk)
            .error(onGetAuthDataError);
    }

    $(function () {
        username = $("#username");
        password = $("#password");
        dologin = $("#dologin");
        dologout = $("#dologout");
        getAuthDataButton = $("#getauthdata");

        dologin.click(onLoginClick);
        dologout.click(onLogoutClick);
        getAuthDataButton.click(onGetAuthData);
    });
}(jQuery));
