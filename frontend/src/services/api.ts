import axios from 'axios'

const api = axios.create({
  baseURL: import.meta.env.VITE_API_URL ?? 'http://localhost:8080',
})

api.interceptors.response.use(
  response => response,
  error => {
    if (error.response?.status === 401) {
      console.error('[API] Unauthorized')
    } else if (!error.response) {
      console.error('[API] Network error — server may be unavailable')
    }
    return Promise.reject(error)
  }
)

export default api
