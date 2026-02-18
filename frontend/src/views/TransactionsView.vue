<template>
  <div class="min-h-screen p-6 md:p-8">
    <!-- Header -->
    <div class="flex flex-col md:flex-row md:items-center justify-between mb-8 gap-4">
      <div>
        <h1 class="text-3xl md:text-4xl font-bold text-white mb-2">Riwayat Transaksi</h1>
        <p class="text-white/70">Lihat seluruh riwayat pemasukan, pengeluaran, dan sampah</p>
      </div>
    </div>

    <!-- Filter Bar -->
    <div class="glass-card mb-6 p-4">
      <div class="flex flex-col md:flex-row gap-4 items-stretch md:items-center">
        <!-- Type filter -->
        <div class="flex gap-2 overflow-x-auto pb-2 md:pb-0 no-scrollbar">
          <button
            v-for="tab in typeTabs"
            :key="tab.value"
            @click="selectTab(tab.value)"
            class="px-4 py-2 rounded-xl text-sm font-medium transition-all duration-200 border whitespace-nowrap flex-shrink-0"
            :class="filterType === tab.value
              ? tab.value === 'EXPENSE'
                ? 'bg-expense-500/30 border-expense-400 text-expense-300'
                : tab.value === 'INCOME'
                  ? 'bg-income-500/30 border-income-400 text-income-300'
                  : tab.value === 'TRASH'
                    ? 'bg-gray-500/30 border-gray-400 text-gray-300'
                    : 'bg-primary-500/30 border-primary-400 text-primary-300'
              : 'glass border-white/10 text-white/60 hover:text-white'"
          >
            {{ tab.label }}
          </button>
        </div>

        <div class="flex-1 hidden md:block"></div>

        <!-- Month/Year filter -->
        <div class="flex gap-3">
          <select v-model="filterMonth" @change="fetchTransactions" class="glass-select flex-1 md:flex-none">
            <option value="" class="bg-gray-900 text-white">Semua Bulan</option>
            <option v-for="m in months" :key="m.value" :value="m.value" class="bg-gray-900 text-white">{{ m.label }}</option>
          </select>
          <select v-model="filterYear" @change="fetchTransactions" class="glass-select w-24 md:w-auto">
            <option value="" class="bg-gray-900 text-white">Semua Tahun</option>
            <option v-for="y in years" :key="y" :value="y" class="bg-gray-900 text-white">{{ y }}</option>
          </select>
        </div>
      </div>
    </div>

    <!-- Summary mini cards -->

    <!-- Loading Skeleton -->
    <div v-if="loading" class="space-y-3">
      <div v-for="i in 5" :key="i" class="glass-card p-3 md:p-4 flex items-center gap-4 animate-pulse">
        <div class="w-10 h-10 md:w-11 md:h-11 rounded-xl bg-white/10 flex-shrink-0"></div>
        <div class="flex-1">
          <div class="h-4 bg-white/10 rounded w-1/3 mb-2"></div>
          <div class="h-3 bg-white/5 rounded w-1/4"></div>
        </div>
        <div class="h-5 bg-white/10 rounded w-20"></div>
      </div>
    </div>

    <!-- Empty -->
    <div v-else-if="transactions.length === 0" class="glass-card text-center py-16">
      <div class="text-5xl mb-4">{{ filterType === 'TRASH' ? '🗑️' : '💳' }}</div>
      <p class="text-white/60 text-lg font-medium">{{ filterType === 'TRASH' ? 'Sampah kosong' : 'Belum ada transaksi' }}</p>
      <p class="text-white/40 text-sm mt-1 mb-6">{{ filterType === 'TRASH' ? 'Tidak ada transaksi yang dihapus' : 'Mulai catat pemasukan atau pengeluaran kamu' }}</p>
    </div>

    <!-- Transaction List -->
    <div v-else class="space-y-3">
      <div
        v-for="tx in transactions"
        :key="tx.id"
        class="glass-card p-3 md:p-4 flex items-start md:items-center gap-3 md:gap-4 group hover:bg-white/5 transition-all duration-200"
      >
        <!-- Type icon -->
        <div
          class="w-10 h-10 md:w-11 md:h-11 rounded-xl flex items-center justify-center flex-shrink-0 mt-0.5 md:mt-0"
          :class="tx.type === 'INCOME' ? 'bg-income-500/20' : 'bg-expense-500/20'"
        >
          <svg class="w-5 h-5" :class="tx.type === 'INCOME' ? 'text-income-400' : 'text-expense-400'" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path v-if="tx.type === 'INCOME'" stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M7 11l5-5m0 0l5 5m-5-5v12" />
            <path v-else stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M17 13l-5 5m0 0l-5-5m5 5V6" />
          </svg>
        </div>

        <!-- Info -->
        <div class="flex-1 min-w-0">
          <div class="flex flex-wrap items-center gap-2 mb-0.5">
            <p class="text-white font-semibold truncate text-sm md:text-base">{{ tx.description || tx.categoryName }}</p>
            <span
              class="text-[10px] md:text-xs px-2 py-0.5 rounded-full flex-shrink-0"
              :style="{ backgroundColor: (tx.categoryColor || '#6366f1') + '33', color: tx.categoryColor || '#6366f1' }"
            >
              {{ tx.categoryName }}
            </span>
          </div>
          <p class="text-white/40 text-xs md:text-sm">{{ formatDate(tx.transactionDate) }}</p>
        </div>

        <!-- Right Side: Amount & Actions -->
        <div class="flex flex-col md:flex-row items-end md:items-center gap-1 md:gap-4 flex-shrink-0">
          <!-- Amount -->
          <div class="text-right">
            <p class="text-sm md:text-lg font-bold whitespace-nowrap" :class="tx.type === 'INCOME' ? 'text-income-400' : 'text-expense-400'">
              {{ tx.type === 'INCOME' ? '+' : '-' }}Rp {{ formatCurrency(tx.amount) }}
            </p>
          </div>

          <!-- Actions -->
          <div class="flex gap-1 md:opacity-0 group-hover:md:opacity-100 transition-opacity duration-200">
            <template v-if="filterType === 'TRASH'">
              <button @click="restoreTransaction(tx)" class="p-1.5 md:p-2 rounded-lg hover:bg-white/10 text-white/60 hover:text-green-400 transition-colors" title="Pulihkan">
                <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M4 4v5h.582m15.356 2A8.001 8.001 0 004.582 9m0 0H9m11 11v-5h-.581m0 0a8.003 8.003 0 01-15.357-2m15.357 2H15" />
                </svg>
              </button>
              <button @click="confirmDelete(tx, true)" class="p-1.5 md:p-2 rounded-lg hover:bg-expense-500/20 text-white/60 hover:text-expense-400 transition-colors" title="Hapus Permanen">
                <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M19 7l-.867 12.142A2 2 0 0116.138 21H7.862a2 2 0 01-1.995-1.858L5 7m5 4v6m4-6v6m1-10V4a1 1 0 00-1-1h-4a1 1 0 00-1 1v3M4 7h16" />
                </svg>
              </button>
            </template>
            <template v-else>
              <button @click="editTransaction(tx)" class="p-1.5 md:p-2 rounded-lg hover:bg-white/10 text-white/60 hover:text-white transition-colors">
                <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M11 5H6a2 2 0 00-2 2v11a2 2 0 002 2h11a2 2 0 002-2v-5m-1.414-9.414a2 2 0 112.828 2.828L11.828 15H9v-2.828l8.586-8.586z" />
                </svg>
              </button>
              <button @click="confirmDelete(tx)" class="p-1.5 md:p-2 rounded-lg hover:bg-expense-500/20 text-white/60 hover:text-expense-400 transition-colors">
                <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M19 7l-.867 12.142A2 2 0 0116.138 21H7.862a2 2 0 01-1.995-1.858L5 7m5 4v6m4-6v6m1-10V4a1 1 0 00-1-1h-4a1 1 0 00-1 1v3M4 7h16" />
                </svg>
              </button>
            </template>
          </div>
        </div>
      </div>

      <!-- Pagination -->
      <div class="flex justify-center gap-2 pt-4">
        <button
          @click="currentPage--; fetchTransactions()"
          :disabled="currentPage === 0"
          class="px-3 py-1.5 rounded-lg glass text-sm font-medium text-white/60 hover:text-white disabled:opacity-50 disabled:cursor-not-allowed transition-all"
        >
          &larr; Prev
        </button>
        <div class="flex items-center px-4 bg-white/5 rounded-lg border border-white/5 text-sm font-medium text-white">
          {{ currentPage + 1 }} / {{ totalPages }}
        </div>
        <button
          @click="currentPage++; fetchTransactions()"
          :disabled="currentPage >= totalPages - 1"
          class="px-3 py-1.5 rounded-lg glass text-sm font-medium text-white/60 hover:text-white disabled:opacity-50 disabled:cursor-not-allowed transition-all"
        >
          Next &rarr;
        </button>
      </div>
    </div>

    <!-- Create/Edit Modal -->
    <BaseModal
      v-model="showModal"
      :title="editingId ? 'Edit Transaksi' : 'Catat Transaksi'"
      @close="resetForm"
    >
      <form @submit.prevent="submitTransaction" class="space-y-4">
        <!-- Type Selector -->
        <div>
          <label class="block text-sm font-medium text-white/80 mb-2">Tipe</label>
          <div class="flex gap-3">
            <button
              type="button"
              @click="form.type = 'EXPENSE'; form.categoryId = ''"
              :class="[
                'flex-1 py-2.5 px-4 rounded-xl font-medium transition-all duration-200 border',
                form.type === 'EXPENSE'
                  ? 'bg-expense-500/30 border-expense-400 text-expense-300'
                  : 'glass border-white/10 text-white/60 hover:text-white'
              ]"
            >
              💸 Pengeluaran
            </button>
            <button
              type="button"
              @click="form.type = 'INCOME'; form.categoryId = ''"
              :class="[
                'flex-1 py-2.5 px-4 rounded-xl font-medium transition-all duration-200 border',
                form.type === 'INCOME'
                  ? 'bg-income-500/30 border-income-400 text-income-300'
                  : 'glass border-white/10 text-white/60 hover:text-white'
              ]"
            >
              💰 Pemasukan
            </button>
          </div>
        </div>

        <!-- Category -->
        <BaseSelect
          v-model="form.categoryId"
          label="Kategori"
          placeholder="Pilih kategori..."
          :options="categoryOptions"
          :error="errors.categoryId"
          required
        />

        <!-- Amount -->
        <BaseInput
          v-model="form.amount"
          label="Jumlah"
          type="number"
          placeholder="Contoh: 150000"
          :error="errors.amount"
          required
        />

        <!-- Date -->
        <BaseDatePicker
          :month="Number(form.month)"
          :year="Number(form.year)"
          label="Tanggal Transaksi"
          placeholder="Pilih tanggal..."
          :error="errors.date"
          required
          @update:month="form.month = $event.toString()"
          @update:year="form.year = $event.toString()"
          @update:date="form.date = $event"
        />

        <!-- Description -->
        <div>
          <label class="block text-sm font-medium text-white/80 mb-2">Catatan <span class="text-white/40">(opsional)</span></label>
          <textarea
            v-model="form.description"
            rows="2"
            placeholder="Contoh: Makan siang di kantor"
            class="glass-input w-full resize-none"
          ></textarea>
        </div>

        <div class="flex gap-3 pt-2">
          <BaseButton type="button" variant="secondary" class="flex-1" @click="showModal = false">Batal</BaseButton>
          <BaseButton type="submit" variant="primary" class="flex-1" :loading="submitting">
            {{ editingId ? 'Update' : 'Simpan' }}
          </BaseButton>
        </div>
      </form>
    </BaseModal>

    <!-- Delete Confirm Modal -->
    <BaseModal v-model="showDeleteModal" title="Hapus Transaksi" @close="deletingTx = null">
      <div class="text-center py-2">
        <div class="flex justify-center mb-4">
          <iframe src="https://lottie.host/embed/33a979c2-96ba-43ed-8a1a-31fb6e5c911a/037TWMCcYp.lottie" style="width: 200px; height: 200px; border: none;" loading="lazy"></iframe>
        </div>
        <p class="text-white/80 mb-1">
          {{ isPermanentDelete ? 'Hapus permanen transaksi ini?' : 'Yakin ingin menghapus transaksi ini?' }}
        </p>
        <p v-if="isPermanentDelete" class="text-expense-400 text-sm mb-2 font-bold">Tindakan ini tidak dapat dibatalkan!</p>
        <p class="text-white/50 text-sm mb-6">
          <span :class="deletingTx?.type === 'INCOME' ? 'text-income-400' : 'text-expense-400'">
            {{ deletingTx?.type === 'INCOME' ? '+' : '-' }}Rp {{ formatCurrency(deletingTx?.amount ?? 0) }}
          </span>
          · {{ deletingTx?.categoryName }}
        </p>
        <div class="flex gap-3">
          <BaseButton variant="secondary" class="flex-1" @click="showDeleteModal = false">Batal</BaseButton>
          <BaseButton variant="danger" class="flex-1" :loading="deleting" @click="handleDelete">Hapus</BaseButton>
        </div>
      </div>
    </BaseModal>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { transactionApi, type Transaction, type TransactionRequest } from '@/api/transaction.api'
import { categoryApi, type Category } from '@/api/category.api'
import { useNotification } from '@/composables/useNotification'
import { BaseButton, BaseInput, BaseSelect, BaseModal, BaseDatePicker } from '@/components/base'

const notification = useNotification()

// State
const transactions = ref<Transaction[]>([])
const categories = ref<Category[]>([])
const loading = ref(false)
const submitting = ref(false)
const deleting = ref(false)
const showModal = ref(false)
const showDeleteModal = ref(false)
const isPermanentDelete = ref(false)
const editingId = ref<number | null>(null)
const deletingTx = ref<Transaction | null>(null)
const currentPage = ref(0)
const totalPages = ref(1)

const now = new Date()
const filterType = ref<'' | 'INCOME' | 'EXPENSE' | 'TRASH'>('')
const filterMonth = ref<number | ''>('')
const filterYear = ref<number | ''>('')

const months = [
  { value: 1, label: 'Januari' }, { value: 2, label: 'Februari' }, { value: 3, label: 'Maret' },
  { value: 4, label: 'April' }, { value: 5, label: 'Mei' }, { value: 6, label: 'Juni' },
  { value: 7, label: 'Juli' }, { value: 8, label: 'Agustus' }, { value: 9, label: 'September' },
  { value: 10, label: 'Oktober' }, { value: 11, label: 'November' }, { value: 12, label: 'Desember' },
]
const years = Array.from({ length: 6 }, (_, i) => now.getFullYear() - i) // Current + 5 years back

const typeTabs = [
  { value: '' as const, label: '📋 Semua' },
  { value: 'EXPENSE' as const, label: '💸 Pengeluaran' },
  { value: 'INCOME' as const, label: '💰 Pemasukan' },
  { value: 'TRASH' as const, label: '🗑️ Sampah' },
]

// Form
const form = ref({
  type: 'EXPENSE' as 'INCOME' | 'EXPENSE',
  categoryId: '',
  amount: '',
  date: null as string | null,
  month: (now.getMonth() + 1).toString(),
  year: now.getFullYear().toString(),
  description: '',
})
const errors = ref({ categoryId: '', amount: '', date: '' })

// Computed
const categoryOptions = computed(() =>
  categories.value
    .filter(c => c.type === form.value.type)
    .map(c => ({ value: c.id.toString(), label: c.name }))
)


// Helpers
const formatCurrency = (v: number) => new Intl.NumberFormat('id-ID').format(Math.round(v ?? 0))
const formatDate = (d: string) => {
  const date = new Date(d)
  return date.toLocaleDateString('id-ID', { day: 'numeric', month: 'long', year: 'numeric' })
}

// API
const fetchTransactions = async () => {
  loading.value = true
  try {
    let startDate: string | undefined
    let endDate: string | undefined

    if (filterYear.value) {
       if (filterMonth.value) {
          // Specific Month
          startDate = `${filterYear.value}-${String(filterMonth.value).padStart(2, '0')}-01`
          endDate = `${filterYear.value}-${String(filterMonth.value).padStart(2, '0')}-${new Date(Number(filterYear.value), Number(filterMonth.value), 0).getDate()}`
       } else {
          // Full Year
          startDate = `${filterYear.value}-01-01`
          endDate = `${filterYear.value}-12-31`
       }
    }

    let res;
    if (filterType.value === 'TRASH') {
      res = await transactionApi.getDeleted(currentPage.value, 10)
    } else {
      res = await transactionApi.getAll({
        type: filterType.value as 'INCOME' | 'EXPENSE' | undefined,
        startDate,
        endDate,
        page: currentPage.value,
        size: 10,
      })
    }
    transactions.value = res.data.content
    totalPages.value = res.data.totalPages
  } catch (e: any) {
    notification.showError('Gagal memuat transaksi')
  } finally {
    loading.value = false
  }
}

const selectTab = (value: '' | 'INCOME' | 'EXPENSE' | 'TRASH') => {
  filterType.value = value as any
  currentPage.value = 0
  fetchTransactions()
}

const restoreTransaction = async (tx: Transaction) => {
  try {
    await transactionApi.restore(tx.id)
    notification.showSuccess('Transaksi dipulihkan')
    fetchTransactions()
  } catch {
    notification.showError('Gagal memulihkan transaksi')
  }
}

const fetchCategories = async () => {
  try {
    const res = await categoryApi.getAll()
    categories.value = res.data
  } catch {}
}


const validateForm = () => {
  errors.value = { categoryId: '', amount: '', date: '' }
  let valid = true
  if (!form.value.categoryId) { errors.value.categoryId = 'Pilih kategori'; valid = false }
  if (!form.value.amount || Number(form.value.amount) <= 0) { errors.value.amount = 'Jumlah harus lebih dari 0'; valid = false }
  if (!form.value.date) { errors.value.date = 'Pilih tanggal'; valid = false }
  return valid
}

const submitTransaction = async () => {
  if (!validateForm()) return
  submitting.value = true
  try {
    const payload: TransactionRequest = {
      categoryId: Number(form.value.categoryId),
      amount: Number(form.value.amount),
      transactionDate: form.value.date!,
      description: form.value.description || undefined,
      type: form.value.type,
    }
    if (editingId.value) {
      await transactionApi.update(editingId.value, payload)
      notification.showSuccess('Transaksi berhasil diupdate')
    } else {
      await transactionApi.create(payload)
      notification.showSuccess('Transaksi berhasil dicatat')
    }
    showModal.value = false
    resetForm()
    fetchTransactions()
  } catch (e: any) {
    notification.showError(e.response?.data?.message || 'Gagal menyimpan transaksi')
  } finally {
    submitting.value = false
  }
}


const editTransaction = (tx: Transaction) => {
  editingId.value = tx.id
  const d = new Date(tx.transactionDate)
  form.value = {
    type: tx.type,
    categoryId: tx.categoryId.toString(),
    amount: tx.amount.toString(),
    date: tx.transactionDate,
    month: (d.getMonth() + 1).toString(),
    year: d.getFullYear().toString(),
    description: tx.description || '',
  }
  showModal.value = true
}

const confirmDelete = (tx: Transaction, permanent = false) => {
  deletingTx.value = tx
  isPermanentDelete.value = permanent
  showDeleteModal.value = true
}

const handleDelete = async () => {
  if (!deletingTx.value) return
  deleting.value = true
  try {
    if (isPermanentDelete.value) {
      await transactionApi.deletePermanently(deletingTx.value.id)
      notification.showSuccess('Transaksi dihapus permanen')
    } else {
      await transactionApi.delete(deletingTx.value.id)
      notification.showSuccess('Transaksi berhasil dihapus')
    }
    showDeleteModal.value = false
    deletingTx.value = null
    fetchTransactions()
  } catch {
    notification.showError('Gagal menghapus transaksi')
  } finally {
    deleting.value = false
  }
}

const resetForm = () => {
  editingId.value = null
  form.value = {
    type: 'EXPENSE',
    categoryId: '',
    amount: '',
    date: null,
    month: (now.getMonth() + 1).toString(),
    year: now.getFullYear().toString(),
    description: '',
  }
  errors.value = { categoryId: '', amount: '', date: '' }
}

onMounted(() => {
  fetchTransactions()
  fetchCategories()
})
</script>
