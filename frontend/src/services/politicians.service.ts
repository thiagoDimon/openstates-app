import api from '@/services/api'
import type { FilterOptions, Politician } from '@/types/politician'

export async function fetchPoliticians(state?: string, party?: string): Promise<Politician[]> {
  const response = await api.get<Politician[]>('/politicians', { params: { state, party } })
  return response.data
}

export async function fetchFilterOptions(): Promise<FilterOptions> {
  const response = await api.get<FilterOptions>('/politicians/filters')
  return response.data
}
