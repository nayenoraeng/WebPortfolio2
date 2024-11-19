//유저네임, 이메일, 전화번호, 사업자번호 ajax로 서버와 연동 후 확인 기능
$(document).ready(function() {
    $('#checkUsername').click(function(){
        var username = $('#username').val();
        $.ajax({
            type: "POST",
            url: "/guest/checkUsername",
            data: {username: username},
            success: function (response) {
                alert(response);
            }
        });
    });

    $('#checkEmail').click(function(){
        var email = $('#email').val();
        $.ajax({
            type: "POST",
            url:"/guest/checkEmail",
            data: {email:email},
            success: function (response) {
                alert(response);
            }
        });
    });

        $('#checkSellerEmail').click(function(){
            var email = $('#email').val();
            $.ajax({
                type: "POST",
                url:"/guest/checkSellerEmail",
                data: {email:email},
                success: function (response) {
                    alert(response);
                }
            });
        });

    $('#checkBusinessNum').click(function(){
        var businessNum = $('#businessNum').val();
        $.ajax({
            type: "POST",
            url: "/guest/checkBusinessNum",
            data: {businessNum: businessNum},
            success: function (response) {
                alert(response);
            }
        });
    });

     $('#checkPhoneNum').click(function(){
        var phoneNum = $('#phoneNum').val();
        $.ajax({
            type: "POST",
            url:"/guest/checkSellerPhoneNum",
            data: {phoneNum:phoneNum},
            success: function (response) {
                alert(response);
            }
        });
    });

     $('#checkSellerPhoneNum').click(function(){
        var phoneNum = $('#phoneNum').val();
        $.ajax({
            type: "POST",
            url:"/guest/checkSellerPhoneNum",
            data: {phoneNum:phoneNum},
            success: function (response) {
                alert(response);
            }
        });
    });
});
