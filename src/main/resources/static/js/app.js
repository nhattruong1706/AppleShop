// js/app.js
var app = angular.module('appleShopApp', []);

app.controller('ProductController', function($scope, $http) {
    $scope.products = [];
    $scope.groups = [];

    // Lấy dữ liệu sản phẩm
    $http.get('/static/data/products.json').then(function(response) {
        $scope.products = response.data;
        $scope.groups = groupByName($scope.products);
    });

    // Gom sản phẩm theo tên
    function groupByName(list) {
        var map = {};
        list.forEach(function(p) {
            var key = p.name || 'Unknown';
            if (!map[key]) map[key] = { name: key, images: [], colors: [] };
            map[key].images.push(p.image);
            if (p.colors && p.colors.length) {
                p.colors.forEach(function(c) {
                    if (map[key].colors.indexOf(c) === -1) map[key].colors.push(c);
                });
            }
        });
        return Object.values(map);
    }

    // Lọc tìm kiếm
    $scope.searchFilter = function(item) {
        if (!$scope.searchText) return true;
        var t = $scope.searchText.toLowerCase();
        return item.name.toLowerCase().includes(t);
    };

    // Gán màu cho nhãn
    $scope.colorStyle = function(color) {
        var map = {
            silver: '#C0C0C0',
            black: '#000000',
            white: '#FFFFFF',
            pink: '#FFB6C1',
            red: '#D9534F',
            blue: '#007BFF',
            green: '#28A745',
            gold: '#D4AF37',
            graphite: '#555555',
            midnight: '#111827',
            purple: '#7B5BD6',
            yellow: '#FFD633'
        };
        var name = color ? color.toLowerCase() : '';
        for (var k in map) if (name.indexOf(k) !== -1)
            return {'background-color': map[k], 'color': (k==='white'?'#000':'#fff'), 'border':'1px solid #333', 'padding':'4px 8px', 'border-radius':'6px'};
        return {'background-color': '#6c757d', 'color':'#fff', 'padding':'4px 8px', 'border-radius':'6px'};
    };
});
