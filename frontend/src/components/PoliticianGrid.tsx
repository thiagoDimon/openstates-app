import { Box, CircularProgress, Grid, Typography } from '@mui/material'
import type { Politician } from '@/types/politician'
import { PoliticianCard } from '@/components/PoliticianCard'

interface PoliticianGridProps {
  politicians: Politician[]
  isLoading: boolean
  isError: boolean
  errorMessage?: string
}

export function PoliticianGrid({ politicians, isLoading, isError, errorMessage }: PoliticianGridProps) {
  if (isLoading) {
    return (
      <Box sx={{ display: 'flex', justifyContent: 'center', py: 8 }}>
        <CircularProgress />
      </Box>
    )
  }

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
    <Grid container spacing={3}>
      {politicians.map(politician => (
        <Grid key={politician.id} size={{ xs: 12, sm: 6, md: 4, lg: 3 }}>
          <PoliticianCard politician={politician} />
        </Grid>
      ))}
    </Grid>
  )
}
