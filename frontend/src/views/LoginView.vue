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
      <!-- Header -->
      <div class="card-header">
        <div class="brand-icon">
          <span class="material-symbols-outlined icon-main">headset_mic</span>
        </div>
        <h1 class="login-title">Call Monitoring</h1>
        <p class="login-subtitle">Sign in to access the supervisor dashboard</p>
      </div>

      <!-- Form -->
      <form class="login-form" @submit="onSubmit" novalidate>
        <div class="form-group">
          <label for="username" class="form-label">Username</label>
          <div class="input-container">
            <span class="material-symbols-outlined input-icon">person</span>
            <InputText
              id="username"
              v-model="username"
              name="username"
              placeholder="Enter your username"
              class="custom-input"
            />
          </div>
          <small v-if="usernameError" class="error-text">{{ usernameError }}</small>
        </div>

        <div class="form-group">
          <label for="password" class="form-label">Password</label>
          <div class="input-container">
            <span class="material-symbols-outlined input-icon">lock</span>
            <InputText
              id="password"
              v-model="password"
              name="password"
              type="password"
              placeholder="Enter your password"
              class="custom-input"
            />
          </div>
          <div class="forgot-row">
            <small v-if="passwordError" class="error-text">{{ passwordError }}</small>
            <a href="#" class="forgot-link" @click.prevent>Forgot password?</a>
          </div>
        </div>

        <Message v-if="loginError" severity="error" class="login-error">{{ loginError }}</Message>

        <Button
          type="submit"
          label="Sign in"
          class="submit-btn"
          :loading="submitting"
        />
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
  background-color: var(--surface-bg, #F8FAFC);
  padding: 1rem;
}

.login-card {
  width: 100%;
  max-width: 28rem;
  background-color: var(--surface-card, #ffffff);
  border-radius: var(--radius-md, 0.5rem);
  border: 1px solid var(--surface-border, #E2E8F0);
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.05), 0 1px 2px rgba(0, 0, 0, 0.05);
  padding: 2rem;
  display: flex;
  flex-direction: column;
  gap: 2rem;
}

.card-header {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  text-align: center;
}

.brand-icon {
  width: 4rem;
  height: 4rem;
  background-color: var(--primary, #e11d48);
  border-radius: 9999px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #ffffff;
  box-shadow: 0 1px 2px rgba(0, 0, 0, 0.05);
  margin-bottom: 1rem;
}

.icon-main {
  font-size: 2.25rem !important;
}

.login-title {
  margin: 0;
  font-size: 1.875rem;
  line-height: 2.25rem;
  font-weight: 700;
  letter-spacing: -0.02em;
  color: var(--text-main, #0F172A);
}

.login-subtitle {
  margin: 0.5rem 0 0;
  font-size: 0.875rem;
  line-height: 1.25rem;
  color: var(--text-muted, #64748B);
}

.login-form {
  display: flex;
  flex-direction: column;
  gap: 1.25rem;
}

.form-group {
  display: flex;
  flex-direction: column;
  gap: 0.375rem;
}

.form-label {
  font-size: 0.875rem;
  font-weight: 600;
  color: var(--text-main, #0F172A);
}

.input-container {
  position: relative;
  width: 100%;
}

.input-icon {
  position: absolute;
  left: 0.75rem;
  top: 50%;
  transform: translateY(-50%);
  color: var(--text-muted, #64748B);
  font-size: 1.25rem !important;
  pointer-events: none;
  z-index: 2;
}

.custom-input {
  width: 100%;
  padding-left: 2.5rem !important;
  padding-right: 1rem !important;
  padding-top: 0.625rem !important;
  padding-bottom: 0.625rem !important;
  border: 1px solid var(--surface-border, #E2E8F0) !important;
  border-radius: var(--radius-sm, 0.25rem) !important;
  font-size: 0.875rem !important;
  color: var(--text-main, #0F172A) !important;
  background-color: var(--surface-card, #ffffff) !important;
}

.forgot-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-top: 0.25rem;
}

.forgot-link {
  font-size: 0.75rem;
  font-weight: 500;
  color: var(--primary-brand, #b80035);
  text-decoration: none;
  margin-left: auto;
}

.forgot-link:hover {
  text-decoration: underline;
}

.error-text {
  color: var(--sentiment-neg, #e11d48);
  font-size: 0.75rem;
  display: block;
}

.login-error {
  margin: 0;
}

.submit-btn {
  width: 100%;
  padding: 0.75rem 1rem !important;
  background-color: var(--primary, #E11D48) !important;
  border: none !important;
  color: #ffffff !important;
  border-radius: var(--radius-sm, 0.25rem) !important;
  font-size: 0.875rem !important;
  font-weight: 600 !important;
  letter-spacing: 0.01em;
  box-shadow: 0 1px 2px rgba(0, 0, 0, 0.05);
  transition: background-color 0.15s ease;
  margin-top: 0.5rem;
}

.submit-btn:hover {
  background-color: var(--primary-hover, #be183d) !important;
}
</style>