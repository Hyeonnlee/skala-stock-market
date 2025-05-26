<template>
  <div class="stock-list">
    <h2>📃 전체 주식 목록</h2>

    <div v-for="stock in stocks" :key="stock.id" class="stock-card">
      <div class="stock-info">
        <strong>{{ stock.name }}</strong>
        <p>{{ stock.price.toLocaleString() }}₩</p>
      </div>
      <div class="stock-actions">
        <button @click="$emit('buy', stock.name)">매수</button>
        <button @click="$emit('sell', stock.name)">매도</button>
      </div>
    </div>

    <div class="add-stock">
      <input v-model="newStock.stockName" placeholder="주식 이름" />
      <input v-model.number="newStock.price" placeholder="가격" type="number" />
      <button @click="addStock">주식 추가</button>
    </div>
  </div>
</template>

<script>
import axios from 'axios'

export default {
  name: 'StockList',
  props: {
    stocks: {
      type: Array,
      required: true
    }
  },
  data() {
    return {
      newStock: {
        id: 0,
        stockName: '',
        price: 0
      }
    }
  },
  methods: {
    async fetchStocks() {
      try {
        const response = await axios.get('/api/stocks/list');
        this.stocks = (response.data.data || []).map(item => ({
          id: item.id,
          name: item.stockName,  // stockName → name으로 매핑
          price: item.price
        }));
      } catch (error) {
        console.error("주식 목록 불러오기 실패:", error);
      }
    },
    async addStock() {
      if (!this.newStock.stockName || this.newStock.price <= 0) {
        alert("올바른 값을 입력하세요.");
        return;
      }
      try {
        // 새 주식 추가 API 호출
        const response = await axios.post('/api/stocks', this.newStock);
        this.newStock.stockName = '';
        this.newStock.price = 0;

        // 새로 추가된 주식만 현재 stock 배열에 추가
        const addedStock = response.data.data;
        this.$emit('addNewStock', addedStock);
      } catch (error) {
        console.error("주식 추가 실패:", error);
      }
    }
  }
}
</script>

<style scoped>
.stock-card {
  display: flex;
  justify-content: space-between; /* 양쪽 정렬 */
  align-items: center;            /* 세로 가운데 정렬 */
  padding: 12px;
  border: 1px solid #ccc;
  border-radius: 8px;
  margin-bottom: 10px;
  background-color: #f9f9f9;
}

.stock-info {
  display: flex;
  flex-direction: column;
}

.stock-actions {
  display: flex;
  gap: 8px;
}

</style>