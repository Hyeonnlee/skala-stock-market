import { createRouter, createWebHistory } from 'vue-router';
import LoginView from '@/components/Login.vue';
import MainStockView from '@/views/MainStockView.vue';

const routes = [
  { path: '/', name: 'Login', component: LoginView },
  { path: '/main', name: 'MainStock', component: MainStockView }
];

const router = createRouter({
  history: createWebHistory(),
  routes,
});

export default router;
