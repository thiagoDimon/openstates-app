import { useEffect, useRef } from 'react'
import { Box, CircularProgress, Grid, Typography } from '@mui/material'
import type { Politician } from '@/types/politician'
import { PoliticianCard } from '@/components/PoliticianCard'

interface PoliticianGridProps {
  politicians: Politician[]
  isError: boolean
  errorMessage?: string
  onLoadMore: () => void
  hasMore: boolean
  isLoadingMore: boolean
}

export function PoliticianGrid({
  politicians,
  isError,
  errorMessage,
  onLoadMore,
  hasMore,
  isLoadingMore,
}: PoliticianGridProps) {
  const sentinelRef = useRef<HTMLDivElement>(null)

  useEffect(() => {
    const sentinel = sentinelRef.current
    if (!sentinel) return

    const observer = new IntersectionObserver(
      (entries) => {
        if (entries[0].isIntersecting && hasMore && !isLoadingMore) {
          onLoadMore()
        }
      },
      { threshold: 0.1 }
    )

    observer.observe(sentinel)
    return () => observer.disconnect()
  }, [hasMore, isLoadingMore, onLoadMore])

  if (isError) {
    return (
      <Typography color="error" sx={{ textAlign: 'center', py: 8 }}>
        {errorMessage ?? 'Failed to load politicians. Please try again.'}
      </Typography>
    )
  }

  if (politicians.length === 0) {
    return (
      <Typography color="text.secondary" sx={{ textAlign: 'center', py: 8 }}>
        No politicians found for the selected filters.
      </Typography>
    )
  }

  return (
    <Box>
      <Grid container spacing={3}>
        {politicians.map(politician => (
          <Grid key={politician.id} size={{ xs: 12, sm: 6, md: 4, lg: 3 }}>
            <PoliticianCard politician={politician} />
          </Grid>
        ))}
      </Grid>

      <div ref={sentinelRef} style={{ height: 1 }} />

      {isLoadingMore && (
        <Box sx={{ display: 'flex', justifyContent: 'center', py: 4 }}>
          <CircularProgress />
        </Box>
      )}

      {!hasMore && politicians.length > 0 && (
        <Typography color="text.secondary" sx={{ textAlign: 'center', py: 4 }}>
          No more politicians to load.
        </Typography>
      )}
    </Box>
  )
}
