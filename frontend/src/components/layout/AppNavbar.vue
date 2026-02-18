<template>
  <nav class="navbar">
    <div class="container mx-auto px-4">
      <div class="flex items-center justify-between h-16">
        <RouterLink to="/" class="flex items-center group">
          <img 
            src="/logo.svg" 
            alt="MyFinTrack Logo" 
            class="h-32 w-auto transition-transform duration-300 group-hover:scale-110"
            fetchpriority="high"
          />
        </RouterLink>
    
        <div v-if="isAuthenticated" class="hidden md:flex items-center gap-6">
          <RouterLink
            v-for="link in navLinks"
            :key="link.path"
            :to="link.path"
            class="nav-link"
            active-class="nav-link-active"
          >
            <component :is="link.icon" class="w-5 h-5" />
            <span>{{ link.label }}</span>
          </RouterLink>
        </div>
        <div class="flex items-center gap-3">
          <template v-if="isAuthenticated">
            <div class="flex items-center gap-3 px-3 py-1.5 glass rounded-xl border border-white/5">
              <div class="w-8 h-8 rounded-full bg-gradient-to-br from-primary-400 to-primary-600 flex items-center justify-center text-white font-semibold text-sm shadow-lg">
                {{ userInitials }}
              </div>
              <div class="hidden sm:flex flex-col">
                <span class="text-white font-medium text-sm leading-tight">{{ authStore.user?.fullName }}</span>
                <span class="text-white/50 text-[10px] leading-tight">{{ authStore.user?.email }}</span>
              </div>
            </div>

            <button 
              @click="handleLogout" 
              class="glass-button-icon text-red-400 hover:bg-red-500/20 hover:text-red-300 border border-white/5 hover:border-red-500/30 transition-all duration-300" 
              title="Logout"
            >
              <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M17 16l4-4m0 0l-4-4m4 4H7m6 4v1a3 3 0 01-3 3H6a3 3 0 01-3-3V7a3 3 0 013-3h4a3 3 0 013 3v1" />
              </svg>
            </button>
          </template>

          <template v-else>
            <RouterLink to="/login" class="glass-button-secondary text-sm px-4 py-2">
              Login
            </RouterLink>
            <RouterLink to="/register" class="glass-button-primary-ghost text-sm px-4 py-2">
              Get Started
            </RouterLink>
          </template>

          <button
            v-if="isAuthenticated"
            @click="toggleMobileMenu"
            class="md:hidden glass-button-icon"
          >
            <svg class="w-6 h-6" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M4 6h16M4 12h16M4 18h16" />
            </svg>
          </button>
        </div>
      </div>
    </div>

    <Transition name="mobile-menu">
      <div v-if="showMobileMenu && isAuthenticated" class="md:hidden border-t border-white/10">
        <div class="px-4 py-3 space-y-2">
          <RouterLink
            v-for="link in navLinks"
            :key="link.path"
            :to="link.path"
            @click="closeMobileMenu"
            class="mobile-nav-link"
            active-class="mobile-nav-link-active"
          >
            <component :is="link.icon" class="w-5 h-5" />
            <span>{{ link.label }}</span>
          </RouterLink>
        </div>
      </div>
    </Transition>
  </nav>
</template>

<script setup lang="ts">
import { ref, computed, h } from 'vue'
import { RouterLink, useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/auth'
import { useNotification } from '@/composables/useNotification'

const authStore = useAuthStore()
const router = useRouter()
const notification = useNotification()

const showMobileMenu = ref(false)

const DashboardIcon = () => h('svg', {
  class: 'w-5 h-5',
  fill: 'none',
  stroke: 'currentColor',
  viewBox: '0 0 24 24',
}, [
  h('path', {
    'stroke-linecap': 'round',
    'stroke-linejoin': 'round',
    'stroke-width': '2',
    d: 'M3 12l2-2m0 0l7-7 7 7M5 10v10a1 1 0 001 1h3m10-11l2 2m-2-2v10a1 1 0 01-1 1h-3m-6 0a1 1 0 001-1v-4a1 1 0 011-1h2a1 1 0 011 1v4a1 1 0 001 1m-6 0h6',
  }),
])

const TransactionsIcon = () => h('svg', {
  class: 'w-5 h-5',
  fill: 'none',
  stroke: 'currentColor',
  viewBox: '0 0 24 24',
}, [
  h('path', {
    'stroke-linecap': 'round',
    'stroke-linejoin': 'round',
    'stroke-width': '2',
    d: 'M9 5H7a2 2 0 00-2 2v12a2 2 0 002 2h10a2 2 0 002-2V7a2 2 0 00-2-2h-2M9 5a2 2 0 002 2h2a2 2 0 002-2M9 5a2 2 0 012-2h2a2 2 0 012 2',
  }),
])

const CatatIcon = () => h('svg', {
  class: 'w-5 h-5',
  fill: 'none',
  stroke: 'currentColor',
  viewBox: '0 0 24 24',
}, [
  h('path', {
    'stroke-linecap': 'round',
    'stroke-linejoin': 'round',
    'stroke-width': '2',
    d: 'M11 5H6a2 2 0 00-2 2v11a2 2 0 002 2h11a2 2 0 002-2v-5m-1.414-9.414a2 2 0 112.828 2.828L11.828 15H9v-2.828l8.586-8.586z',
  }),
])

const navLinks = [
  { path: '/dashboard', label: 'Beranda', icon: DashboardIcon },
  { path: '/transactions', label: 'Riwayat', icon: TransactionsIcon },
  { path: '/budgets', label: 'Catat Keuangan', icon: CatatIcon },
]

const isAuthenticated = computed(() => authStore.isAuthenticated)

const userInitials = computed(() => {
  const name = authStore.user?.fullName || 'U'
  return name
    .split(' ')
    .map((n) => n[0])
    .join('')
    .toUpperCase()
    .slice(0, 2)
})


const toggleMobileMenu = () => {
  showMobileMenu.value = !showMobileMenu.value
}

const closeMobileMenu = () => {
  showMobileMenu.value = false
}


const handleLogout = async () => {
  const result = await notification.showConfirm(
    'Are you sure you want to logout?',
    'You will be redirected to the login page.'
  )
  
  if (result.isConfirmed) {
    authStore.logout()
    router.push('/login')
    notification.showSuccess('Logged out successfully')
  }
}

if (typeof window !== 'undefined') {
  window.addEventListener('click', (e) => {
    const target = e.target as HTMLElement
    if (!target.closest('nav')) {
      showMobileMenu.value = false
    }
  })
}
</script>

<style scoped>
.navbar {
  @apply fixed top-0 left-0 right-0 z-50;
  background: rgba(255, 255, 255, 0.03);
  backdrop-filter: blur(20px);
  -webkit-backdrop-filter: blur(20px);
  border-bottom: 1px solid rgba(255, 255, 255, 0.08);
  box-shadow: 
    0 4px 24px rgba(0, 0, 0, 0.1),
    inset 0 1px 0 rgba(255, 255, 255, 0.05);
}

.nav-link {
  @apply flex items-center gap-2 px-4 py-2 rounded-lg text-white/70 hover:text-white hover:bg-white/10 transition-all duration-200;
}

.nav-link-active {
  @apply text-white bg-white/10;
}

.mobile-nav-link {
  @apply flex items-center gap-3 px-4 py-3 rounded-xl text-white/70 hover:text-white hover:bg-white/10 transition-all duration-200;
}

.mobile-nav-link-active {
  @apply text-white bg-white/10;
}

.glass-button-icon {
  @apply glass px-3 py-2 rounded-lg text-white hover:bg-white/10 transition-all duration-200;
}

.dropdown-item {
  @apply w-full flex items-center gap-3 px-4 py-2.5 text-white/80 hover:text-white hover:bg-white/10 rounded-lg transition-all duration-200 text-left;
}

.dropdown-enter-active,
.dropdown-leave-active {
  transition: all 0.2s ease;
}

.dropdown-enter-from,
.dropdown-leave-to {
  opacity: 0;
  transform: translateY(-10px);
}

.mobile-menu-enter-active,
.mobile-menu-leave-active {
  transition: all 0.3s ease;
}

.mobile-menu-enter-from,
.mobile-menu-leave-to {
  opacity: 0;
  max-height: 0;
}

.mobile-menu-enter-to,
.mobile-menu-leave-from {
  opacity: 1;
  max-height: 500px;
}
</style>
