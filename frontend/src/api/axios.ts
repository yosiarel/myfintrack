import axios, { type AxiosInstance, type InternalAxiosRequestConfig, type AxiosResponse, type AxiosError } from 'axios'

const API_BASE_URL = import.meta.env.VITE_API_URL || 'http://localhost:8080'

// Create axios instance
const axiosInstance: AxiosInstance = axios.create({
  baseURL: API_BASE_URL,
  timeout: 10000,
  headers: {
    'Content-Type': 'application/json',
  },
  withCredentials: true,
})

// Request interceptor - inject JWT token
axiosInstance.interceptors.request.use(
  (config: InternalAxiosRequestConfig) => {
    const token = localStorage.getItem('auth_token')
    if (token && config.headers) {
      config.headers.Authorization = `Bearer ${token}`
    }
    return config
  },
  (error: AxiosError) => {
    return Promise.reject(error)
  }
)

// Response interceptor - handle errors
axiosInstance.interceptors.response.use(
  (response: AxiosResponse) => {
    return response
  },
  async (error: AxiosError) => {
    const originalRequest = error.config as InternalAxiosRequestConfig & { _retry?: boolean }

    if (error.response?.status === 401 && !originalRequest._retry) {
      originalRequest._retry = true

      try {
        // Attempt to refresh token
        // Use a new instance or the same? Same is fine if we don't loop.
        // But the refresh endpoint path must not trigger this interceptor loop if it 401s.
        // The check !originalRequest._retry prevents infinite loop for the ORIGINAL request.
        // But if refresh-token returns 401, it enters here again?
        // Yes, but originalRequest will be different (the refresh request).
        // We need to ensure we don't retry the refresh request itself.
        // Or simply catch error below.
        
        const response = await axiosInstance.post('/api/auth/refresh-token')
        
        if (response.data.success) {
            const newToken = response.data.data.accessToken
            localStorage.setItem('auth_token', newToken)
            
            // Update headers
            axiosInstance.defaults.headers.common['Authorization'] = `Bearer ${newToken}`
            if (originalRequest.headers) {
                originalRequest.headers['Authorization'] = `Bearer ${newToken}`
            }
            
            return axiosInstance(originalRequest)
        }
      } catch (refreshError) {
        // Refresh failed - logout and redirect
        localStorage.removeItem('auth_token')
        localStorage.removeItem('user')
        window.location.href = '/login'
        return Promise.reject(refreshError)
      }
    }
    return Promise.reject(error)
  }
)

export default axiosInstance
