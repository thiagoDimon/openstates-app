import api from '@/services/api'
import type { Politician } from '@/types/politician'

export async function fetchPoliticians(state?: string, party?: string): Promise<Politician[]> {
  const params: Record<string, string> = {}
  if (state) params.state = state.toLowerCase()
  if (party) params.party = party
  const response = await api.get<Politician[]>('/politicians', { params })
  return response.data
}
