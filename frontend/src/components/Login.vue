<template>
  <div class="login-container">
    <h2>SKALA stock market</h2>
    <form @submit.prevent="handleLogin">
      <div class="form-row">
        <div class="form-inline">
          <label for="userId">아이디</label>
          <input v-model="userId" id="userId" type="text" required />
        </div>
      </div>
      <div>
        <div class="form-inline">
          <label for="password">비밀번호</label>
          <input v-model="password" id="password" type="password" required />
        </div>
      </div>
      
      <div class="form-row">
        <div></div>
        <label>관리자 모드:</label>
        <div>
          <input type="radio" id="adminTrue" :value="true" v-model="isAdmin" />
          <label for="adminTrue">예</label>
          <input type="radio" id="adminFalse" :value="false" v-model="isAdmin" />
          <label for="adminFalse">아니오</label>
        </div>
      </div>

      <div class="form-item">
        <button type="submit">로그인</button>
      </div>
    </form>

    <div class="help-links">
      <a href="#">아이디 찾기</a> | <a href="#">비밀번호 찾기</a>
    </div>
  </div>
</template>

<script>
export default {
  data() {
    return {
      userId: '',
      password: '',
      isAdmin: false
    };
  },
  methods: {
    async handleLogin() {
      const payload = {
        userId: this.userId,
        password: this.password,
        isAdmin: this.isAdmin
      };

      try {
        const response = await fetch('http://localhost:8080/api/login', {
          method: 'POST',
          headers: { 'Content-Type': 'application/json' },
          body: JSON.stringify(payload)
        });
        const result = await response.json();
        alert(result.message);
        this.$router.push('/main');
      } catch (error) {
        alert('로그인 중 오류가 발생했습니다.');
      }
    }
  }
};
</script>

<style scoped>
.login-container {
  max-width: 500px;
  margin: auto;
  padding: 1rem;
  border: 1px solid #ccc;
  border-radius: 8px;
}

.form-row {
  display: flex;
  gap: 1rem;
  margin-bottom: 1rem;
}

.form-inline {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  flex: 1;
}

.form-item {
  margin-bottom: 1rem;
  display: flex;
  flex-direction: column;
}

input[type="text"],
input[type="password"] {
  flex: 1;
  padding: 0.5rem;
  box-sizing: border-box;
}

button {
  padding: 0.5rem 1rem;
}
</style>
