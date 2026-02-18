<template>
  <div class="min-h-screen flex items-center justify-center p-4">
    <div class="w-full max-w-md">
      <!-- Login Card -->
      <div class="glass rounded-3xl p-8 shadow-2xl">
        <!-- Header -->
        
        <!-- Error Message -->
        <div v-if="errorMessage" class="mb-6 p-4 rounded-xl bg-red-600 text-white font-medium shadow-lg flex items-center animate-pulse">
            <svg class="w-6 h-6 mr-3 flex-shrink-0" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 9v2m0 4h.01m-6.938 4h13.856c1.54 0 2.502-1.667 1.732-3L13.732 4c-.77-1.333-2.694-1.333-3.464 0L3.34 16c-.77 1.333.192 3 1.732 3z" />
            </svg>
            <span>{{ errorMessage }}</span>
        </div>
        <div class="text-center mb-8">
          <div class="w-16 h-16 mx-auto mb-4 rounded-2xl bg-gradient-to-br from-primary-500 to-primary-600 flex items-center justify-center shadow-xl">
            <svg class="w-8 h-8 text-white" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M16 7a4 4 0 11-8 0 4 4 0 018 0zM12 14a7 7 0 00-7 7h14a7 7 0 00-7-7z" />
            </svg>
          </div>
          <h1 class="text-3xl font-bold text-white mb-2">Selamat Datang!</h1>
          <p class="text-white/70">Masuk ke akun MyFinTrack Anda</p>
        </div>

        <!-- Login Form -->
        <form @submit.prevent="handleLogin" class="space-y-4">
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
            placeholder="Masukkan password"
            :required="true"
            :error="errors.password"
          />

          <!-- Forgot Password Link Removed -->
          <!-- <div class="text-right">
            <a href="#" class="text-sm text-primary-400 hover:text-primary-300 transition-colors">
              Lupa password?
            </a>
          </div> -->

          <!-- Submit Button -->
          <!-- Buttons -->
          <div>
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
                Masuk
              </button>
          </div>
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

        <!-- Register Link -->
        <div class="text-center">
          <p class="text-white/70">
            Belum punya akun?
            <RouterLink to="/register" class="text-primary-400 hover:text-primary-300 font-semibold transition-colors">
              Daftar Sekarang
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
import { useRouter, useRoute, RouterLink } from 'vue-router'
import { useAuthStore } from '@/stores/auth'
import { useNotification } from '@/composables/useNotification'
import { BaseInput, BaseButton } from '@/components/base'

const router = useRouter()
const route = useRoute()
const authStore = useAuthStore()
const notification = useNotification()

const loading = ref(false)
const errorMessage = ref('')
const form = reactive({
  email: '',
  password: '',
})

const errors = reactive({
  email: '',
  password: '',
})

const validateForm = () => {
  errors.email = ''
  errors.password = ''
  errorMessage.value = ''

  if (!form.email) {
    errors.email = 'Email harus diisi'
    return false
  }

  if (!/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(form.email)) {
    errors.email = 'Format email tidak valid'
    return false
  }

  if (!form.password) {
    errors.password = 'Password harus diisi'
    return false
  }

  if (form.password.length < 6) {
    errors.password = 'Password minimal 6 karakter'
    return false
  }

  return true
}

const resetForm = () => {
    form.email = ''
    form.password = ''
    errorMessage.value = ''
    errors.email = ''
    errors.password = ''
}

const handleLogin = async () => {
  if (!validateForm()) return

  loading.value = true

  try {
    await authStore.login({
      email: form.email,
      password: form.password,
    })

    // notification.showSuccess('Login berhasil! Selamat datang kembali.') // Optional, user wanted less alerts?

    // Redirect to intended page or dashboard
    const redirect = (route.query.redirect as string) || '/dashboard'
    router.push(redirect)
  } catch (error: any) {
    console.error('Login Error:', error)
    let message = 'Login gagal. Periksa email dan password Anda.'
    
    if (error.response && error.response.data) {
        if (typeof error.response.data === 'string') {
            message = error.response.data
        } else if (error.response.data.message) {
            message = error.response.data.message
        }
    }
    
    errorMessage.value = message
  } finally {
    loading.value = false
  }
}
</script>
