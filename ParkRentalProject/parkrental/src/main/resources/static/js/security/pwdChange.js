document.getElementById('passwordChangeForm').addEventListener('submit', function(event) {
    event.preventDefault(); // Prevent default form submission

    const form = event.target;
    const formData = new FormData(form);

    if (formData.get('newPwd') !== formData.get('confirmPwd')) {
        alert("새 비밀번호가 일치하지 않습니다.");
        return;
    }

    fetch(form.action, {
        method: 'POST',
        body: formData,
        credentials: 'include',
    })
    .then(response => {
        if (response.ok) {
            alert("비밀번호가 변경되었습니다.");
            window.location.href = '/user/userpage'; // Redirect to user page or another page
        } else {
            return response.json().then(data => {
                alert(data.message);
            });
        }
    })
    .catch(error => {
        console.error('Error during password change:', error);
        alert('비밀번호 변경 중 에러가 발생하였습니다.');
    });
});