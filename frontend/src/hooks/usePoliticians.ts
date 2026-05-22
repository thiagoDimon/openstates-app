import { useQuery } from '@tanstack/react-query'
import { fetchPoliticians } from '@/services/politicians.service'

export function usePoliticians(state?: string, party?: string) {
  return useQuery({
    queryKey: ['politicians', state, party],
    queryFn: () => fetchPoliticians(state, party),
    enabled: !!state,
    retry: 1,
  })
}
