var app = angular.module("appleShopApp", []);

app.config(['$httpProvider', function($httpProvider) {
    // Cho phép gửi cookie/session đi kèm request
    $httpProvider.defaults.withCredentials = true;
}]);

app.controller("ProfileCtrl", function($scope, $http) {
    $scope.user = {};
    $scope.message = "";
    $scope.passwordData = {
        oldPassword: "",
        newPassword: ""
    };

    // ================== LẤY THÔNG TIN NGƯỜI DÙNG HIỆN TẠI ==================
    $http.get("/api/profile").then(function(res) {
        $scope.user = res.data;
        console.log("✅ User hiện tại:", $scope.user);
    }, function(err) {
        console.error("❌ Lỗi tải thông tin người dùng:", err);
        $scope.message = "Không thể tải thông tin người dùng!";
    });

    // ================== CẬP NHẬT THÔNG TIN NGƯỜI DÙNG ==================
    $scope.updateProfile = function() {
        console.log("📤 Dữ liệu gửi đi:", $scope.user);

        $http.put("/api/profile", $scope.user)
            .then(function(res) {
                console.log("✅ Server trả về:", res.data);
                $scope.message = "Cập nhật hồ sơ thành công!";
            }, function(err) {
                console.error("❌ Lỗi cập nhật thông tin:", err);
                $scope.message = "Lỗi cập nhật hồ sơ!";
            });
    };

    // ================== ĐỔI MẬT KHẨU ==================
    $scope.changePassword = function() {
        if (!$scope.passwordData.oldPassword || !$scope.passwordData.newPassword) {
            $scope.message = "⚠️ Vui lòng nhập đầy đủ thông tin!";
            return;
        }

        $http.put("/api/profile/change-password", $scope.passwordData)
            .then(function(res) {
                console.log("✅ Đổi mật khẩu:", res.data);
                $scope.message = "Đổi mật khẩu thành công!";
                $scope.passwordData = {};
            }, function(err) {
                console.error("❌ Lỗi đổi mật khẩu:", err);
                $scope.message = "Lỗi khi đổi mật khẩu!";
            });
    };
});
