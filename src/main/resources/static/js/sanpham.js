(function() {
    angular.module('appleShopApp') // ⚠️ không có [] để dùng chung module
        .constant('API_BASE', window.API_BASE || 'http://localhost:8080')
        .controller('MainCtrl', ['$http', '$scope', 'API_BASE', function($http, $scope, API_BASE) {
            var vm = this;
            vm.API_BASE = API_BASE;
            vm.products = [];
            vm.loadingProducts = false;
            vm.productForm = {};
            vm.editingProduct = false;
            vm.productMessage = '';
            vm.uploadingProductImage = false;
            vm.uploadingVariantImage = false;
            vm.selectedProduct = null;
            vm.variants = [];
            vm.variantForm = {};
            vm.editingVariant = false;
            vm.variantMessage = '';

            vm.scrollToTop = function() {
                window.scrollTo({ top: 0, behavior: 'smooth' });
            };

            vm.loadProducts = function() {
                vm.loadingProducts = true;
                $http.get(API_BASE + '/api/products')
                    .then(res => vm.products = res.data)
                    .catch(() => alert('❌ Lỗi tải danh sách sản phẩm!'))
                    .finally(() => vm.loadingProducts = false);
            };

            vm.onProductImageSelect = function(files) {
                if (!files || !files[0] || vm.uploadingProductImage) return;
                vm.uploadingProductImage = true;
                let formData = new FormData();
                formData.append("file", files[0]);
                $http.post(API_BASE + '/api/upload/image', formData, { headers: { 'Content-Type': undefined } })
                    .then(res => {
                        const url = res.data.url;
                        vm.productForm.img = url.startsWith('http') ? url : (vm.API_BASE + url);
                    })
                    .catch(() => alert('❌ Upload ảnh sản phẩm thất bại!'))
                    .finally(() => vm.uploadingProductImage = false);
            };

            vm.categories = [];
            vm.loadCategories = function() {
                $http.get(API_BASE + '/api/categories')
                    .then(res => vm.categories = res.data)
                    .catch(err => console.error(err));
            };

            vm.saveProduct = function() {
                const url = vm.editingProduct
                    ? API_BASE + '/api/products/' + vm.productForm.id
                    : API_BASE + '/api/products';
                const method = vm.editingProduct ? 'put' : 'post';
                $http[method](url, vm.productForm)
                    .then(() => {
                        vm.productMessage = vm.editingProduct ? '✅ Cập nhật thành công' : '✅ Thêm mới thành công';
                        vm.resetProductForm();
                        vm.loadProducts();
                    })
                    .catch(() => alert('❌ Lưu sản phẩm thất bại!'));
            };

            vm.editProduct = p => {
                vm.editingProduct = true;
                vm.productForm = angular.copy(p);
                vm.scrollToTop();
            };

            vm.resetProductForm = () => {
                vm.editingProduct = false;
                vm.productForm = {};
                vm.productMessage = '';
            };

            vm.deleteProduct = p => {
                if(confirm('Xóa sản phẩm "' + p.name + '"?'))
                    $http.delete(API_BASE + '/api/products/' + p.id).then(() => vm.loadProducts());
            };

            vm.selectProduct = p => {
                vm.selectedProduct = p;
                vm.loadVariants(p.id);
                vm.scrollToTop();
            };

            vm.closeVariants = () => { vm.selectedProduct = null; vm.variants = []; };

            vm.loadVariants = id => {
                $http.get(API_BASE + '/api/products/' + id + '/variants')
                    .then(res => vm.variants = res.data)
                    .catch(() => alert('❌ Lỗi tải biến thể!'));
            };

            vm.onVariantImageSelect = function(files) {
                if (!files || !files[0] || vm.uploadingVariantImage) return;
                vm.uploadingVariantImage = true;
                let formData = new FormData();
                formData.append("file", files[0]);
                $http.post(API_BASE + '/api/upload/image', formData, { headers: { 'Content-Type': undefined } })
                    .then(res => {
                        const url = res.data.url;
                        vm.variantForm.img = url.startsWith('http') ? url : (vm.API_BASE + url);
                    })
                    .catch(() => alert('❌ Upload ảnh biến thể thất bại!'))
                    .finally(() => vm.uploadingVariantImage = false);
            };

            vm.saveVariant = function() {
                if (!vm.selectedProduct) return alert('⚠️ Hãy chọn sản phẩm trước!');
                const url = vm.editingVariant
                    ? API_BASE + '/api/products/variants/' + vm.variantForm.id
                    : API_BASE + '/api/products/' + vm.selectedProduct.id + '/variants';
                const method = vm.editingVariant ? 'put' : 'post';
                $http[method](url, vm.variantForm)
                    .then(() => {
                        vm.variantMessage = vm.editingVariant ? '✅ Cập nhật biến thể' : '✅ Thêm biến thể';
                        vm.resetVariantForm();
                        vm.loadVariants(vm.selectedProduct.id);
                    })
                    .catch(() => alert('❌ Lưu biến thể thất bại!'));
            };

            vm.editVariant = v => {
                vm.editingVariant = true;
                vm.variantForm = angular.copy(v);
                vm.scrollToTop();
            };

            vm.resetVariantForm = () => {
                vm.editingVariant = false;
                vm.variantForm = {};
                vm.variantMessage = '';
            };

            vm.deleteVariant = v => {
                if(confirm('Xóa biến thể "' + v.variantName + '"?'))
                    $http.delete(API_BASE + '/api/products/variants/' + v.id).then(() => vm.loadVariants(vm.selectedProduct.id));
            };

            vm.loadProducts();
            vm.loadCategories();
        }]);
})();
