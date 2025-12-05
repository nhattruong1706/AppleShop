var app = angular.module("appleShopApp");

app.controller("ReportController", function ($scope, $http, $timeout) {

    let productChart = null;
    let dateChart = null;

    // Format yyyy-MM-dd
    function formatDate(d) {
        return new Date(d).toISOString().split("T")[0];
    }

    // ============================= LOAD DATA =============================
    $scope.load = function () {

        let params = {};

        if ($scope.start) params.start = formatDate($scope.start);
        if ($scope.end) params.end = formatDate($scope.end);

        // Tổng doanh thu
        $http.get("/api/admin/report/total", { params }).then(res => {
            $scope.total = res.data;
        });

        // Doanh thu theo sản phẩm
        $http.get("/api/admin/report/products", { params }).then(res => {
            $scope.items = res.data;

            // Đợi Angular render xong canvas mới vẽ chart
            $timeout(() => drawProductChart(res.data), 150);
        });

        // Theo ngày
        if ($scope.start && $scope.end) {
            $http.get("/api/admin/report/by-date", { params }).then(res => {
                $scope.dateItems = res.data;

                // Vẽ chart theo ngày
                $timeout(() => drawDateChart(res.data), 150);
            });
        }
    };

    // ============================= EXPORT EXCEL =============================
    $scope.exportExcel = function () {
        window.location.href = "/api/admin/report/excel";
    };

    // ============================= PRODUCT CHART =============================
    function drawProductChart(data) {

        let canvas = document.getElementById("productChart");
        if (!canvas) return console.error("Không tìm thấy canvas productChart");

        let ctx = canvas.getContext("2d");

        if (productChart) productChart.destroy();

        productChart = new Chart(ctx, {
            type: 'bar',
            data: {
                labels: data.map(x => x.name),
                datasets: [{
                    label: "Doanh thu sản phẩm (VND)",
                    data: data.map(x => x.revenue),
                    borderWidth: 1
                }]
            }
        });
    }

    // ============================= DATE CHART =============================
    function drawDateChart(data) {

        let canvas = document.getElementById("dateChart");
        if (!canvas) return console.error("Không tìm thấy canvas dateChart");

        let ctx = canvas.getContext("2d");

        if (dateChart) dateChart.destroy();

        dateChart = new Chart(ctx, {
            type: 'line',
            data: {
                labels: data.map(x => x.date),
                datasets: [{
                    label: "Doanh thu theo ngày (VND)",
                    data: data.map(x => x.revenue),
                    borderWidth: 2
                }]
            }
        });
    }

    // ============================= LOAD LẦN ĐẦU =============================
    $scope.load();
});
