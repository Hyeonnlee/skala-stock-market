<template>
    <div class="portfolio">
      <h2>👤 사용자 정보</h2>
      <button @click="logout" class="logout-button">로그아웃</button>
      <p><strong>보유 현금:</strong> {{ balance.toLocaleString() }}₩</p>
      <div v-if="Object.keys(portfolio).length">
        <h3>📦 보유 주식</h3>
        <table>
          <thead>
            <tr>
              <th>종목명</th>
              <th>수량</th>
              <th>현재가</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="(stock, name) in portfolio" :key="name">
              <td>{{ name }}</td>
              <td>{{ stock.quantity }}</td>
              <td>{{ stock.currentPrice.toLocaleString() }}₩</td>
            </tr>
          </tbody>
        </table>
      </div>
      <div v-else>
        <p>보유한 주식이 없습니다.</p>
      </div>
    </div>
  </template>
  
  <script>
  export default {
    name: 'UserPortfolio',
    props: ['portfolio', 'balance'],
    methods: {
      logout() {
        localStorage.removeItem('user')
        this.$router.push('/')
      }
    }
  }
  </script>
  
  <style scoped>
  .portfolio {
    width: 45%;
    padding: 1rem;
    background-color: #eef;
    border-radius: 12px;
    text-align: left;
  }
  .logout-button {
    float: right;
    margin-bottom: 1rem;
    padding: 0.5rem 1rem;
    background: #ff4d4d;
    border: none;
    color: white;
    border-radius: 6px;
    cursor: pointer;
  }
  h2 {
    text-align: center;
  }
  table {
    width: 100%;
    border-collapse: collapse;
    margin-top: 1rem;
  }
  th, td {
    border: 1px solid #aaa;
    padding: 0.5rem;
    text-align: center;
  }
  th {
    background-color: #ddd;
  }
  </style>
  