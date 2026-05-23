import api from '@/services/api'
import type { PoliticianPage } from '@/types/politician'

export async function fetchPoliticians(state?: string, party?: string, page = 0): Promise<PoliticianPage> {
  const params: Record<string, string | number> = { page, size: 10 }
  if (state) params.state = state.toLowerCase()
  if (party) params.party = party
  const response = await api.get<PoliticianPage>('/politicians', { params })
  return response.data
}

export async function syncState(stateCode: string): Promise<void> {
  await api.post(`/politicians/sync/${stateCode.toLowerCase()}`)
}
