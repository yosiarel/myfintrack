import axios from './axios'

export interface LoginRequest {
  email: string
  password: string
}

export interface RegisterRequest {
  email: string
  password: string
  fullName: string
}

export interface AuthResponse {
  success: boolean
  message: string
  data: {
    accessToken: string
    refreshToken: string
    type: string
    userId: number
    email: string
    fullName: string
  }
  timestamp: string
}

export const authApi = {
  register(data: RegisterRequest): Promise<AuthResponse> {
    return axios.post('/api/auth/register', data).then((res) => res.data)
  },

  login(data: LoginRequest): Promise<AuthResponse> {
    return axios.post('/api/auth/login', data).then((res) => res.data)
  },

}
