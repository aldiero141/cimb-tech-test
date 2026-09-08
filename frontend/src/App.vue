<script setup>
import Toast from 'primevue/toast'
import { useToast } from 'primevue/usetoast'
import { setToastGetter } from './services/api'

const toast = useToast()
setToastGetter(() => toast)
</script>

<template>
  <Toast />
  <router-view />
</template>