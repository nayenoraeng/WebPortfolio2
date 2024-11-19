//사업자 번호 형식 만들기
document.getElementById("businessNum").addEventListener('input', function(businessNumForm) {
    let input = businessNumForm.target.value;
    // 숫자만 남기기
    input = input.replace(/[^0-9]/g, '');

    // 길이에 따라 포맷 적용
    if (input.length <= 3) {
        businessNumForm.target.value = input; // 3자리 이하
    } else if (input.length <= 5) {
        businessNumForm.target.value = input.slice(0, 3) + '-' + input.slice(3); // 3-5자리
    } else if (input.length <= 10) {
        businessNumForm.target.value = input.slice(0, 3) + '-' + input.slice(3, 5) + '-' + input.slice(5); // 6-10자리
    } else {
        businessNumForm.target.value = input.slice(0, 3) + '-' + input.slice(3, 5) + '-' + input.slice(5, 10); // 최대 10자리
    }
});
