import { useInfiniteQuery } from '@tanstack/react-query'
import { fetchPoliticians } from '@/services/politicians.service'

export function usePoliticians(state?: string, party?: string) {
  return useInfiniteQuery({
    queryKey: ['politicians', state, party],
    queryFn: ({ pageParam }) => fetchPoliticians(state, party, pageParam),
    initialPageParam: 0,
    getNextPageParam: (lastPage) => (lastPage.hasNext ? lastPage.page + 1 : undefined),
    enabled: !!state,
  })
}
