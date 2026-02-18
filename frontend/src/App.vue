<script setup lang="ts">
import { RouterView } from 'vue-router'
import Iridescence from './components/Iridescence.vue'
import AppNavbar from './components/layout/AppNavbar.vue'
import AppFooter from './components/layout/AppFooter.vue'
</script>

<template>
  <div class="fixed inset-0 z-0">
    <Iridescence 
      :color="[0.3, 0.4, 0.5]"
      :speed="0.2"
      :amplitude="0.15"
      :mouseReact="false"
    />
  </div>

  <div class="relative z-10 min-h-screen flex flex-col">
    <AppNavbar />
  
    <main class="pt-16 flex-1">
      <RouterView />
    </main>

    <AppFooter />
  </div>
</template>

<style>
body {
  background: transparent !important;
  min-height: 100vh;
  overflow-x: hidden;
}

body::before {
  display: none !important;
}
</style>
