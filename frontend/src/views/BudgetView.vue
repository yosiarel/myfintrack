<template>
  <div class="min-h-screen p-6 md:p-8">
    <!-- Header -->
    <div class="flex flex-col md:flex-row md:items-center justify-between mb-8 gap-4">
      <div>
        <h1 class="text-3xl md:text-4xl font-bold text-white mb-2">Catat Keuangan</h1>
        <p class="text-white/70">Kelola alokasi budget dan catat transaksi harian</p>
      </div>
      <button
        @click="openTransactionModal"
        class="flex items-center justify-center gap-2 px-5 py-3 md:py-2.5 rounded-xl font-semibold text-white transition-all duration-200 border border-white/20 hover:border-white/40 w-full md:w-auto active:scale-95 bg-white/10 backdrop-blur-md"
      >
        <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 4v16m8-8H4" />
        </svg>
        Catat Transaksi
      </button>
    </div>

    <!-- Filter Bar (Month/Year Only) -->
    <div class="glass-card mb-6 p-4 flex justify-end">
      <div class="flex gap-3 w-full md:w-auto">
        <select v-model="filterMonth" @change="fetchData" class="glass-select flex-1 md:flex-none">
          <option v-for="m in months" :key="m.value" :value="m.value" class="bg-gray-900 text-white">{{ m.label }}</option>
        </select>
        <select v-model="filterYear" @change="fetchData" class="glass-select w-24 md:w-auto">
          <option v-for="y in years" :key="y" :value="y" class="bg-gray-900 text-white">{{ y }}</option>
        </select>
      </div>
    </div>

    <!-- Summary Envelope Cards -->
    <div class="grid grid-cols-3 gap-2 md:gap-3 mb-6">
      <div class="glass-card py-3 px-1 md:px-2 text-center flex flex-col justify-center">
        <p class="text-[10px] md:text-xs text-white/50 mb-0.5 md:mb-1">Pemasukan</p>
        <p class="text-xs md:text-lg font-bold text-income-400 break-words leading-tight">+Rp {{ formatCurrency(summary.totalIncome) }}</p>
      </div>
      <div class="glass-card py-3 px-1 md:px-2 text-center flex flex-col justify-center">
        <p class="text-[10px] md:text-xs text-white/50 mb-0.5 md:mb-1">Total Budget</p>
        <!-- Total Spent includes Allocated + Unbudgeted -->
        <p class="text-xs md:text-lg font-bold text-expense-400 break-words leading-tight">-Rp {{ formatCurrency(summary.totalSpent) }}</p>
      </div>
      <div class="glass-card py-3 px-1 md:px-2 text-center flex flex-col justify-center">
        <p class="text-[10px] md:text-xs text-white/50 mb-0.5 md:mb-1">Sisa (Net)</p>
        <p class="text-xs md:text-lg font-bold break-words leading-tight" :class="summary.netBalance >= 0 ? 'text-income-400' : 'text-expense-400'">
          {{ summary.netBalance >= 0 ? '+' : '-' }}Rp {{ formatCurrency(Math.abs(summary.netBalance)) }}
        </p>
      </div>
    </div>

    <!-- Loading -->
    <div v-if="loading" class="flex justify-center py-16">
      <BaseLoading size="lg" />
    </div>

    <!-- Budget List Section -->
    <div v-else>
      <div class="flex items-center justify-between mb-4">
        <h2 class="text-xl font-bold text-white">Alokasi Budget</h2>
        <button 
          @click="openBudgetModal"
          class="text-sm text-primary-300 text-white hover:text-gray-400 transition-colors"
        >
          + Tambah Budget
        </button>
      </div>

      <!-- Empty State -->
      <div v-if="budgets.length === 0" class="glass-card text-center py-12 mb-6">
        <div class="text-4xl mb-4">✉️</div>
        <p class="text-white/60 text-lg font-medium">Belum ada budget</p>
        <p class="text-white/40 text-sm mt-1 mb-4">Buat amplop budget untuk mengatur pengeluaranmu</p>
        <button
          @click="openBudgetModal"
          class="px-4 py-2 rounded-lg bg-white/10 hover:bg-white/20 text-white font-medium transition-colors"
        >
          Buat Budget Bulan Ini
        </button>
      </div>

      <!-- Budget List -->
      <div v-else class="space-y-4 mb-8">
        <div v-for="budget in budgets" :key="budget.id" class="glass-card p-4">
          <div class="flex justify-between items-start mb-2">
            <div class="flex items-center gap-3">
              <!-- Category Icon -->
              <div 
                class="w-10 h-10 rounded-xl flex items-center justify-center font-bold text-lg"
                :style="{ backgroundColor: (budget.categoryColor || '#6366f1') + '20', color: budget.categoryColor }"
              >
                {{ budget.categoryName.charAt(0) }}
              </div>
              <div>
                <h3 class="font-bold text-white">{{ budget.categoryName }}</h3>
                <p class="text-xs text-white/50">Limit: Rp {{ formatCurrency(budget.monthlyLimit) }}</p>
              </div>
            </div>
            
            <!-- Actions -->
            <div class="flex gap-2">
              <button @click="editBudget(budget)" class="p-1.5 rounded-lg hover:bg-white/10 text-white/40 hover:text-white transition-colors">
                <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M11 5H6a2 2 0 00-2 2v11a2 2 0 002 2h11a2 2 0 002-2v-5m-1.414-9.414a2 2 0 112.828 2.828L11.828 15H9v-2.828l8.586-8.586z" /></svg>
              </button>
              <button @click="confirmDeleteBudget(budget)" class="p-1.5 rounded-lg hover:bg-expense-500/20 text-expense-400/60 hover:text-expense-400 transition-colors">
                <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M19 7l-.867 12.142A2 2 0 0116.138 21H7.862a2 2 0 01-1.995-1.858L5 7m5 4v6m4-6v6m1-10V4a1 1 0 00-1-1h-4a1 1 0 00-1 1v3M4 7h16" /></svg>
              </button>
            </div>
          </div>

          <!-- Progress Bar -->
          <div class="relative h-2 bg-white/5 rounded-full overflow-hidden mb-2">
            <div 
              class="absolute top-0 left-0 h-full transition-all duration-500 rounded-full"
              :class="getProgressColor(budget.usagePercentage)"
              :style="{ width: Math.min(budget.usagePercentage, 100) + '%' }"
            ></div>
          </div>

          <div class="flex justify-between text-sm">
            <span class="text-white/60">Terpakai Rp {{ formatCurrency(budget.spent) }}</span>
            <span :class="budget.remaining < 0 ? 'text-expense-400 font-bold' : 'text-white/60'">
              {{ budget.remaining < 0 ? 'Over: ' : 'Sisa: ' }} Rp {{ formatCurrency(Math.abs(budget.remaining)) }}
            </span>
          </div>
        </div>
      </div>

      <!-- Unbudgeted Warning -->
      <div v-if="summary.totalUnbudgetedSpent > 0" class="glass-card p-4 border border-warning-500/30 bg-warning-500/5">
        <div class="flex justify-between items-center">
          <div class="flex items-center gap-3">
             <div class="w-10 h-10 rounded-xl bg-warning-500/20 flex items-center justify-center text-warning-400">
               ⚠️
             </div>
             <div>
               <h3 class="font-bold text-white">Pengeluaran Tak Terduga</h3>
               <p class="text-xs text-white/50">Transaksi di luar kategori budget</p>
             </div>
          </div>
          <p class="text-lg font-bold text-expense-400">-Rp {{ formatCurrency(summary.totalUnbudgetedSpent) }}</p>
        </div>
      </div>
      
      <!-- Link to Full History -->
      <div class="flex justify-center pt-8 pb-4">
        <router-link
          to="/transactions"
          class="px-5 py-2 rounded-xl text-sm font-medium text-white/60 hover:text-white hover:bg-white/10 transition-all duration-200"
        >
          Lihat Semua Riwayat Transaksi &rarr;
        </router-link>
      </div>
    </div>

    <!-- Transaction Modal (Create Only) -->
    <BaseModal
      v-model="showTxModal"
      title="Catat Transaksi"
      @close="resetTxForm"
    >
      <form @submit.prevent="submitTransaction" class="space-y-4">
        <!-- Validation errors handled by browser or logic -->
        
        <!-- Type -->
        <div class="flex gap-3">
            <button type="button" @click="txForm.type = 'EXPENSE'; txForm.categoryId = ''" 
              :class="['flex-1 py-2 rounded-lg border transition-colors', txForm.type === 'EXPENSE' ? 'bg-expense-500/20 border-expense-400 text-expense-300' : 'border-white/10 text-white/50']">
              Pengeluaran
            </button>
            <button type="button" @click="txForm.type = 'INCOME'; txForm.categoryId = ''" 
              :class="['flex-1 py-2 rounded-lg border transition-colors', txForm.type === 'INCOME' ? 'bg-income-500/20 border-income-400 text-income-300' : 'border-white/10 text-white/50']">
              Pemasukan
            </button>
        </div>

        <BaseSelect v-model="txForm.categoryId" label="Kategori" :options="categoryOptions" required />
        <BaseInput v-model="txForm.amount" label="Jumlah" type="number" required />
        <BaseDatePicker :month="Number(txForm.month)" :year="Number(txForm.year)" label="Tanggal" required 
           @update:date="txForm.date = $event" />
        
        <div>
          <label class="block text-sm font-medium text-white/80 mb-2">Catatan</label>
          <textarea v-model="txForm.description" rows="2" class="glass-input w-full resize-none"></textarea>
        </div>

        <div class="flex gap-3 pt-2">
          <BaseButton type="button" variant="secondary" class="flex-1" @click="showTxModal = false">Batal</BaseButton>
          <BaseButton type="submit" variant="primary" class="flex-1" :loading="submitting">Simpan</BaseButton>
        </div>
      </form>
    </BaseModal>

    <!-- Budget Modal (Create/Edit) -->
    <BaseModal
      v-model="showBudgetModal"
      :title="editingBudgetId ? 'Edit Budget' : 'Buat Budget'"
      @close="resetBudgetForm"
    >
      <form @submit.prevent="submitBudget" class="space-y-4">
        <BaseSelect 
          v-model="budgetForm.categoryId" 
          label="Kategori Pengeluaran" 
          :options="expenseCategoryOptions" 
          required 
          :disabled="!!editingBudgetId" 
        />
        
        <BaseInput 
          v-model="budgetForm.monthlyLimit" 
          label="Batas Budget (Limit)" 
          type="number" 
          placeholder="Contoh: 500000" 
          required 
        />
        
        <div class="flex gap-3 pt-2">
          <BaseButton type="button" variant="secondary" class="flex-1" @click="showBudgetModal = false">Batal</BaseButton>
          <BaseButton type="submit" variant="primary" class="flex-1" :loading="submitting">Simpan</BaseButton>
        </div>
      </form>
    </BaseModal>

    <!-- Delete Budget Confirm -->
    <BaseModal v-model="showDeleteBudgetModal" title="Hapus Budget" @close="deletingBudget = null">
      <div class="text-center py-4">
        <p class="text-white/80 mb-4">Hapus budget untuk <span class="font-bold text-white">{{ deletingBudget?.categoryName }}</span>?</p>
        <p class="text-white/50 text-sm mb-6">Transaksi tidak akan terhapus, tapi pengeluaran kategori ini akan dianggap "Tak Terduga".</p>
        <div class="flex gap-3">
          <BaseButton variant="secondary" class="flex-1" @click="showDeleteBudgetModal = false">Batal</BaseButton>
          <BaseButton variant="danger" class="flex-1" @click="handleDeleteBudget">Hapus</BaseButton>
        </div>
      </div>
    </BaseModal>

  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { transactionApi, type TransactionRequest } from '@/api/transaction.api'
import { budgetApi, type Budget, type BudgetSummary, type BudgetRequest } from '@/api/budget.api'
import { categoryApi, type Category } from '@/api/category.api'
import { useNotification } from '@/composables/useNotification'
import { BaseButton, BaseInput, BaseSelect, BaseModal, BaseLoading, BaseDatePicker } from '@/components/base'

const notification = useNotification()
const now = new Date()

// State
const loading = ref(false)
const submitting = ref(false)

// Data
const budgets = ref<Budget[]>([])
const summary = ref<BudgetSummary>({
  totalIncome: 0,
  totalAllocated: 0,
  totalUnbudgetedSpent: 0,
  totalSpent: 0,
  netBalance: 0,
  month: now.getMonth() + 1,
  year: now.getFullYear()
})
const categories = ref<Category[]>([])

// Filters
const filterMonth = ref(now.getMonth() + 1)
const filterYear = ref(now.getFullYear())

// Modals
const showTxModal = ref(false)
const showBudgetModal = ref(false)
const showDeleteBudgetModal = ref(false)
const editingBudgetId = ref<number | null>(null)
const deletingBudget = ref<Budget | null>(null)

// Forms
const txForm = ref({
  type: 'EXPENSE' as 'INCOME' | 'EXPENSE',
  categoryId: '',
  amount: '',
  date: null as string | null,
  month: (now.getMonth() + 1).toString(),
  year: now.getFullYear().toString(),
  description: ''
})

const budgetForm = ref({
  categoryId: '',
  monthlyLimit: ''
})

// Options
const months = [
  { value: 1, label: 'Januari' }, { value: 2, label: 'Februari' }, { value: 3, label: 'Maret' },
  { value: 4, label: 'April' }, { value: 5, label: 'Mei' }, { value: 6, label: 'Juni' },
  { value: 7, label: 'Juli' }, { value: 8, label: 'Agustus' }, { value: 9, label: 'September' },
  { value: 10, label: 'Oktober' }, { value: 11, label: 'November' }, { value: 12, label: 'Desember' },
]
const years = Array.from({ length: 6 }, (_, i) => now.getFullYear() - i)

const categoryOptions = computed(() => 
  categories.value
    .filter(c => c.type === txForm.value.type)
    .map(c => ({ value: c.id.toString(), label: c.name }))
)

const expenseCategoryOptions = computed(() => 
  categories.value
    .filter(c => c.type === 'EXPENSE')
    .map(c => ({ value: c.id.toString(), label: c.name }))
)

// Helpers
const formatCurrency = (v: number) => new Intl.NumberFormat('id-ID').format(Math.round(v ?? 0))
const getProgressColor = (pct: number) => {
  if (pct >= 100) return 'bg-expense-500' // Red
  if (pct >= 80) return 'bg-warning-500' // Yellow
  return 'bg-income-500' // Green
}

// Fetch Data
const fetchData = async () => {
  loading.value = true
  try {
    const [budgetRes, summaryRes] = await Promise.all([
      budgetApi.getAll(filterMonth.value, filterYear.value),
      budgetApi.getSummary(filterMonth.value, filterYear.value)
    ])
    budgets.value = budgetRes.data
    summary.value = summaryRes.data
  } catch {
    notification.showError('Gagal memuat data')
  } finally {
    loading.value = false
  }
}

const fetchCategories = async () => {
  try {
    const res = await categoryApi.getAll()
    categories.value = res.data
  } catch {}
}

// Transaction Logic
const openTransactionModal = () => {
  resetTxForm()
  showTxModal.value = true
}

const resetTxForm = () => {
  txForm.value = {
    type: 'EXPENSE',
    categoryId: '',
    amount: '',
    date: new Date().toISOString().substring(0, 10), // Default today
    month: (now.getMonth() + 1).toString(),
    year: now.getFullYear().toString(),
    description: ''
  }
}

const submitTransaction = async () => {
  if (!txForm.value.categoryId || !txForm.value.amount || !txForm.value.date) return
  
  submitting.value = true
  try {
    const payload: TransactionRequest = {
      categoryId: Number(txForm.value.categoryId),
      amount: Number(txForm.value.amount),
      transactionDate: txForm.value.date!,
      description: txForm.value.description || undefined,
      type: txForm.value.type
    }
    await transactionApi.create(payload)
    notification.showSuccess('Transaksi dicatat')
    showTxModal.value = false
    fetchData() // Refresh budgets & summary
  } catch (e: any) {
    notification.showError(e.response?.data?.message || 'Gagal menyimpan')
  } finally {
    submitting.value = false
  }
}

// Budget Logic
const openBudgetModal = () => {
  resetBudgetForm()
  showBudgetModal.value = true
}

const editBudget = (b: Budget) => {
  editingBudgetId.value = b.id
  budgetForm.value = {
    categoryId: b.categoryId.toString(),
    monthlyLimit: b.monthlyLimit.toString()
  }
  showBudgetModal.value = true
}

const resetBudgetForm = () => {
  editingBudgetId.value = null
  budgetForm.value = { categoryId: '', monthlyLimit: '' }
}

const submitBudget = async () => {
  if (!budgetForm.value.categoryId || !budgetForm.value.monthlyLimit) return
  
  submitting.value = true
  try {
    const payload: BudgetRequest = {
      categoryId: Number(budgetForm.value.categoryId),
      monthlyLimit: Number(budgetForm.value.monthlyLimit),
      month: filterMonth.value,
      year: filterYear.value,
      startDate: `${filterYear.value}-${String(filterMonth.value).padStart(2, '0')}-01`
    }
    
    if (editingBudgetId.value) {
      await budgetApi.update(editingBudgetId.value, payload)
      notification.showSuccess('Budget diupdate')
    } else {
      await budgetApi.create(payload)
      notification.showSuccess('Budget dibuat')
    }
    showBudgetModal.value = false
    fetchData()
  } catch (e: any) {
    notification.showError(e.response?.data?.message || 'Gagal menyimpan budget')
  } finally {
    submitting.value = false
  }
}

const confirmDeleteBudget = (b: Budget) => {
  deletingBudget.value = b
  showDeleteBudgetModal.value = true
}

const handleDeleteBudget = async () => {
  if (!deletingBudget.value) return
  loading.value = true // use main loading or creating a deleting state
  try {
    await budgetApi.delete(deletingBudget.value.id)
    notification.showSuccess('Budget dihapus')
    showDeleteBudgetModal.value = false
    fetchData()
  } catch {
    notification.showError('Gagal menghapus budget')
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  fetchData()
  fetchCategories()
})
</script>
