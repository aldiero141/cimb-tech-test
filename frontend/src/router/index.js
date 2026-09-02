import { createRouter, createWebHistory } from 'vue-router'
import { useAuthStore } from '../stores/auth'
import LoginView from '../views/LoginView.vue'
import MonitoringView from '../views/MonitoringView.vue'

const routes = [
  {
    path: '/',
    redirect: () => {
      const auth = useAuthStore()
      return auth.isAuthenticated ? { name: 'monitoring' } : { name: 'login' }
    }
  },
  {
    path: '/login',
    name: 'login',
    component: LoginView
  },
  {
    path: '/monitoring',
    name: 'monitoring',
    component: MonitoringView,
    meta: { requiresAuth: true }
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

router.beforeEach((to) => {
  const auth = useAuthStore()
  if (to.meta.requiresAuth && !auth.isAuthenticated) {
    return { name: 'login' }
  }
  if (to.name === 'login' && auth.isAuthenticated) {
    return { name: 'monitoring' }
  }
  return true
})

export default router