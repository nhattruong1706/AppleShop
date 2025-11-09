angular.module('appleShopApp')
    .controller('AdminOrderController', ['$http', '$timeout', function($http, $timeout) {
        const vm = this;
        vm.orders = [];
        vm.selectedOrderItems = [];
        vm.selectedOrderId = null;

        // Load tất cả đơn hàng
        vm.loadOrders = function() {
            $http.get('http://localhost:8080/api/orders/all')
                .then(resp => vm.orders = resp.data)
                .catch(err => console.error('❌ Lỗi khi load đơn hàng:', err));
        };

        // Cập nhật trạng thái
        vm.updateStatus = function(orderId, newStatus) {
            if (!confirm(`Xác nhận chuyển trạng thái đơn hàng sang ${newStatus}?`)) return;
            $http.put(`http://localhost:8080/api/orders/${orderId}/status?status=${newStatus}`)
                .then(resp => { alert(resp.data.message || '✅ Cập nhật thành công'); vm.loadOrders(); })
                .catch(err => { alert('❌ Lỗi!'); console.error(err); });
        };

        // Hủy đơn hàng
        vm.cancelOrder = function(orderId) {
            if (!confirm('Bạn có chắc muốn hủy đơn hàng này không?')) return;
            $http.put(`http://localhost:8080/api/orders/${orderId}/cancel`)
                .then(resp => { alert(resp.data.message || '✅ Đã hủy'); vm.loadOrders(); })
                .catch(err => { alert('❌ Lỗi!'); console.error(err); });
        };

        // Xóa tất cả đơn hàng đã hủy
        vm.deleteCancelledOrders = function() {
            if (!confirm('Bạn có chắc muốn xóa tất cả đơn hàng đã hủy không?')) return;
            $http.delete('http://localhost:8080/api/orders/cancelled')
                .then(resp => { alert(resp.data.message || '✅ Đã xóa'); vm.loadOrders(); })
                .catch(err => { alert('❌ Lỗi!'); console.error(err); });
        };

        // Xem chi tiết đơn hàng
        vm.showOrderItems = function(orderId) {
            vm.selectedOrderId = orderId;
            vm.selectedOrderItems = [];

            $http.get(`http://localhost:8080/api/orders/${orderId}/variants`)
                .then(resp => vm.selectedOrderItems = resp.data)
                .catch(err => { alert('❌ Không tải được sản phẩm'); console.error(err); });

            // Mở modal sau khi Angular bind xong
            $timeout(() => {
                const modalEl = document.getElementById('orderItemsModal');
                if(modalEl) new window.bootstrap.Modal(modalEl).show();
            }, 300);
        };

        // Load dữ liệu ban đầu
        vm.loadOrders();
    }]);
