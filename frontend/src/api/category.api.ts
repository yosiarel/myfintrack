import axios from './axios'

export interface Category {
  id: number
  name: string
  type: 'INCOME' | 'EXPENSE'
  color: string
}

export interface ApiResponse<T> {
  success: boolean
  message: string
  data: T
  timestamp: string
}

export const categoryApi = {
  getAll(type?: 'INCOME' | 'EXPENSE'): Promise<ApiResponse<Category[]>> {
    const params = type ? { type } : {}
    return axios.get('/api/categories', { params }).then((res) => res.data)
  },
}
