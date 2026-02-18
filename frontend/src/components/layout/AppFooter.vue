<template>
  <footer class="mt-auto border-t border-white/10 bg-black/20 backdrop-blur-md">
    <div class="container mx-auto px-6 py-8">
      <div class="flex flex-col md:flex-row justify-between items-center md:items-center gap-6">
        
        <div class="flex items-center gap-3">
          <div class="w-10 h-10 rounded-xl bg-gradient-to-br from-primary-400 to-primary-600 flex items-center justify-center text-white font-bold shadow-lg">
            <img src="/logo.svg" alt="Logo" class="w-6 h-6" fetchpriority="high" />
          </div>
          <div class="flex flex-col">
            <span class="text-xl font-bold bg-clip-text text-transparent bg-gradient-to-r from-white to-white/70">
              FinTrack
            </span>
            <span class="text-white/40 text-[10px] tracking-wider uppercase">Financial Manager</span>
          </div>
        </div>

           <div class="text-white/30 text-xs text-center md:text-right">
             &copy; {{ new Date().getFullYear() }} FinTrack. All rights reserved.
           </div>
        </div>

      </div>
  </footer>
</template>

<script setup lang="ts">
import { RouterLink } from 'vue-router'
</script>
