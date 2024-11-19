//js내 유효성 검사
function validateForm() {

    event.preventDefault();

    const username = document.getElementById('username').value;
    const password = document.getElementById('password').value;
    const confirmPassword = document.getElementById('confirmPassword').value;
    const passwordPattern = /^(?=.*[0-9])(?=.*[A-Za-z])(?=.*\W)(?=.{8,16}$)/;
    const phoneNum = document.getElementById('phoneNum').value;
    const email = document.getElementById('email').value;
    const postcode = document.getElementById('postcode').value;
    const address = document.getElementById('address').value;
    const detailAddress = document.getElementById('detailAddress').value;


    if (!passwordPattern.test(password)) {
        alert('비밀번호는 8-16자 영문 대소문자, 숫자, 특수문자를 사용해야 합니다.');
        return false;
    }
    if (password !== confirmPassword) {
        alert('비밀번호가 일치하지 않습니다.');
        return false;
    }
    if (postcode == null || postcode === '') {
        alert('우편번호를 입력해주세요.');
        return false;
    }
    if (address == null || address === '') {
        alert('주소를 입력해주세요.');
        return false;
    }
    if (detailAddress == null || detailAddress === '') {
        alert('상세주소를 입력해주세요.');
        return false;
    }

    const isValid = checkDuplicates(username, email, phoneNum);
    if (isValid) {
        // If all checks are valid, submit the form
        document.getElementById('signup').submit();
    }
}


async function checkDuplicates(username, email, phoneNum) {
    // Check username
    const usernameResponse = await fetch('/checkUsername', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify({username})
    });

    const usernameResult = await usernameResponse.text();
    if (usernameResult.includes("이미 사용중인 아이디입니다.")) {
        alert(usernameResult);
        return false;
    }

    // Check email
    const emailResponse = await fetch('/checkEmail', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify({email})
    });

    const emailResult = await emailResponse.text();
    if (emailResult.includes("이미 사용중인 이메일입니다.")) {
        alert(emailResult);
        return false;
    }

    // Check phone number
    const phoneResponse = await fetch('/checkPhoneNum', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify({phoneNum})
    });

    const phoneResult = await phoneResponse.text();
    if (phoneResult.includes("이미 사용중인 전화번호입니다.")) {
        alert(phoneResult);
        return false;
    }

    alert('회원가입이 완료되었습니다.');
    return true;
}