<template>
  <div class="min-h-screen flex items-center justify-center p-4 py-12">
    <div class="w-full max-w-md">
      <!-- Register Card -->
      <div class="glass rounded-3xl p-8 shadow-2xl">
        <!-- Header -->
        <div class="text-center mb-8">
          <div class="w-16 h-16 mx-auto mb-4 rounded-2xl bg-gradient-to-br from-primary-500 to-primary-600 flex items-center justify-center shadow-xl">
            <svg class="w-8 h-8 text-white" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M18 9v3m0 0v3m0-3h3m-3 0h-3m-2-5a4 4 0 11-8 0 4 4 0 018 0zM3 20a6 6 0 0112 0v1H3v-1z" />
            </svg>
          </div>
          <h1 class="text-3xl font-bold text-white mb-2">Buat Akun Baru</h1>
          <p class="text-white/70">Mulai kelola keuangan Anda dengan lebih baik</p>
        </div>

        <!-- Register Form -->
        <form @submit.prevent="handleRegister" class="space-y-4">
          <!-- Full Name Input -->
          <BaseInput
            v-model="form.fullName"
            type="text"
            label="Nama Lengkap"
            placeholder="Masukkan nama lengkap"
            :required="true"
            :error="errors.fullName"
          />

          <!-- Email Input -->
          <BaseInput
            v-model="form.email"
            type="email"
            label="Email"
            placeholder="nama@email.com"
            :required="true"
            :error="errors.email"
          />

          <!-- Password Input -->
          <BaseInput
            v-model="form.password"
            type="password"
            label="Password"
            placeholder="Minimal 6 karakter"
            :required="true"
            :error="errors.password"
            hint="Gunakan kombinasi huruf, angka, dan simbol"
          />

          <!-- Confirm Password Input -->
          <BaseInput
            v-model="form.confirmPassword"
            type="password"
            label="Konfirmasi Password"
            placeholder="Masukkan ulang password"
            :required="true"
            :error="errors.confirmPassword"
          />

          <!-- Terms Checkbox -->
          <div class="flex items-start gap-3">
            <input
              id="terms"
              v-model="form.acceptTerms"
              type="checkbox"
              class="mt-1 w-4 h-4 rounded border-white/20 bg-white/10 text-primary-500 focus:ring-primary-500"
            />
            <label for="terms" class="text-sm text-white/80">
              Saya setuju dengan
              <a href="#" class="text-primary-400 hover:text-primary-300">Syarat & Ketentuan</a>
              dan
              <a href="#" class="text-primary-400 hover:text-primary-300">Kebijakan Privasi</a>
            </label>
          </div>
          <p v-if="errors.acceptTerms" class="text-sm text-red-400">{{ errors.acceptTerms }}</p>

          <!-- Submit Button -->
          <!-- Submit Button -->
          <button
            type="submit"
            class="w-full btn-glass flex justify-center items-center"
            :disabled="loading"
          >
            <span v-if="loading" class="mr-2">
              <svg class="animate-spin h-5 w-5" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24">
                <circle class="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" stroke-width="4"></circle>
                <path class="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"></path>
              </svg>
            </span>
            Daftar Sekarang
          </button>
        </form>

        <!-- Divider -->
        <div class="relative my-6">
          <div class="absolute inset-0 flex items-center">
            <div class="w-full border-t border-white/10"></div>
          </div>
          <div class="relative flex justify-center text-sm">
            <span class="px-4 bg-transparent text-white/50">atau</span>
          </div>
        </div>

        <!-- Login Link -->
        <div class="text-center">
          <p class="text-white/70">
            Sudah punya akun?
            <RouterLink to="/login" class="text-primary-400 hover:text-primary-300 font-semibold transition-colors">
              Masuk
            </RouterLink>
          </p>
        </div>
      </div>

      <!-- Back to Home -->
      <div class="text-center mt-6">
        <RouterLink to="/" class="text-white/60 hover:text-white transition-colors text-sm">
          ← Kembali ke Beranda
        </RouterLink>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive } from 'vue'
import { useRouter, RouterLink } from 'vue-router'
import { useAuthStore } from '@/stores/auth'
import { useNotification } from '@/composables/useNotification'
import { BaseInput, BaseButton } from '@/components/base'

const router = useRouter()
const authStore = useAuthStore()
const notification = useNotification()

const loading = ref(false)
const form = reactive({
  fullName: '',
  email: '',
  password: '',
  confirmPassword: '',
  acceptTerms: false,
})

const errors = reactive({
  fullName: '',
  email: '',
  password: '',
  confirmPassword: '',
  acceptTerms: '',
})

const validateForm = () => {
  // Reset errors
  errors.fullName = ''
  errors.email = ''
  errors.password = ''
  errors.confirmPassword = ''
  errors.acceptTerms = ''

  let isValid = true

  // Validate full name
  if (!form.fullName) {
    errors.fullName = 'Nama lengkap harus diisi'
    isValid = false
  } else if (form.fullName.length < 3) {
    errors.fullName = 'Nama minimal 3 karakter'
    isValid = false
  }

  // Validate email
  if (!form.email) {
    errors.email = 'Email harus diisi'
    isValid = false
  } else if (!/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(form.email)) {
    errors.email = 'Format email tidak valid'
    isValid = false
  }

  // Validate password
  if (!form.password) {
    errors.password = 'Password harus diisi'
    isValid = false
  } else if (form.password.length < 6) {
    errors.password = 'Password minimal 6 karakter'
    isValid = false
  }

  // Validate confirm password
  if (!form.confirmPassword) {
    errors.confirmPassword = 'Konfirmasi password harus diisi'
    isValid = false
  } else if (form.password !== form.confirmPassword) {
    errors.confirmPassword = 'Password tidak cocok'
    isValid = false
  }

  // Validate terms
  if (!form.acceptTerms) {
    errors.acceptTerms = 'Anda harus menyetujui syarat & ketentuan'
    isValid = false
  }

  return isValid
}

const handleRegister = async () => {
  if (!validateForm()) return

  loading.value = true

  try {
    await authStore.register({
      fullName: form.fullName,
      email: form.email,
      password: form.password,
    })

    notification.showSuccess('Registrasi berhasil! Selamat datang di MyFinTrack.')
    router.push('/dashboard')
  } catch (error: any) {
    const message = error.response?.data?.message || 'Registrasi gagal. Silakan coba lagi.'
    notification.showError(message)
  } finally {
    loading.value = false
  }
}
</script>
