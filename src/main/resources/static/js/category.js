// Khởi tạo module chính nếu chưa tồn tại
var app = angular.module("appleShopApp");

// ✅ Controller quản lý danh mục
app.controller("CategoryController", function($scope, $http, $timeout) {
    $scope.categories = [];
    $scope.category = {};
    $scope.isEditing = false;
    $scope.showToast = false;
    $scope.toastMessage = "";

    // Hiển thị toast thông báo
    $scope.showToastMessage = function(message, color = "#10b981") {
        $scope.toastMessage = message;
        const toast = document.querySelector(".toast-notify");
        if (toast) toast.style.background = color;
        $scope.showToast = true;
        $timeout(() => $scope.showToast = false, 3000);
    };

    // 🟢 Lấy danh sách danh mục
    $scope.loadCategories = function() {
        $http.get("/api/categories").then(function(response) {
            $scope.categories = response.data;
        }).catch(() => {
            $scope.showToastMessage("❌ Lỗi tải danh mục!", "#ef4444");
        });
    };

    // 🟢 Thêm mới hoặc cập nhật danh mục
    $scope.saveCategory = function() {
        if (!$scope.category.name || $scope.category.name.trim() === "") {
            $scope.showToastMessage("⚠️ Vui lòng nhập tên danh mục!", "#f59e0b");
            return;
        }

        if ($scope.isEditing) {
            // Cập nhật
            $http.put("/api/categories/" + $scope.category.id, $scope.category)
                .then(() => {
                    $scope.loadCategories();
                    $scope.cancelEdit();
                    $scope.showToastMessage("✅ Cập nhật danh mục thành công!");
                })
                .catch(() => {
                    $scope.showToastMessage("❌ Lỗi khi cập nhật danh mục!", "#ef4444");
                });
        } else {
            // Thêm mới
            $http.post("/api/categories", $scope.category)
                .then(() => {
                    $scope.loadCategories();
                    $scope.category = {};
                    $scope.showToastMessage("✅ Thêm danh mục thành công!");
                })
                .catch(() => {
                    $scope.showToastMessage("❌ Lỗi khi thêm danh mục!", "#ef4444");
                });
        }
    };

    // 🟢 Chọn danh mục để sửa
    $scope.editCategory = function(cat) {
        $scope.category = angular.copy(cat);
        $scope.isEditing = true;
    };

    // 🟢 Hủy chỉnh sửa
    $scope.cancelEdit = function() {
        $scope.isEditing = false;
        $scope.category = {};
    };

    // 🟢 Xóa danh mục
    $scope.deleteCategory = function(id) {
        if (confirm("Bạn có chắc muốn xóa danh mục này không?")) {
            $http.delete("/api/categories/" + id)
                .then(() => {
                    $scope.loadCategories();
                    $scope.showToastMessage("🗑️ Xóa danh mục thành công!");
                })
                .catch(() => {
                    $scope.showToastMessage("❌ Lỗi khi xóa danh mục!", "#ef4444");
                });
        }
    };

    // 🟢 Tải dữ liệu ban đầu
    $scope.loadCategories();
});
