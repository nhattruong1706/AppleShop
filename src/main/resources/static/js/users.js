angular.module('appleShopApp')
    .controller('UserController', function($scope, $http) {

        $scope.users = [];
        $scope.searchUsername = "";

        // 🟢 Load toàn bộ user
        $scope.loadUsers = function() {
            $http.get("/api/users").then(resp => {
                $scope.users = resp.data;
            });
        };

        // 🔍 Tìm kiếm user theo username
        $scope.searchUser = function() {
            if (!$scope.searchUsername.trim()) {
                alert("Vui lòng nhập username cần tìm!");
                return;
            }

            $http.get("/api/users/search", { params: { username: $scope.searchUsername } })
                .then(resp => {
                    if (resp.data.length === 0) {
                        alert("Không tìm thấy người dùng nào.");
                    }
                    $scope.users = resp.data;
                })
                .catch(() => {
                    alert("Lỗi khi tìm kiếm người dùng!");
                });
        };

        // 🗑️ Xóa user
        $scope.deleteUser = function(id) {
            if (confirm("Bạn có chắc muốn xóa người dùng này không?")) {
                $http.delete("/api/users/" + id).then(() => {
                    $scope.users = $scope.users.filter(u => u.id !== id);
                });
            }
        };

        // 🔄 Cập nhật quyền user
        $scope.updateRole = function(user) {
            $http.put("/api/users/" + user.id, user).then(() => {
                alert("Cập nhật quyền thành công!");
            });
        };

        // Tải lần đầu
        $scope.loadUsers();
    });
