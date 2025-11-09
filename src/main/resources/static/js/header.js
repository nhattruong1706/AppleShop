(function() {
    angular.module('appleShopApp')
        .controller('HeaderCtrl', ['$http', '$window', '$timeout', '$rootScope', function($http, $window, $timeout, $rootScope) {
            const hd = this;
            hd.query = '';
            hd.currentUser = null;
            let searchTimeout = null;

            // 🧠 Load thông tin user
            hd.loadUser = function() {
                $http.get('/api/auth/me', {withCredentials: true})
                    .then(res => hd.currentUser = res.data)
                    .catch(() => hd.currentUser = null);
            };

            // 🚪 Logout
            hd.logout = function() {
                $http.post('/api/auth/logout', {}, {withCredentials: true})
                    .then(() => {
                        hd.currentUser = null;
                        $window.location.href = '/login.html';
                    });
            };

            // 🔍 Debounce tìm kiếm + phát event cho MainCtrl
            hd.onSearchChange = function() {
                if (searchTimeout) $timeout.cancel(searchTimeout);
                searchTimeout = $timeout(() => {
                    $rootScope.$broadcast('searchQueryChanged', hd.query);
                }, 300);
            };

            hd.loadUser();
        }]);
})();
