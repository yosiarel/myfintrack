import { defineStore } from 'pinia'
import { ref } from 'vue'
import { categoryApi, type Category } from '@/api/category.api'

export const useCategoryStore = defineStore('category', () => {
  // State
  const categories = ref<Category[]>([])
  const loading = ref(false)
  const error = ref<string | null>(null)

  // Actions
  async function fetchCategories(type?: 'INCOME' | 'EXPENSE') {
    loading.value = true
    error.value = null

    try {
      const response = await categoryApi.getAll(type)
      if (response.success) {
        categories.value = response.data
      }
    } catch (err: any) {
      error.value = err.response?.data?.message || 'Failed to fetch categories'
      throw err
    } finally {
      loading.value = false
    }
  }

  function getCategoryById(id: number): Category | undefined {
    return categories.value.find((c) => c.id === id)
  }

  function getCategoriesByType(type: 'INCOME' | 'EXPENSE'): Category[] {
    return categories.value.filter((c) => c.type === type)
  }

  return {
    // State
    categories,
    loading,
    error,
    // Actions
    fetchCategories,
    getCategoryById,
    getCategoriesByType,
  }
})
