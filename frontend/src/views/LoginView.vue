<script setup>
import { ref } from 'vue'
import { useForm, useField } from 'vee-validate'
import { toFormValidator } from '@vee-validate/zod'
import { useRouter } from 'vue-router'
import InputText from 'primevue/inputtext'
import Button from 'primevue/button'
import Message from 'primevue/message'
import { loginSchema, loginFormValues } from '../schemas/loginSchema'
import { useAuthStore } from '../stores/auth'

const router = useRouter()
const auth = useAuthStore()

const { handleSubmit } = useForm({
  validationSchema: toFormValidator(loginSchema),
  initialValues: loginFormValues.initialValues
})

const { value: username, errorMessage: usernameError } = useField('username')
const { value: password, errorMessage: passwordError } = useField('password')

const loginError = ref('')
const submitting = ref(false)

const onSubmit = handleSubmit(async (values) => {
  submitting.value = true
  loginError.value = ''
  try {
    await auth.login(values.username, values.password)
    router.push({ name: 'monitoring' })
  } catch {
    loginError.value = 'Invalid username or password'
  } finally {
    submitting.value = false
  }
})
</script>

<template>
  <div class="login-page">
    <div class="login-card">
      <h1 class="login-title">Call Monitoring</h1>
      <p class="login-subtitle">Sign in to continue</p>

      <form @submit="onSubmit" novalidate>
        <div class="field">
          <label for="username">Username</label>
          <InputText id="username" v-model="username" class="w-full" />
          <small class="error">{{ usernameError }}</small>
        </div>

        <div class="field">
          <label for="password">Password</label>
          <InputText id="password" v-model="password" type="password" class="w-full" />
          <small class="error">{{ passwordError }}</small>
        </div>

        <Message v-if="loginError" severity="error" class="login-error">{{ loginError }}</Message>

        <Button type="submit" label="Sign in" class="w-full" :loading="submitting" />
      </form>
    </div>
  </div>
</template>

<style scoped>
.login-page {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: #f4f6fa;
}

.login-card {
  width: 24rem;
  background: #fff;
  border-radius: 0.75rem;
  box-shadow: 0 8px 30px rgba(0, 0, 0, 0.08);
  padding: 2.5rem;
}

.login-title {
  margin: 0 0 0.25rem;
  font-size: 1.5rem;
}

.login-subtitle {
  margin: 0 0 1.75rem;
  color: #6b7280;
}

.field {
  margin-bottom: 1.25rem;
}

.field label {
  display: block;
  margin-bottom: 0.4rem;
  font-size: 0.875rem;
}

.error {
  color: #dc2626;
  font-size: 0.75rem;
}

.login-error {
  margin-bottom: 1rem;
}
</style>