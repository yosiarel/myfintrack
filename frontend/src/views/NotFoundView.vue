<template>
  <div class="min-h-screen flex items-center justify-center p-4">
    <div class="glass rounded-3xl p-8 text-center max-w-md w-full">
      <div class="text-6xl mb-4">404</div>
      <h1 class="text-3xl font-bold text-white mb-4">Page Not Found</h1>
      <p class="text-white/70 mb-8">
        The page you're looking for doesn't exist or has been moved.
      </p>
      <RouterLink to="/" class="glass-button-primary inline-block px-6 py-3 rounded-xl">
        Go Home
      </RouterLink>
    </div>
  </div>
</template>

<script setup lang="ts">
import { RouterLink } from 'vue-router'
</script>
