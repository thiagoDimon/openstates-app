import axios from 'axios'

const api = axios.create({
  baseURL: import.meta.env.VITE_API_URL ?? 'http://localhost:8080/api',
})

api.interceptors.response.use(
  response => response,
  error => {
    const backendMessage: string | undefined = error.response?.data?.message
    if (backendMessage) {
      error.message = backendMessage
    } else if (!error.response) {
      error.message = 'Server is unavailable. Please check your connection and try again.'
    }
    return Promise.reject(error)
  }
)

export default api
