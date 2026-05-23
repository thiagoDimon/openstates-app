import { useMutation } from '@tanstack/react-query'
import { syncState } from '@/services/politicians.service'

export function useSyncState() {
  return useMutation({
    mutationFn: (stateCode: string) => syncState(stateCode),
  })
}
