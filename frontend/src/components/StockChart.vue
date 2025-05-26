<template>
    <div class="stock-chart">
      <h2>📊 실시간 주가 변동 리포트</h2>
      <line-chart :chart-data="chartData" :chart-options="chartOptions" />
    </div>
  </template>
  
  <script>
  import { Line } from 'vue-chartjs'
  import {
    Chart as ChartJS,
    Title,
    Tooltip,
    Legend,
    LineElement,
    PointElement,
    CategoryScale,
    LinearScale
  } from 'chart.js'
  
  ChartJS.register(Title, Tooltip, Legend, LineElement, PointElement, CategoryScale, LinearScale)
  
  export default {
    name: 'StockChart',
    components: {
      LineChart: Line
    },
    props: ['history'],
    computed: {
      chartData() {
        const labels = this.history.map(item => item.time)
        const datasets = ['삼성전자', '카카오', '네이버'].map((stockName, idx) => {
          return {
            label: stockName,
            data: this.history.map(h => h.prices[idx].price),
            fill: false,
            borderColor: ['red', 'blue', 'green'][idx],
            tension: 0.1
          }
        })
        return { labels, datasets }
      },
      chartOptions() {
        return {
          responsive: true,
          plugins: { legend: { position: 'top' } },
          scales: { y: { beginAtZero: false } }
        }
      }
    }
  }
  </script>
  
  <style scoped>
  .stock-chart {
    margin-top: 3rem;
    padding: 1rem;
    background-color: #f5f5f5;
    border-radius: 12px;
  }
  </style>