import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import { authApi, type LoginRequest, type RegisterRequest } from '@/api/auth.api'

interface User {
  userId: number
  email: string
  fullName: string
}

export const useAuthStore = defineStore('auth', () => {
  const token = ref<string | null>(localStorage.getItem('auth_token'))
  const user = ref<User | null>(JSON.parse(localStorage.getItem('user') || 'null'))
  const loading = ref(false)
  const error = ref<string | null>(null)
  const isAuthenticated = computed(() => !!token.value)

  async function login(credentials: LoginRequest) {
    loading.value = true
    error.value = null

    try {
      const response = await authApi.login(credentials)
      
      if (response.success) {
        token.value = response.data.accessToken
        user.value = {
          userId: response.data.userId,
          email: response.data.email,
          fullName: response.data.fullName,
        }

        localStorage.setItem('auth_token', response.data.accessToken)
        localStorage.setItem('user', JSON.stringify(user.value))

        return true
      }
      return false
    } catch (err: any) {
      error.value = err.response?.data?.message || 'Login failed'
      throw err
    } finally {
      loading.value = false
    }
  }

  async function register(data: RegisterRequest) {
    loading.value = true
    error.value = null

    try {
      const response = await authApi.register(data)
      
      if (response.success) {
        // Auto-login after register
        token.value = response.data.accessToken
        user.value = {
          userId: response.data.userId,
          email: response.data.email,
          fullName: response.data.fullName,
        }

        localStorage.setItem('auth_token', response.data.accessToken)
        localStorage.setItem('user', JSON.stringify(user.value))

        return true
      }
      return false
    } catch (err: any) {
      error.value = err.response?.data?.message || 'Registration failed'
      throw err
    } finally {
      loading.value = false
    }
  }

  function logout() {
    token.value = null
    user.value = null
    localStorage.removeItem('auth_token')
    localStorage.removeItem('user')
  }

  function checkAuth() {
    const storedToken = localStorage.getItem('auth_token')
    const storedUser = localStorage.getItem('user')

    if (storedToken && storedUser) {
      token.value = storedToken
      user.value = JSON.parse(storedUser)
      return true
    }
    return false
  }

  return {
    token,
    user,
    loading,
    error,
    // Getters
    isAuthenticated,
    // Actions
    login,
    register,
    logout,
    checkAuth,
  }
})
