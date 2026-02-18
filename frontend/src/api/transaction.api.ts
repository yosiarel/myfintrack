import axios from './axios'
import type { ApiResponse } from './category.api'

export interface Transaction {
  id: number
  categoryId: number
  categoryName: string
  categoryColor: string
  type: 'INCOME' | 'EXPENSE'
  amount: number
  description: string
  transactionDate: string
  createdAt: string
  updatedAt: string
}

export interface TransactionRequest {
  categoryId: number
  amount: number
  description?: string
  transactionDate: string
  type?: 'INCOME' | 'EXPENSE' 
}

export interface TransactionFilters {
  page?: number
  size?: number
  type?: 'INCOME' | 'EXPENSE'
  categoryId?: number
  startDate?: string
  endDate?: string
}

export interface PagedResponse<T> {
  content: T[]
  totalElements: number
  totalPages: number
  size: number
  number: number
}

export const transactionApi = {
  getAll(filters?: TransactionFilters): Promise<ApiResponse<PagedResponse<Transaction>>> {
    return axios.get('/api/transactions', { params: filters }).then((res) => res.data)
  },

  getById(id: number): Promise<ApiResponse<Transaction>> {
    return axios.get(`/api/transactions/${id}`).then((res) => res.data)
  },


  create(data: TransactionRequest): Promise<ApiResponse<Transaction>> {
    return axios.post('/api/transactions', data).then((res) => res.data)
  },


  update(id: number, data: TransactionRequest): Promise<ApiResponse<Transaction>> {
    return axios.put(`/api/transactions/${id}`, data).then((res) => res.data)
  },

  delete(id: number): Promise<ApiResponse<null>> {
    return axios.delete(`/api/transactions/${id}`).then((res) => res.data)
  },

  deletePermanently(id: number): Promise<ApiResponse<null>> {
    return axios.delete(`/api/transactions/${id}/permanent`).then((res) => res.data)
  },

  getDeleted(page = 0, size = 10): Promise<ApiResponse<PagedResponse<Transaction>>> {
    return axios.get('/api/transactions/deleted', { params: { page, size } }).then((res) => res.data)
  },


  restore(id: number): Promise<ApiResponse<Transaction>> {
    return axios.put(`/api/transactions/${id}/restore`).then((res) => res.data)
  },
}
