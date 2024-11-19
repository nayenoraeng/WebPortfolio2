//전화번호 형식 만들기
document.getElementById("phoneNum").addEventListener('input', function(phoneNumForm) {
    let input = phoneNumForm.target.value;
    input = input.replace(/[^0-9]/g, '');

    if (input.length < 4) {
        phoneNumForm.target.value = input;
    } else if (input.length < 8) {
        phoneNumForm.target.value = input.slice(0, 3) + '-' + input.slice(3);
    } else {
        phoneNumForm.target.value = input.slice(0, 3) + '-' + input.slice(3, 7) + '-' + input.slice(7, 11);
    }
});
