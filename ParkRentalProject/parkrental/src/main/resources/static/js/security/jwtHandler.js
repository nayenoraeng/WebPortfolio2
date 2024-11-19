window.addEventListener('beforeunload', function() {
    deleteJWTCookie();
});

function deleteJWTCookie() {
    document.cookie = 'JWT=; Max-Age=0; path=/; domain=; secure; HttpOnly;';
    console.log('JWT cookie deleted');
}

function refreshToken() {
    fetch('api/auth/refresh-token', {
        method: 'POST',
        credentials: 'include',
    })
    .then(response => {
        if (response.ok) {
            return response.json();
        } else {
            throw new Error('Failed to refresh Token');
        }
    })
    .then(data => {
        document.cookie = 'JWT=' + data.token + '; path =/;';
    })
    .catch(error => {
        console.error('Error refreshing token: ', error);
        deleteJWTCookie();
        window.location.href = '/guest/Login';
    });
}

const refreshInterval = 15 * 60 * 1000;//자동토큰 갱신 - 15분

setInterval(refreshToken, refreshInterval);

function deleteJWTCookie() {
    document.cookie = 'JWT=; Max-Age=0; path=/; domain=; secure; HttpOnly;';
    console.log('JWT cookie deleted');
}