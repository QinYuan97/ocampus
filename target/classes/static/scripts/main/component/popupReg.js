(function (window) {
    var PopupReg = Base.createClass('main.component.PopupReg');
    var Popup = Base.getClass('main.component.Popup');
    var Component = Base.getClass('main.component.Component');
    var Util = Base.getClass('main.base.Util');

    Base.mix(PopupReg, Component, {
        _tpl: [
            '<div class="wrapper-content clearfix">',
                '<div class="input-section">',
                    '<div class="form-group">',
                        '<label class="control-label">用户名</label>',
                        '<div class="control-group js-username"><input type="text" placeholder="请输入用户名"></div>',
                    '</div>',
                    '<div class="form-group">',
                        '<label class="control-label">电子邮箱</label>',
                        '<div class="control-group js-email"><input type="email" placeholder="请输入邮箱"></div>',
                    '</div>',
                    '<div class="form-group">',
                        '<label class="control-label">密码</label>',
                        '<div class="control-group js-pwd"><input type="password" placeholder="请输入密码"></div>',
                    '</div>',
                    '<div class="form-group">',
                        '<div class="col-input-login">',
                            '<a class="btn btn-info js-register center" href="javascript:void(0);">注册</a>',
                        '</div>',
                    '</div>',
                '</div>',
            '</div>'].join(''),
        listeners: [{
            name: 'render',
            type: 'custom',
            handler: function () {
                var that = this;
                var oEl = that.getEl();
                that.usernameIpt = oEl.find('div.js-username');
                that.emailIpt = oEl.find('div.js-email');
                that.pwdIpt = oEl.find('div.js-pwd');
                that.initCpn();
            }
        },  {
            name: 'click a.js-register',
            handler: function (oEvent) {
                oEvent.preventDefault();
                var that = this;
                // 值检查
                if (!that.checkVal()) {
                    return;
                }
                var oData = that.val();
                $.ajax({
                    url: '/reg/',
                    type: 'post',
                    dataType: 'json',
                    data: {
                        username: oData.username,
                        email: oData.email,
                        password: oData.pwd
                    }
                }).done(function (oResult) {
                    if (oResult.code === 0) {
//                        window.location.reload();
                        that.emit('register');
                    } else {
                        oResult.msgname && that.iptError(that.usernameIpt, oResult.msgname);
                        oResult.msgpwd && that.iptError(that.pwdIpt, oResult.msgpwd);
                        oResult.msgemail && that.iptError(that.emailIpt, oResult.msgemail);
                    }
                }).fail(function () {
                    alert('出现错误，请重试');
                });
            }
        }],
        show: fStaticShow
    }, {
        initialize: fInitialize,
        initCpn: fInitCpn,
        val: fVal,
        checkVal: fCheckVal,
        iptSucc: fIptSucc,
        iptError: fIptError,
        iptNone: fIptNone
    });

    function fStaticShow(oConf) {
        var that = this;
        var oLogin = new PopupReg(oConf);
        var oPopup = new Popup({
            width: 540,
            content: oLogin.html()
        });
        oLogin._popup = oPopup;
        Component.setEvents();
    }

    function fInitialize(oConf) {
        var that = this;
        delete oConf.renderTo;
        PopupReg.superClass.initialize.apply(that, arguments);
    }

    function fInitCpn() {
        var that = this;
        that.usernameIpt.find('input').on('focus', Base.bind(that.iptNone, that, that.usernameIpt));
        that.emailIpt.find('input').on('focus', Base.bind(that.iptNone, that, that.emailIpt));
        that.pwdIpt.find('input').on('focus', Base.bind(that.iptNone, that, that.pwdIpt));
    }

    function fVal(oData) {
        var that = this;
        var oEl = that.getEl();
        var oUserNameIpt = that.usernameIpt.find('input');
        var oEmailIpt = that.emailIpt.find('input');
        var oPwdIpt = that.pwdIpt.find('input');
        if (arguments.length === 0) {
            return {
                username: $.trim(oUserNameIpt.val()),
                email: $.trim(oEmailIpt.val()),
                pwd: $.trim(oPwdIpt.val()),
            };
        } else {
            oUserNameIpt.val($.trim(oData.username));
            oEmailIpt.val($.trim(oData.email));
            oPwdIpt.val($.trim(oData.pwd));
        }
    }

    function fCheckVal() {
        var that = this;
        var oData = that.val();
        var bRight = true;
        if(!oData.username){
            that.iptError(that.usernameIpt, '用户名不能为空');
            bRight = false;
        }

        if (!oData.pwd) {
            that.iptError(that.pwdIpt, '密码不能为空');
            bRight = false;
        } else if (oData.pwd.length < 6) {
            that.iptError(that.pwdIpt, '密码不能小于6位');
            bRight = false;
        }
        return bRight;
    }

    function fIptSucc(oIpt) {
        var that = this;
        oIpt = $(oIpt);
        that.iptNone(oIpt);
        oIpt.addClass('success');
        if (!oIpt.find('.icon-ok-sign').get(0)) {
            oIpt.append('<i class="input-icon icon-ok-sign"></i>');
        }
    }

    function fIptError(oIpt, sMsg) {
        var that = this;
        oIpt = $(oIpt);
        that.iptNone(oIpt);
        oIpt.addClass('error');
        if (!oIpt.find('.icon-remove-sign').get(0)) {
            oIpt.append('<i class="input-icon icon-remove-sign"></i>');
        }
        var oSpan = oIpt.find('.input-tip');
        if (!oSpan.get(0)) {
            oSpan = $('<span class="input-tip"></span>');
            oIpt.append(oSpan);
        }
        oSpan.html($.trim(sMsg));
    }

    function fIptNone(oIpt) {
        var that = this;
        $(oIpt).removeClass('error success');
    }
})(window);