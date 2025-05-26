<template>
    <div class="p-6">
      <h2 class="text-xl font-bold mb-4">📈 주가 예측</h2>
      <input type="file" @change="handleFileChange" accept=".csv" class="mb-4" />
      <button @click="submitFile" class="bg-blue-500 text-white px-4 py-2 rounded">예측하기</button>
  
      <div v-if="imageUrl" class="mt-6">
        <h3 class="text-lg font-semibold mb-2">예측 결과</h3>
        <img :src="imageUrl" alt="예측 결과 이미지" class="border rounded shadow-md max-w-full" />
      </div>
    </div>
  </template>
  
  <script setup>
  import { ref } from 'vue'
  import axios from 'axios'
  
  const selectedFile = ref(null)
  const imageUrl = ref(null)
  
  const handleFileChange = (event) => {
    selectedFile.value = event.target.files[0]
  }
  
  const submitFile = async () => {
    if (!selectedFile.value) {
      alert('CSV 파일을 선택해주세요.')
      return
    }
  
    const formData = new FormData()
    formData.append('file', selectedFile.value)
  
    try {
      const response = await axios.post('/api/predict', formData, {
        headers: {
          'Content-Type': 'multipart/form-data'
        },
        responseType: 'blob'  // 이미지 바이너리로 받기
      })
  
      const blob = new Blob([response.data], { type: 'image/png' })
      imageUrl.value = URL.createObjectURL(blob)
    } catch (error) {
      console.error('예측 요청 실패:', error)
      alert('예측 중 오류가 발생했습니다.')
    }
  }
  </script>
  
  <style scoped>
  input[type="file"] {
    display: block;
  }
  </style>
  