<template>
  <div class="main-view">
    <h1>📈 SKALA Stock Market</h1>

    <div class="content">
      <stock-list 
        :stocks="stocks" 
        @buy="buyStock" 
        @sell="sellStock" 
        @refresh="fetchStocks" 
      />
      <user-portfolio 
        :portfolio="portfolio" 
        :balance="balance" 
      />
    </div>

    <div v-if="priceHistory.length">
      <div>
        <label v-for="stock in stocks" :key="stock.id">
          <input 
            type="radio" 
            :name="`stock-${stock.id}`" 
            :value="stock.name" 
            v-model="selectedStock" 
            @change="() => renderChart({ destroy: true })"
          />
          {{ stock.name }}
        </label>
      </div>

      <div>
        <canvas id="stockChart"></canvas>
      </div>
    </div>
    <button @click="downloadCSV">CSV 다운로드</button>

    <div class="predict-section mt-10 p-4 border rounded bg-gray-50">
      <h2 class="text-xl font-bold mb-4">📈 주가 예측</h2>
      <input type="file" @change="handleFileChange" accept=".csv" class="mb-4" />
      <button @click="uploadCSV" class="bg-blue-500 text-white px-4 py-2 rounded hover:bg-blue-600">업로드 및 예측</button>

      <div v-if="predictedImage" class="mt-6">
        <h3 class="text-lg font-semibold mb-2">예측 결과</h3>
        <img :src="predictedImage" alt="예측 결과 그래프" class="rounded shadow-md" />
      </div>

      <div v-if="reportText">
        <h3>📝 예측 보고서</h3>
        <p>{{ reportText }}</p>
      </div>
    </div>
  </div>
</template>

<script>
import axios from 'axios'
import StockList from '@/components/StockList.vue'
import UserPortfolio from '@/components/UserPortfolio.vue'
import { nextTick, render } from 'vue'
import {
  Chart,
  LineController,
  LineElement,
  PointElement,
  LinearScale,
  CategoryScale,  // 👈 이게 바로 "category" scale
  Title,
  Tooltip,
  Legend
} from 'chart.js'

Chart.register(
  LineController,
  LineElement,
  PointElement,
  LinearScale,
  CategoryScale,  // 👈 이거 꼭 포함
  Title,
  Tooltip,
  Legend
)

export default {
  components: { StockList, UserPortfolio },
  data() {
    return {
      balance: 100000,
      stocks: [],
      portfolio: {},
      priceHistory: [],
      stockUpdateInterval: null,
      selectedStock: null,
      stockPricesHistory: {},  // 각 주식의 가격 변화 저장
      chartInstance: null,
      csvFile: null,
      predictedImage: null,
      reportText: ''  // 예측 요약 + GPT 분석 보고서
    }
  },
  created() {
    this.stockPricesHistory = {}
    this.fetchStocks()
  },
  beforeUnmount() {
    clearInterval(this.stockUpdateInterval) // 메모리 누수 방지
    if (this.chartInstance) {
      this.chartInstance.destroy()  // 컴포넌트가 unmount될 때 차트 인스턴스를 정리
    }
  },
  methods: {
    async fetchStocks() {
      try {
        const response = await axios.get('/api/stocks/list')
        this.stocks = (response.data.data || []).map(item => ({
          id: item.id,
          name: item.stockName,
          price: item.price
        }))
        if (!this.stockUpdateInterval) {
          this.startStockUpdates()
        }
      } catch (error) {
        console.error("주식 목록 불러오기 실패:", error)
      }
    },
    startStockUpdates() {
      this.stockUpdateInterval = setInterval(() => {
        this.stocks = this.stocks.map(stock => {
          const diff = Math.floor(Math.random() * 101) - 50
          const newPrice = Math.max(1, stock.price + diff)
          this.trackStockPriceHistory(stock.name, newPrice)
          return { ...stock, price: newPrice }
        })
        this.updateHistory()
      }, 1000)
    },
    trackStockPriceHistory(stockName, price) {
      if (!this.stockPricesHistory[stockName]) {
        this.stockPricesHistory[stockName] = []
      }
      this.stockPricesHistory[stockName].push({
        time: new Date().toLocaleTimeString(),
        price
      })
    },
    updateHistory() {
      const snapshot = this.stocks.map(s => ({
        name: s.name,
        price: s.price
      }))
      this.priceHistory.push({
        time: new Date().toLocaleTimeString(),
        prices: snapshot
      })

      if (this.selectedStock) {
        this.renderChart() // destroy 없이, 데이터만 업데이트
      }
    },
    renderChart({ destroy = false } = {}) {
      const selectedHistory = this.stockPricesHistory[this.selectedStock]
      if (!selectedHistory || !selectedHistory.length) return
      const trimmedHistory = selectedHistory.slice(-30)
      nextTick(() => {
        const canvas = document.getElementById('stockChart')
        if (!canvas) return

        const ctx = canvas.getContext('2d')
        if (!ctx) return

        if (this.chartInstance && destroy) {
          this.chartInstance.destroy()
          this.chartInstance = null
        }

        // 기존 차트가 있다면 데이터만 갱신
        if (this.chartInstance) {
          this.chartInstance.data.labels = selectedHistory.map(item => item.time)
          this.chartInstance.data.datasets[0].data = selectedHistory.map(item => item.price)
          this.chartInstance.update()
          return
        }

        // 차트 새로 생성
        this.chartInstance = new Chart(ctx, {
          type: 'line',
          data: {
            labels: selectedHistory.map(item => item.time),
            datasets: [{
              label: this.selectedStock,
              data: selectedHistory.map(item => item.price),
              borderColor: 'rgba(75, 192, 192, 1)',
              tension: 0.1,
              fill: false,
              pointRadius: 1,
              pointHoverRadius: 4
            }]
          },
          options: {
            animation: false,
            responsive: true,
            scales: {
              x: {
                type: 'category',
                title: { display: true, text: 'Time' }
              },
              y: {
                beginAtZero: false,
                title: { display: true, text: 'Price' }
              }
            },
            plugins: {
              legend: { display: true }
            }
          }
        })
      })
    },
    buyStock(name) {
      const stock = this.stocks.find(s => s.name === name)
      if (this.balance >= stock.price) {
        this.balance -= stock.price
        if (!this.portfolio[name]) {
          this.portfolio[name] = { quantity: 0, currentPrice: stock.price }
        }
        this.portfolio[name].quantity++
        this.portfolio[name].currentPrice = stock.price
      }
    },
    sellStock(name) {
      const stock = this.stocks.find(s => s.name === name)
      const owned = this.portfolio[name]
      if (owned && owned.quantity > 0) {
        this.balance += stock.price
        owned.quantity--
        owned.currentPrice = stock.price
        if (owned.quantity === 0) delete this.portfolio[name]
      }
    },
    downloadCSV() {
      const stockData = []
      const startDate = new Date('2025-04-01') // 시작일
      const stockNames = Object.keys(this.stockPricesHistory)
      const maxLength = Math.max(...stockNames.map(name => this.stockPricesHistory[name].length))

      for (let i = 0; i < maxLength; i++) {
        const date = new Date(startDate)
        date.setDate(startDate.getDate() + i)
        const formattedDate = date.toISOString().slice(0, 10) // YYYY-MM-DD

        stockNames.forEach(stockName => {
          const record = this.stockPricesHistory[stockName][i]
          if (record) {
            const open = (record.price + Math.random() * 2).toFixed(2)
            const high = (record.price + Math.random() * 4).toFixed(2)
            const low = (record.price - Math.random() * 4).toFixed(2)
            const close = record.price.toFixed(2)
            const volume = Math.floor(Math.random() * 10000000 + 1000000)

            stockData.push(`${formattedDate},${open},${high},${low},${close},${volume},${stockName}`)
          }
        })
      }

      const csvContent = "Date,Open,High,Low,Close,Volume,Name\n" + stockData.join("\n")
      const blob = new Blob([csvContent], { type: 'text/csv;charset=utf-8;' })
      const link = document.createElement('a')
      link.href = URL.createObjectURL(blob)
      link.download = 'stock_prices.csv'
      link.click()
    },
    handleFileChange(event) {
      this.csvFile = event.target.files[0]
      this.predictedImage = null
    },
    async uploadCSV() {
      if (!this.csvFile) {
      alert('CSV 파일을 선택하세요!')
      return
      }
      const formData = new FormData()
      formData.append('file', this.csvFile)
      try {
        const response = await axios.post('/api/predict', formData, {
          headers: {
            'Content-Type': 'multipart/form-data'
          }
        })

      // 서버에서 base64로 인코딩된 이미지와 보고서를 받음
      const { image_base64, report, gpt_report } = response.data
      this.predictedImage = `data:image/png;base64,${image_base64}`
      this.reportText = `${report}\n\n${gpt_report}`
      } catch (error) {
        console.error('예측 요청 실패:', error)
        alert('예측 중 오류가 발생했습니다.')
      }
    },
  }
}
</script>

<style scoped>
.main-view {
  padding: 2rem;
  text-align: center;
}

.content {
  display: flex;
  justify-content: space-between;
  margin-top: 2rem;
}

button {
  margin-top: 2rem;
}
</style>
