<template>
  <div class="min-h-screen">
    <!-- Hero Section -->
    <section class="relative min-h-[calc(100vh-4rem)] flex items-center justify-center px-4 py-20">
      <div class="max-w-6xl mx-auto text-center">
        <!-- Main Heading with Typing Effect -->
        <div class="mb-8">
          <h1 class="text-5xl md:text-7xl font-bold text-white mb-4">
            Kelola Keuangan Anda
          </h1>
          <TextType
            :text="[
              'Kelola Keuangan dengan Mudah',
              'Pantau Pengeluaran Real-time',
              'Capai Target Budget Anda',
              'Finansial Lebih Terencana'
            ]"
            :typingSpeed="75"
            :pauseDuration="2000"
            :deletingSpeed="50"
            :showCursor="true"
            cursorCharacter="|"
            class="text-2xl md:text-4xl font-semibold text-gradient-primary min-h-[3rem]"
            :loop="true"
          />
        </div>

        <!-- Lottie Animation -->
        <div class="mb-12 flex justify-center">
          <DotLottieVue 
            style="height: 400px; width: 400px; max-width: 100%;" 
            autoplay 
            loop 
            src="https://lottie.host/4b554a03-b607-4117-9065-fcff8da4139b/ndgUpTxzZL.lottie" 
          />
        </div>

        <!-- CTA Button -->
        <div class="flex justify-center items-center mb-16">
          <RouterLink
            to="/register"
            class="glass-button-primary px-10 py-4 text-lg font-semibold rounded-xl"
          >
            Mulai Sekarang - Gratis!
          </RouterLink>
        </div>

        <!-- Features ScrollStack -->
         <div class="w-full max-w-6xl mx-auto p-0">
          <ScrollStack>
            <!-- Feature 1 -->
            <ScrollStackItem item-class-name="glass-card !bg-white/5 !border-white/10 flex flex-col items-center justify-center text-center h-[500px]">
              <div class="w-16 h-16 mx-auto mb-4 rounded-2xl bg-gradient-to-br from-income-500 to-income-600 flex items-center justify-center shadow-xl">
                <svg class="w-8 h-8 text-white" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M7 11l5-5m0 0l5 5m-5-5v12" />
                </svg>
              </div>
              <h3 class="text-2xl font-bold text-white mb-3">Lacak Pemasukan</h3>
              <p class="text-white/70 max-w-sm mx-auto">
                Catat semua sumber pendapatan Anda dengan kategori yang jelas dan terorganisir
              </p>
            </ScrollStackItem>

            <!-- Feature 2 -->
             <ScrollStackItem item-class-name="glass-card !bg-white/5 !border-white/10 flex flex-col items-center justify-center text-center h-[500px]">
              <div class="w-20 h-20 mx-auto mb-6 rounded-2xl bg-gradient-to-br from-primary-500 to-primary-600 flex items-center justify-center shadow-xl">
                <svg class="w-10 h-10 text-white" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 19v-6a2 2 0 00-2-2H5a2 2 0 00-2 2v6a2 2 0 002 2h2a2 2 0 002-2zm0 0V9a2 2 0 012-2h2a2 2 0 012 2v10m-6 0a2 2 0 002 2h2a2 2 0 002-2m0 0V5a2 2 0 012-2h2a2 2 0 012 2v14a2 2 0 01-2 2h-2a2 2 0 01-2-2z" />
                </svg>
              </div>
              <h3 class="text-3xl font-bold text-white mb-4">Analisis Lengkap</h3>
              <p class="text-white/70 max-w-lg mx-auto text-lg">
                Dashboard interaktif dengan grafik dan insight keuangan yang mudah dipahami
              </p>
            </ScrollStackItem>

            <!-- Feature 3 -->
             <ScrollStackItem item-class-name="glass-card !bg-white/5 !border-white/10 flex flex-col items-center justify-center text-center h-[500px]">
              <div class="w-20 h-20 mx-auto mb-6 rounded-2xl bg-gradient-to-br from-expense-500 to-expense-600 flex items-center justify-center shadow-xl">
                <svg class="w-10 h-10 text-white" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 12l2 2 4-4m6 2a9 9 0 11-18 0 9 9 0 0118 0z" />
                </svg>
              </div>
              <h3 class="text-3xl font-bold text-white mb-4">Atur Budget</h3>
              <p class="text-white/70 max-w-lg mx-auto text-lg">
                Tetapkan batas pengeluaran dan pantau progress budget Anda secara real-time
              </p>
            </ScrollStackItem>
          </ScrollStack>
         </div>
      </div>
    </section>

    <!-- Stats Section -->
    <section class="py-16 px-4">
      <div class="max-w-6xl mx-auto">
        <StarBorder 
          as="div" 
          color="rgba(99, 102, 241, 0.6)" 
          speed="8s"
          custom-class="w-full"
        >
          <div class="p-8 md:p-12 ">
            <div class="grid grid-cols-1 md:grid-cols-3 gap-8 text-center">
              <div>
                <div class="text-4xl md:text-5xl font-bold text-gradient-primary mb-2">
                  <CountUp :to="100"  :duration="0.9" class-name="inline-block" />
                  <span>+</span>
                </div>
                <p class="text-white/70">Pengguna Aktif</p>
              </div>
              <div>
                <div class="text-4xl md:text-5xl font-bold text-gradient-income mb-2">
                  <CountUp :to="999" :duration="0.8" class-name="inline-block" />
                  <span>+</span>
                </div>
                <p class="text-white/70">Transaksi Tercatat</p>
              </div>
              <div>
                <div class="text-4xl md:text-5xl font-bold text-gradient-expense mb-2">
                  <CountUp :to="99" :duration="1" class-name="inline-block" />
                  <span>%</span>
                </div>
                <p class="text-white/70">Kepuasan User</p>
              </div>
            </div>
          </div>
        </StarBorder>
      </div>
    </section>

    <!-- How It Works -->
    <section class="py-16 px-4">
      <div class="max-w-6xl mx-auto">
        <h2 class="text-3xl md:text-4xl font-bold text-white text-center mb-12">
          Cara Kerja
        </h2>
        
        <div class="grid grid-cols-1 md:grid-cols-4 gap-6">
          <div class="text-center">
            <div class="w-12 h-12 mx-auto mb-4 rounded-full bg-primary-500 text-white flex items-center justify-center font-bold text-xl">
              1
            </div>
            <h3 class="text-lg font-semibold text-white mb-2">Daftar Akun</h3>
            <p class="text-white/70 text-sm">Buat akun gratis dalam hitungan detik</p>
          </div>

          <div class="text-center">
            <div class="w-12 h-12 mx-auto mb-4 rounded-full bg-primary-500 text-white flex items-center justify-center font-bold text-xl">
              2
            </div>
            <h3 class="text-lg font-semibold text-white mb-2">Catat Transaksi</h3>
            <p class="text-white/70 text-sm">Input pemasukan dan pengeluaran Anda</p>
          </div>

          <div class="text-center">
            <div class="w-12 h-12 mx-auto mb-4 rounded-full bg-primary-500 text-white flex items-center justify-center font-bold text-xl">
              3
            </div>
            <h3 class="text-lg font-semibold text-white mb-2">Atur Budget</h3>
            <p class="text-white/70 text-sm">Tetapkan target dan batasan budget</p>
          </div>

          <div class="text-center">
            <div class="w-12 h-12 mx-auto mb-4 rounded-full bg-primary-500 text-white flex items-center justify-center font-bold text-xl">
              4
            </div>
            <h3 class="text-lg font-semibold text-white mb-2">Pantau Progress</h3>
            <p class="text-white/70 text-sm">Lihat insight dan capai tujuan finansial</p>
          </div>
        </div>
      </div>
    </section>

    <!-- Final CTA -->
    <section class="py-20 px-4">
      <div class="max-w-4xl mx-auto text-center">
        <GlassSurface
          width="100%"
          height="auto"
          :border-radius="24"
          :brightness="15"
          :opacity="0.95"
          :blur="15"
          :background-opacity="0.05"
          :saturation="1.5"
          class-name="!p-12"
        >
          <div class="w-full">
            <h2 class="text-3xl md:text-4xl font-bold text-white mb-4">
              Siap Mengelola Keuangan Lebih Baik?
            </h2>
            <p class="text-white/80 mb-8 text-lg">
              Bergabunglah dengan ribuan pengguna yang sudah merasakan manfaatnya
            </p>
            <RouterLink
              to="/register"
              class="glass-button-primary inline-block px-10 py-4 text-lg font-semibold rounded-xl"
            >
              Mulai Sekarang - Gratis!
            </RouterLink>
          </div>
        </GlassSurface>
      </div>
    </section>
  </div>
</template>

<script setup lang="ts">
import { RouterLink } from 'vue-router'
import TextType from '@/components/TextType.vue'
import { DotLottieVue } from '@lottiefiles/dotlottie-vue'
import CountUp from '@/components/CountUp.vue'
import StarBorder from '@/components/StarBorder.vue'
import GlassSurface from '@/components/GlassSurface.vue'
import ScrollStack, { ScrollStackItem } from '@/components/base/ScrollStack.vue'
</script>

