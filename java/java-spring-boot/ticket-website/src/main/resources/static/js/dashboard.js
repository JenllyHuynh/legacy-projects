// Load khi trang mở
// window.onload = function () {
//     loadDashboard();
// };
//
// async function loadDashboard() {
//     try {
//         const response = await fetch("/api/admin/dashboard");
//
//         // 👇 Debug cực quan trọng (tránh lỗi JSON)
//         const text = await response.text();
//         console.log("RAW response:", text);
//
//         const data = JSON.parse(text);
//
//         console.log("Parsed data:", data);
//
//         renderDashboard(data);
//
//     } catch (error) {
//         console.error("Error loading dashboard:", error);
//     }
// }
//
// function renderDashboard(data) {
//
//     // Total Revenue
//     document.getElementById("revenue").innerText =
//         formatMoney(data.totalRevenue);
//
//     // Total Events
//     document.getElementById("events").innerText =
//         data.totalEvents;
//
//     // Total Customers
//     document.getElementById("customers").innerText =
//         data.totalCustomers;
//
//     // Tickets Sold
//     document.getElementById("tickets").innerText =
//         data.ticketsSold;
//
//     document.getElementById("lastMonth").innerText =
//         formatMoney(data.lastMonthRevenue);
//
//     // Hiển thị tăng giảm
//     const growthEl = document.getElementById("growth");
//
//     const growth = data.growthRate.toFixed(2);
//
//     if (growth >= 0) {
//         growthEl.innerHTML = "+" + growth + "%";
//         growthEl.style.color = "green";
//     } else {
//         growthEl.innerHTML = "📉 " + growth + "%";
//         growthEl.style.color = "red";
//     }
// }
//
// // Format tiền VND cho đẹp
// function formatMoney(value) {
//     if (!value) return "0 VND";
//     return value.toLocaleString("vi-VN") + " VND";
// }


// Line chart revenue
let revenueChart;

function setDefaultDate() {
    const today = new Date();
    const past = new Date();
    past.setDate(today.getDate() - 7);

    document.getElementById("toDate").value = today.toISOString().split('T')[0];
    document.getElementById("fromDate").value = past.toISOString().split('T')[0];
}
//
// async function loadRevenueChart() {
//     try {
//         const from = document.getElementById("fromDate").value;
//         const to = document.getElementById("toDate").value;
//
//         const res = await fetch(`/api/admin/revenue?from=${from}&to=${to}`);
//         if (!res.ok) throw new Error("API Error");
//
//         const data = await res.json();
//
//         const labels = data.date;
//         const values = data.revenue;
//
//         renderRevenueChart(labels, values);
//
//     } catch (err) {
//         console.error(err);
//     }
// }
//
function renderRevenueChart(labels, values) {
    const ctx = document.getElementById('revenueTrendChart').getContext('2d');

    // Gradient
    const gradient = ctx.createLinearGradient(0, 0, 0, 400);
    gradient.addColorStop(0, 'rgba(79, 70, 229, 0.1)');
    gradient.addColorStop(1, 'rgba(79, 70, 229, 0)');

    if (revenueChart) revenueChart.destroy();

    revenueChart = new Chart(ctx, {
        type: 'line',
        data: {
            labels,
            datasets: [{
                label: 'Revenue',
                data: values,
                borderColor: '#4F46E5',
                borderWidth: 3,
                fill: true,
                backgroundColor: gradient,
                tension: 0.4,
                pointRadius: 0,
                pointHoverRadius: 6
            }]
        },
        options: {
            responsive: true,
            maintainAspectRatio: false,

            plugins: {
                legend: { display: false },

                tooltip: {
                    callbacks: {
                        label: ctx => 'VND' + ctx.raw.toLocaleString()
                    }
                }
            },

            scales: {
                y: {
                    beginAtZero: true,
                    ticks: {
                        callback: val => 'VND' + val.toLocaleString()
                    }
                },
                x: {
                    ticks: {
                        maxRotation: 0,
                        autoSkip: true
                    }
                }
            }
        }
    });
}
//
// // Event top chart
// const ctxEvents = document.getElementById('topEventsChart').getContext('2d');
// //
// let topChart;
//
// // document.addEventListener("DOMContentLoaded", () => {
// //     loadTopEvents();
// // });
//
// async function loadTopEvents() {
//     try {
//         const res = await fetch("/api/admin/top-events?limit=5");
//
//         if (!res.ok) throw new Error("API error");
//
//         const data = await res.json();
//
//         console.log("Top Events:", data);
//
//         renderChartTopEvent(data.eventNames, data.revenues);
//
//     } catch (err) {
//         console.error("Error loading top events:", err);
//     }
// }
//
// function renderChartTopEvent(labels, values) {
//     if (topChart) topChart.destroy();
//
//     topChart = new Chart(ctxEvents, {
//         type: 'bar',
//         data: {
//             labels: labels,
//             datasets: [{
//                 label: 'Revenue (VND)',
//                 data: values,
//                 backgroundColor: '#4F46E5',
//                 borderRadius: 8,
//                 barThickness: 20
//             }]
//         },
//         options: {
//             indexAxis: 'y',
//             responsive: true,
//             maintainAspectRatio: false,
//             plugins: {
//                 legend: {display: false},
//                 tooltip: {
//                     callbacks: {
//                         label: ctx =>
//                             ctx.raw.toLocaleString("vi-VN") + " VND"
//                     }
//                 }
//             },
//             scales: {
//                 x: {
//                     grid: {display: false},
//                     border: {display: false},
//                     ticks: {
//                         color: '#9CA3AF',
//                         font: {size: 10},
//                         callback: v =>
//                             v.toLocaleString("vi-VN")
//                     }
//                 },
//                 y: {
//                     grid: {display: false},
//                     border: {display: false},
//                     ticks: {
//                         color: '#111827',
//                         font: {size: 12, weight: '500'}
//                     }
//                 }
//             }
//         }
//     });
// }
//
// // customer chart
const ctxGrowth = document.getElementById('customerGrowthChart').getContext('2d');
//
let growthChart;
//
// // document.addEventListener("DOMContentLoaded", () => {
// //     loadCustomerGrowth();
// //     loadTopEvents();
// // });
//
// async function loadCustomerGrowth() {
//     try {
//         const res = await fetch("/api/admin/customer-growth");
//
//         if (!res.ok) throw new Error("API error");
//
//         const data = await res.json();
//
//         console.log("Growth:", data);
//
//         renderChartCustomer(data.labels, data.newUsers, data.returnUsers);
//
//     } catch (err) {
//         console.error(err);
//     }
// }
//
function renderChartCustomer(labels, newUsers, returningUsers) {

    if (growthChart) growthChart.destroy();

    growthChart = new Chart(ctxGrowth, {
        type: 'line',
        data: {
            labels: labels,
            datasets: [
                {
                    label: 'New',
                    data: newUsers,
                    borderColor: '#4F46E5',
                    backgroundColor: 'transparent',
                    tension: 0.4,
                    borderWidth: 2,
                    pointRadius: 3
                },
                {
                    label: 'Returning',
                    data: returningUsers,
                    borderColor: '#C7D2FE',
                    backgroundColor: 'transparent',
                    tension: 0.4,
                    borderWidth: 2,
                    pointRadius: 3
                }
            ]
        },
        options: {
            responsive: true,
            maintainAspectRatio: false,
            plugins: {
                legend: {display: false},
                tooltip: {
                    callbacks: {
                        label: ctx => ctx.dataset.label + ": " + ctx.raw
                    }
                }
            },
            scales: {
                y: {
                    grid: {color: '#F3F4F6'},
                    border: {display: false},
                    ticks: {
                        color: '#9CA3AF',
                        font: {size: 10}
                    }
                },
                x: {
                    grid: {display: false},
                    border: {display: false},
                    ticks: {
                        color: '#9CA3AF',
                        font: {size: 10}
                    }
                }
            }
        }
    });
}
//
// Category chart
//
let categoryChart;
//
// // document.addEventListener("DOMContentLoaded", () => {
// //     loadCategoryChart();
// // });
//
// async function loadCategoryChart() {
//     try {
//         showLoading();
//
//         const res = await fetch("/api/admin/event-categories");
//         if (!res.ok) throw new Error("API Error");
//
//         const data = await res.json();
//
//         if (!data || data.length === 0) {
//             showEmpty();
//             return;
//         }
//
//         // ===== Transform =====
//         const labels = data.nameCate;
//         const values = data.countByCate;
//
//         const colors = generateColors(labels.length);
//
//         // ===== Render =====
//         renderChartCate(labels, values, colors);
//         renderLegend(labels, values, colors);
//         renderReport(values);
//
//     } catch (err) {
//         console.error(err);
//         showError();
//     }
// }
//
// // =========================
// // 🎨 CHART
// // =========================
function renderChartCate(labels, values, colors) {
    const ctx = document.getElementById('categoryDonutChart').getContext('2d');

    if (categoryChart) categoryChart.destroy();

    categoryChart = new Chart(ctx, {
        type: 'doughnut',
        data: {
            labels,
            datasets: [{
                data: values,
                backgroundColor: colors,
                borderWidth: 0,
                cutout: '70%'
            }]
        },
        options: {
            responsive: true,
            maintainAspectRatio: false,

            plugins: {
                legend: { display: false },

                tooltip: {
                    callbacks: {
                        label: function(context) {
                            const total = context.dataset.data.reduce((a,b)=>a+b,0);
                            const value = context.raw;
                            const percent = ((value / total) * 100).toFixed(1);
                            return `${context.label}: ${value} (${percent}%)`;
                        }
                    }
                }
            }

            // 🔥 click vào chart
            // onClick: function(evt, elements) {
            //     if (elements.length > 0) {
            //         const index = elements[0].index;
            //         const category = labels[index];
            //
            //         console.log("Clicked:", category);
            //
            //         // 👉 Gọi filter hoặc redirect
            //         filterByCategory(category);
            //     }
            // }
        }
    });
}
//
function generateColors(n) {
    const base = [
        '#4F46E5','#6366F1','#818CF8',
        '#A5B4FC','#C7D2FE','#E0E7FF'
    ];

    return Array.from({ length: n }, (_, i) => base[i % base.length]);
}
//
function showLoading() {
    console.log("Loading...");
}

function showEmpty() {
    document.getElementById("categoryReport").innerHTML = "No data";
}

function showError() {
    document.getElementById("categoryReport").innerHTML = "Error loading data";
}
//
function renderReport(values) {
    const total = values.reduce((a,b)=>a+b,0);

    const max = Math.max(...values);
    const min = Math.min(...values);

    const container = document.getElementById("categoryReport");

    container.innerHTML = `
            <div class="text-sm text-gray-600">
                Total Events: <span class="font-bold">${total}</span>
            </div>
            <div class="text-sm text-gray-600">
                Highest: <span class="font-bold">${max}</span>
            </div>
            <div class="text-sm text-gray-600">
                Lowest: <span class="font-bold">${min}</span>
            </div>
        `;
}
//
function renderLegend(labels, values, colors) {
    const container = document.getElementById("categoryLegend");
    container.innerHTML = "";

    const total = values.reduce((a,b)=>a+b,0);

    labels.forEach((label, i) => {
        const percent = ((values[i] / total) * 100).toFixed(1);

        container.innerHTML += `
                <div class="flex items-center text-xs text-gray-600">
                    <span class="w-2 h-2 rounded-full mr-2" style="background:${colors[i]}"></span>
                    ${label} (${percent}%)
                </div>
            `;
    });
}
//
// document.addEventListener("DOMContentLoaded", () => {
//     setDefaultDate();
//     loadRevenueChart();
//
//     loadTopEvents();
//
//     loadCustomerGrowth();
//
//     loadCategoryChart();
//
// });

async function loadDashboard() {
    try {
        const from = document.getElementById("fromDate").value;
        const to = document.getElementById("toDate").value;

        const res = await fetch(`/api/admin/dashboard?from=${from}&to=${to}`);
        if (!res.ok) throw new Error("API Error");

        const data = await res.json();

        // ===== 1. KPI =====
        updateKPI(data.kpi);

        // ===== 2. Revenue Chart =====
        renderRevenueChart(data.date, data.revenue);

        // ===== 3. Top Events =====
        // renderChartTopEvent(data.eventNames, data.revenues);

        // ===== 4. Customer Growth =====
        renderChartCustomer(
            data.labels,
            data.newUsers,
            data.returnUsers
        );

        // ===== 5. Category =====
        const labels = data.nameCate;
        const values = data.countByCate;
        const colors = generateColors(labels.length);

        renderChartCate(labels, values, colors);
        renderLegend(labels, values, colors);
        renderReport(values);

        // ===== 6. Transactions =====
        // updateTransactions(data.transactions);

    } catch (err) {
        console.error(err);
    }
}

// function updateKPI(kpi) {
//     document.getElementById("revenue").innerText =
//         kpi.totalRevenue.toLocaleString() + " VND";
//
//     document.getElementById("growth").innerText =
//         kpi.revenueGrowth + "%";
//
//     document.getElementById("lastMonth").innerText =
//         kpi.lastMonthRevenue.toLocaleString() + " VND";
//
//     document.getElementById("events").innerText = kpi.totalEvents;
//     document.getElementById("customers").innerText = kpi.totalUsers;
//     document.getElementById("tickets").innerText = kpi.ticketsThisMonth;
// }

function updateKPI(data, from, to) {
    // ===== Revenue =====
    document.getElementById("revenue").innerText =
        data.totalRevenue.toLocaleString() + " VND";

    // document.getElementById("revenueGrowth").innerText =
    //     data.revenueGrowth.toFixed(2) + "%";

    // document.getElementById("lastRevenue").innerText =
    //     data.lastYearRevenue.toLocaleString() + " VND";

    // ===== Events =====
    document.getElementById("events").innerText = data.totalEvents;
    // document.getElementById("eventGrowth").innerText =
    //     data.eventGrowth.toFixed(2) + "%";
    // document.getElementById("lastEvents").innerText =
    //     data.lastMonthEvents + " events";

    // ===== Customers =====
    document.getElementById("customers").innerText = data.totalUsers;
    document.getElementById("userGrowth").innerText =
        data.userGrowth.toFixed(2) + "%";
    document.getElementById("newUsers").innerText = data.newUsers;

    // ===== Tickets =====
    document.getElementById("tickets").innerText = data.ticketsThisMonth;
    // document.getElementById("ticketGrowth").innerText =
    //     data.ticketGrowth.toFixed(2) + "%";
    // document.getElementById("lastTickets").innerText =
    //     data.lastMonthTickets + " tickets";

    // ===== Date =====
    // document.getElementById("dateRange").innerText = `${from} - ${to}`;
    // document.getElementById("dateRange2").innerText = `${from} - ${to}`;
    // document.getElementById("dateRange3").innerText = `${from} - ${to}`;
}

function updateTransactions(transactions) {
    const tbody = document.querySelector("tbody");
    tbody.innerHTML = "";

    transactions.forEach(t => {
        tbody.innerHTML += `
            <tr>
                <td>${t.customerName}</td>
                <td>${t.orderStatus}</td>
                <td>${t.totalAmount.toLocaleString()} VND</td>
                <td>${t.orderDate}</td>
            </tr>
        `;
    });
}

document.addEventListener("DOMContentLoaded", () => {
    setDefaultDate();
    loadDashboard();
});


function openModal() {
    const modal = document.getElementById("transactionModal");
    modal.classList.remove("hidden");
    modal.classList.add("flex");
}

function closeModal() {
    const modal = document.getElementById("transactionModal");
    modal.classList.add("hidden");
    modal.classList.remove("flex");
}

// Click outside to close
window.onclick = function (e) {
    const modal = document.getElementById("transactionModal");
    if (e.target === modal) {
        closeModal();
    }
}