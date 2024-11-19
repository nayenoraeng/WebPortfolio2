document.getElementById('loginForm').addEventListener('submit', function(event) {
    event.preventDefault(); // Prevent default form submission

    const form = event.target;
    const formData = new FormData(form);

    fetch(form.action, {
        method: 'POST',
        body: formData,
        credentials: 'include' // Include cookies
    }).then(response => {
        return response.json(); // Always parse the response as JSON
    }).then(data => {
        if (data.success) {
            alert(data.message);
            window.location.href = '/';
            setTimeout(function() {
                window.location.href = '/';
            }, 300);

        } else {
            alert(data.message);
        }
    }).catch(error => {
        console.error('Error during login:', error);
        alert('로그인 처리 중 오류가 발생했습니다.');
    });
});