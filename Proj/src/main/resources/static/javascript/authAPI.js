const TokenStorage = {
	saveToken: function (jwt) {
		localStorage.setItem("token", jwt);
	},
	getToken: function () {
		return localStorage.getItem("token");
	},
	
	removeToken: function () {
		localStorage.removeItem("token");
	}
}