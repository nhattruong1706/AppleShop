var app = angular.module("appleShopApp", []);

app.config(['$httpProvider', function($httpProvider) {
    $httpProvider.defaults.withCredentials = true;
}]);

app.controller("ProfileCtrl", function($scope, $http) {
    $scope.user = {};
    $scope.message = "";

    // Khởi tạo dữ liệu mật khẩu
    $scope.passwordData = {
        oldPassword: "",
        newPassword: "",
        confirmPassword: ""
    };

    // Thông báo mật khẩu
    $scope.errorPwd = null;
    $scope.messagePwd = null;

    // ================== LẤY THÔNG TIN NGƯỜI DÙNG HIỆN TẠI ==================
    $http.get("/api/profile").then(function(res) {
        $scope.user = res.data;
        console.log("User hiện tại:", $scope.user);
    }, function(err) {
        console.error("Lỗi tải thông tin người dùng:", err);
        $scope.message = "Không thể tải thông tin người dùng!";
    });

    // ================== CẬP NHẬT HỒ SƠ ==================
    $scope.updateProfile = function() {
        $http.put("/api/profile", $scope.user)
            .then(function(res) {
                $scope.message = "Cập nhật hồ sơ thành công!";
            }, function(err) {
                $scope.message = "Lỗi cập nhật hồ sơ!";
            });
    };

    // ================== ĐỔI MẬT KHẨU ==================
    $scope.changePassword = function () {

        // Kiểm tra khớp mật khẩu mới
        if ($scope.passwordData.newPassword !== $scope.passwordData.confirmPassword) {
            $scope.errorPwd = "Mật khẩu mới không trùng khớp với nhau!";
            $scope.messagePwd = null;
            return;
        }

        $http.put("/api/profile/change-password", {
            oldPassword: $scope.passwordData.oldPassword,
            newPassword: $scope.passwordData.newPassword,
            confirmPassword: $scope.passwordData.confirmPassword
        }).then(res => {
            $scope.messagePwd = res.data.message;
            $scope.errorPwd = null;
        }).catch(err => {
            $scope.errorPwd = err.data.error;
            $scope.messagePwd = null;
        });
    };

});
