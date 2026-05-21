import { useQuery } from '@tanstack/react-query'
import { fetchFilterOptions, fetchPoliticians } from '@/services/politicians.service'

export function usePoliticians(state?: string, party?: string) {
  return useQuery({
    queryKey: ['politicians', state, party],
    queryFn: async () => await fetchPoliticians(state, party),
    retry: 1,
  })
}

export function useFilterOptions() {
  return useQuery({
    queryKey: ['filter-options'],
    queryFn: async () => await fetchFilterOptions(),
    staleTime: 5 * 60 * 1000,
    retry: 1,
  })
}
